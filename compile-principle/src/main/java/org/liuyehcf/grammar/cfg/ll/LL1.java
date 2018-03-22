package org.liuyehcf.grammar.cfg.ll;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.cfg.AbstractCfgParser;
import org.liuyehcf.grammar.core.MorphemeType;
import org.liuyehcf.grammar.core.ParserException;
import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Production;
import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.core.definition.converter.GrammarConverterPipelineImpl;
import org.liuyehcf.grammar.core.definition.converter.LreElfGrammarConverter;
import org.liuyehcf.grammar.core.definition.converter.MergeGrammarConverter;
import org.liuyehcf.grammar.core.parse.Token;
import org.liuyehcf.grammar.utils.SetUtils;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;
import static org.liuyehcf.grammar.utils.AssertUtils.assertNotNull;


/**
 * LL1文法编译器
 */
public class LL1 extends AbstractCfgParser implements LLParser {

    // select集
    private Map<Symbol, Map<PrimaryProduction, Set<Symbol>>> selects = new HashMap<>();

    private LL1(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        super(lexicalAnalyzer, originalGrammar, GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(MergeGrammarConverter.class)
                .registerGrammarConverter(LreElfGrammarConverter.class)
                .build());
    }

    public static LLParser create(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        LL1 parser = new LL1(lexicalAnalyzer, originalGrammar);

        parser.init();

        return parser;
    }


    /**
     * 初始化方法
     */
    @Override
    protected void postInit() {
        // 计算select集
        calculateSelect();
    }

    @SuppressWarnings("unchecked")
    private void calculateSelect() {

        for (Symbol _A : this.grammar.getNonTerminators()) {
            Production _PA = getProductionMap().get(_A);

            for (PrimaryProduction _PPA : _PA.getPrimaryProductions()) {
                Symbol firstAlpha = _PPA.getRight().getSymbols().get(0);

                if (!selects.containsKey(_A)) {
                    selects.put(_A, new HashMap<>());
                }
                assertFalse(selects.get(_A).containsKey(_PPA));

                selects.get(_A).put(_PPA, new HashSet<>());

                // 如果ε∉FIRST(α)，那么SELECT(A→α)=FIRST(α)
                if (!getFirsts().get(firstAlpha).contains(Symbol.EPSILON)) {

                    selects.get(_A).get(_PPA).addAll(
                            getFirsts().get(firstAlpha)
                    );
                }
                // 如果ε∈FIRST(α)，那么SELECT(A→α)=(FIRST(α)−{ε})∪FOLLOW(A)
                else {
                    selects.get(_A).get(_PPA).addAll(
                            SetUtils.of(
                                    SetUtils.extract(getFirsts().get(firstAlpha), Symbol.EPSILON),
                                    getFollows().get(_A)
                            )
                    );
                }
            }
        }
    }

    @Override
    protected void checkIsLegal() {
        // 检查select集的唯一性：具有相同左部的产生式其SELECT集不相交
        for (Symbol _A : this.grammar.getNonTerminators()) {
            Map<PrimaryProduction, Set<Symbol>> map = selects.get(_A);
            assertNotNull(map);

            Set<Symbol> selectsOfA = new HashSet<>();

            for (Map.Entry<PrimaryProduction, Set<Symbol>> entry : map.entrySet()) {
                for (Symbol eachSelectSymbol : entry.getValue()) {
                    if (!selectsOfA.add(eachSelectSymbol)) {
                        setLegal(false);
                        return;
                    }
                }
            }
        }

        setLegal(true);
    }

    @Override
    protected boolean doMatches(String input) {
        LexicalAnalyzer.TokenIterator tokenIterator;
        try {
            tokenIterator = lexicalAnalyzer.iterator(input);
        } catch (ParserException e) {
            // 词法分析阶段发生了错误
            return false;
        }

        LinkedList<Symbol> symbolStack = new LinkedList<>();
        symbolStack.push(Symbol.DOLLAR);
        symbolStack.push(this.grammar.getStart());

        Token token = null;
        Symbol symbol;

        try {
            while (true) {
                if (symbolStack.isEmpty() && !tokenIterator.hasNext()) {
                    break;
                }

                if (symbolStack.isEmpty()) {
                    throw new ParserException();
                }

                // 每次迭代都会消耗一个symbol
                symbol = symbolStack.pop();

                // 每次迭代未必会消耗一个token
                if (token == null) {
                    if (!tokenIterator.hasNext()) {
                        throw new ParserException();
                    }
                    token = tokenIterator.next();
                }

                if (symbol.isTerminator()) {
                    // 若当前符号是ε则不消耗token
                    if (!symbol.equals(Symbol.EPSILON)) {

                        // symbol与token要么都是REGEX类型，要么都不是
                        if (symbol.getType().equals(MorphemeType.REGEX)
                                || token.getType().equals(MorphemeType.REGEX)) {
                            if (!(symbol.getType().equals(MorphemeType.REGEX) &&
                                    token.getType().equals(MorphemeType.REGEX))) {
                                throw new ParserException();
                            }
                        }

                        if (!token.getId().equals(symbol)) {
                            throw new ParserException();
                        }

                        // 消耗一个token
                        token = null;
                    }
                } else {
                    PrimaryProduction _PP = findProductionByToken(symbol, token);

                    List<Symbol> reversedSymbols = new ArrayList<>(_PP.getRight().getSymbols());

                    Collections.reverse(reversedSymbols);

                    for (Symbol nextSymbol : reversedSymbols) {
                        symbolStack.push(nextSymbol);
                    }
                }
            }
        } catch (ParserException e) {
            return false;
        }

        return true;
    }

    private PrimaryProduction findProductionByToken(Symbol symbol, Token token) throws ParserException {
        Symbol key = token.getId();

        Map<PrimaryProduction, Set<Symbol>> map = selects.get(symbol);

        PrimaryProduction _PPSelected = null;

        for (Map.Entry<PrimaryProduction, Set<Symbol>> entry : map.entrySet()) {
            for (Symbol selectedSymbol : entry.getValue()) {
                if (selectedSymbol.equals(key)) {
                    _PPSelected = entry.getKey();
                }
            }
        }

        if (_PPSelected == null) {
            throw new ParserException();
        }

        return _PPSelected;
    }


    @Override
    public String getSelectJSONString() {
        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (Map.Entry<Symbol, Map<PrimaryProduction, Set<Symbol>>> outerEntry : selects.entrySet()) {
            sb.append('\"');
            sb.append(outerEntry.getKey());
            sb.append('\"');
            sb.append(':');

            sb.append('{');

            for (Map.Entry<PrimaryProduction, Set<Symbol>> innerEntry : outerEntry.getValue().entrySet()) {
                sb.append('\"');
                sb.append(outerEntry.getKey())
                        .append(" → ")
                        .append(innerEntry.getKey().getRight());
                sb.append('\"');
                sb.append(':');

                sb.append('\"');

                for (Symbol firstSymbol : innerEntry.getValue()) {
                    sb.append(firstSymbol).append(',');
                }

                assertFalse(innerEntry.getValue().isEmpty());
                sb.setLength(sb.length() - 1);

                sb.append('\"');
                sb.append(',');
            }

            assertFalse(outerEntry.getValue().entrySet().isEmpty());
            sb.setLength(sb.length() - 1);
            sb.append('}');
            sb.append(',');
        }

        assertFalse(selects.isEmpty());
        sb.setLength(sb.length() - 1);
        sb.append('}');

        return sb.toString();
    }


    public String getAnalysisTableMarkdownString() {
        StringBuilder sb = new StringBuilder();

        String separator = "|";

        // 第一行：表头，各个终结符符号
        sb.append(separator)
                .append(' ')
                .append("非终结符\\终结符")
                .append(' ');

        for (Symbol terminator : this.grammar.getTerminators()) {
            sb.append(separator)
                    .append(' ')
                    .append(terminator)
                    .append(' ');
        }

        sb.append(separator).append('\n');

        // 第二行：对齐格式
        sb.append(separator);

        for (int i = 0; i < this.grammar.getTerminators().size(); i++) {
            sb.append(":--")
                    .append(separator);
        }
        sb.append(":--")
                .append(separator);

        sb.append('\n');

        // 其余行：每一行代表某个非终结符在不同终结符下的产生式
        // A → α
        for (Symbol _A : this.grammar.getNonTerminators()) {
            // 第一列，产生式
            sb.append(separator)
                    .append(' ')
                    .append(_A)
                    .append(' ');

            for (Symbol terminator : this.grammar.getTerminators()) {

                PrimaryProduction _PPA = null;

                for (Map.Entry<PrimaryProduction, Set<Symbol>> entry : selects.get(_A).entrySet()) {

                    Set<Symbol> selectsOfA = entry.getValue();

                    if (selectsOfA.contains(terminator)) {
                        _PPA = entry.getKey();
                        break;
                    }
                }

                if (_PPA != null) {
                    sb.append(separator)
                            .append(' ')
                            .append(_A)
                            .append(" → ")
                            .append(_PPA.getRight())
                            .append(' ');
                } else {
                    sb.append(separator)
                            .append(' ')
                            .append('\\')
                            .append(' ');
                }
            }

            sb.append(separator).append('\n');
        }

        return sb.toString();
    }
}
