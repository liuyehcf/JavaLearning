package org.liuyehcf.grammar.cfg.ll;

import org.liuyehcf.grammar.cfg.LexicalAnalyzer;
import org.liuyehcf.grammar.core.MorphemeType;
import org.liuyehcf.grammar.core.ParserException;
import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Production;
import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.core.definition.converter.GrammarConverterPipeline;
import org.liuyehcf.grammar.core.definition.converter.GrammarConverterPipelineImpl;
import org.liuyehcf.grammar.core.definition.converter.LreElfGrammarConverter;
import org.liuyehcf.grammar.core.definition.converter.MergeGrammarConverter;
import org.liuyehcf.grammar.core.parse.Token;
import org.liuyehcf.grammar.utils.SetUtils;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.*;


/**
 * LL1文法编译器
 */
public class LL1 implements LLParser {

    // 词法分析器
    private final LexicalAnalyzer lexicalAnalyzer;

    // 原始文法
    private final Grammar originalGrammar;

    // 文法转换流水线
    private final GrammarConverterPipeline grammarConverterPipeline;

    // 转换后的文法
    private Grammar grammar;

    // 文法开始符号
    private Symbol start;

    // 文法符号集合
    private Set<Symbol> symbols;

    // 非终结符集合
    private Set<Symbol> nonTerminatorSymbols;

    // 终结符集合
    private Set<Symbol> terminatorSymbols;

    // 非终结符->产生式的映射
    private Map<Symbol, Production> productionMap;

    // first集
    private Map<Symbol, Set<Symbol>> firsts;

    // follow集
    private Map<Symbol, Set<Symbol>> follows;

    // select集
    private Map<Symbol, Map<PrimaryProduction, Set<Symbol>>> selects;

    public LL1(Grammar originalGrammar, LexicalAnalyzer lexicalAnalyzer) {
        if (originalGrammar == null || lexicalAnalyzer == null) {
            throw new NullPointerException();
        }
        this.originalGrammar = originalGrammar;
        this.grammarConverterPipeline = GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(MergeGrammarConverter.class)
                .registerGrammarConverter(LreElfGrammarConverter.class)
                .build();
        this.lexicalAnalyzer = lexicalAnalyzer;
        symbols = new HashSet<>();
        nonTerminatorSymbols = new HashSet<>();
        terminatorSymbols = new HashSet<>();
        productionMap = new HashMap<>();
        firsts = new HashMap<>();
        follows = new HashMap<>();
        selects = new HashMap<>();

        init();
    }

    /**
     * 初始化方法
     */
    private void init() {
        // 转换给定文法，包括消除直接/间接左递归；提取公因子
        convertGrammar();

        // 计算first集
        calculateFirst();

        // 计算follow集
        calculateFollow();

        // 计算select集
        calculateSelect();
    }

    private void convertGrammar() {
        this.grammar = grammarConverterPipeline.convert(originalGrammar);

        this.start = this.grammar.getStart();

        for (Production _P : grammar.getProductions()) {
            nonTerminatorSymbols.add(_P.getLeft());

            for (PrimaryProduction _PP : _P.getRight()) {
                for (Symbol symbol : _PP.getSymbols()) {
                    if (symbol.isTerminator()) {
                        terminatorSymbols.add(symbol);
                    } else {
                        nonTerminatorSymbols.add(symbol);
                    }
                }
            }

            assertFalse(productionMap.containsKey(_P.getLeft()));
            productionMap.put(_P.getLeft(), _P);
        }

        symbols.addAll(nonTerminatorSymbols);
        symbols.addAll(terminatorSymbols);
    }

    private void calculateFirst() {
        // 首先，处理所有的终结符
        for (Symbol symbol : terminatorSymbols) {
            firsts.put(symbol, SetUtils.of(symbol));
        }

        // 处理非终结符
        boolean canBreak = false;
        while (!canBreak) {
            Map<Symbol, Set<Symbol>> newFirsts = new HashMap<>(this.firsts);

            for (Symbol _X : nonTerminatorSymbols) {
                Production _PX = productionMap.get(_X);

                assertNotNull(_PX);

                // 如果X是一个非终结符，且X→Y1...Yk∈P(k≥1)
                // 那么如果对于某个i，a在FIRST(Yi)中且ε在所有的FIRST(Y1),...,FIRST(Yi−1)中(即Y1...Yi−1⇒∗ε)，就把a加入到FIRST(X)中
                // 如果对于所有的j=1,2,...,k，ε在FIRST(Yj)中，那么将ε加入到FIRST(X)

                // 这里需要遍历每个子产生式
                for (PrimaryProduction _PPX : _PX.getRight()) {
                    boolean canReachEpsilon = true;

                    for (int i = 0; i < _PPX.getSymbols().size(); i++) {
                        Symbol _YI = _PPX.getSymbols().get(i);
                        if (!newFirsts.containsKey(_YI)) {
                            // 说明该符号的first集尚未计算，因此跳过当前子表达式
                            canReachEpsilon = false;
                            break;
                        } else {
                            // 首先，将_Y的first集(除了ε)添加到_X的first集中
                            if (!newFirsts.containsKey(_X)) {
                                newFirsts.put(_X, new HashSet<>());
                            }
                            newFirsts.get(_X).addAll(
                                    SetUtils.extract(
                                            newFirsts.get(_YI),
                                            Symbol.EPSILON
                                    )
                            );

                            // 若_Y的first集不包含ε，那么到子表达式循环结束
                            if (!newFirsts.get(_YI).contains(Symbol.EPSILON)) {
                                canReachEpsilon = false;
                                break;
                            }
                        }
                    }

                    if (canReachEpsilon) {
                        newFirsts.get(_X).add(Symbol.EPSILON);
                    }
                }
            }

            if (newFirsts.equals(this.firsts)) {
                canBreak = true;
            } else {
                this.firsts = newFirsts;
                canBreak = false;
            }
        }
    }

    private void calculateFollow() {
        // 将$放入FOLLOW(S)中，其中S是开始符号，$是输入右端的结束标记
        follows.put(start, SetUtils.of(Symbol.DOLLAR));

        boolean canBreak = false;
        while (!canBreak) {
            Map<Symbol, Set<Symbol>> newFollows = new HashMap<>(this.follows);

            for (Symbol _A : nonTerminatorSymbols) {
                Production _PA = productionMap.get(_A);

                assertNotNull(_PA);

                for (PrimaryProduction _PPA : _PA.getRight()) {
                    for (int i = 0; i < _PPA.getSymbols().size(); i++) {
                        Symbol _B = _PPA.getSymbols().get(i);
                        Symbol _BetaFirst = null;

                        if (_B.isTerminator()) {
                            continue;
                        }

                        if (i < _PPA.getSymbols().size() - 1) {
                            _BetaFirst = _PPA.getSymbols().get(i + 1);
                        }

                        // 如果存在一个产生式A→αBβ，那么FIRST(β)中除ε之外的所有符号都在FOLLOW(B)中
                        if (_BetaFirst != null) {
                            if (!newFollows.containsKey(_B)) {
                                newFollows.put(_B, new HashSet<>());
                            }

                            assertNotNull(this.firsts.get(_BetaFirst));

                            newFollows.get(_B).addAll(
                                    SetUtils.extract(
                                            this.firsts.get(_BetaFirst),
                                            Symbol.EPSILON)
                            );
                        }

                        // 如果存在一个产生式A→αB，或存在产生式A→αBβ且FIRST(β)包含ε，那么FOLLOW(A)中的所有符号都在FOLLOW(B)中
                        if (_BetaFirst == null
                                || this.firsts.get(_BetaFirst).contains(Symbol.EPSILON)) {

                            if (newFollows.containsKey(_A)) {

                                if (!newFollows.containsKey(_B)) {
                                    newFollows.put(_B, new HashSet<>());
                                }

                                newFollows.get(_B).addAll(
                                        newFollows.get(_A)
                                );
                            }
                        }
                    }
                }
            }

            if (newFollows.equals(this.follows)) {
                canBreak = true;
            } else {
                this.follows = newFollows;
                canBreak = false;
            }
        }

        // 检查一下是否所有的非终结符都有了follow集
        for (Symbol nonTerminator : nonTerminatorSymbols) {
            assertFalse(follows.get(nonTerminator).isEmpty());
        }
    }

    @SuppressWarnings("unchecked")
    private void calculateSelect() {

        for (Symbol _A : nonTerminatorSymbols) {
            Production _PA = productionMap.get(_A);

            for (PrimaryProduction _PPA : _PA.getRight()) {
                Symbol firstAlpha = _PPA.getSymbols().get(0);

                if (!selects.containsKey(_A)) {
                    selects.put(_A, new HashMap<>());
                }
                assertFalse(selects.get(_A).containsKey(_PPA));

                selects.get(_A).put(_PPA, new HashSet<>());

                // 如果ε∉FIRST(α)，那么SELECT(A→α)=FIRST(α)
                if (!firsts.get(firstAlpha).contains(Symbol.EPSILON)) {

                    selects.get(_A).get(_PPA).addAll(
                            firsts.get(firstAlpha)
                    );
                }
                // 如果ε∈FIRST(α)，那么SELECT(A→α)=(FIRST(α)−{ε})∪FOLLOW(A)
                else {
                    selects.get(_A).get(_PPA).addAll(
                            SetUtils.of(
                                    SetUtils.extract(firsts.get(firstAlpha), Symbol.EPSILON),
                                    follows.get(_A)
                            )
                    );
                }
            }
        }


        // 检查select集的唯一性：具有相同左部的产生式其SELECT集不相交
        for (Symbol _A : nonTerminatorSymbols) {
            Map<PrimaryProduction, Set<Symbol>> map = selects.get(_A);
            assertNotNull(map);

            Set<Symbol> selectsOfA = new HashSet<>();

            for (Map.Entry<PrimaryProduction, Set<Symbol>> entry : map.entrySet()) {
                for (Symbol eachSelectSymbol : entry.getValue()) {
                    assertTrue(selectsOfA.add(eachSelectSymbol));
                }
            }
        }
    }

    @Override
    public boolean isMatch(String expression) {
        LexicalAnalyzer.TokenIterator tokenIterator = lexicalAnalyzer.iterator(expression);

        LinkedList<Symbol> symbolStack = new LinkedList<>();
        symbolStack.push(Symbol.DOLLAR);
        symbolStack.push(start);

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

                        if (!token.getId().equals(symbol.getValue())) {
                            throw new ParserException();
                        }

                        // 消耗一个token
                        token = null;
                    }
                } else {
                    PrimaryProduction _PP = findProductionByToken(symbol, token);

                    // System.out.println(symbol.getStatus() + " → " + _PP.getStatus());

                    List<Symbol> reversedSymbols = new ArrayList<>(_PP.getSymbols());

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
        String key = token.getId();

        Map<PrimaryProduction, Set<Symbol>> map = selects.get(symbol);

        PrimaryProduction _PPSelected = null;

        for (Map.Entry<PrimaryProduction, Set<Symbol>> entry : map.entrySet()) {
            for (Symbol selectedSymbol : entry.getValue()) {
                if (selectedSymbol.getValue().equals(key)) {
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
    public Grammar getGrammar() {
        return this.grammar;
    }

    @Override
    public String getStatus() {
        StringBuilder sb = new StringBuilder();

        sb.append('{');

        sb.append('\"');
        sb.append("FIRST");
        sb.append('\"');
        sb.append(':');
        sb.append(getFirstReadableJSONString());

        sb.append(',');
        sb.append('\"');
        sb.append("FOLLOW");
        sb.append('\"');
        sb.append(':');
        sb.append(getFollowReadableJSONString());

        sb.append(',');
        sb.append('\"');
        sb.append("SELECT");
        sb.append('\"');
        sb.append(':');
        sb.append(getSelectReadableJSONString());

        sb.append('}');

        return sb.toString();
    }

    private String getFirstReadableJSONString() {
        return getReadableJSONStringFor(this.firsts, true, true);
    }

    private String getFollowReadableJSONString() {
        return getReadableJSONStringFor(this.follows, false, true);
    }

    private String getSelectReadableJSONString() {
        StringBuilder sb = new StringBuilder();

        sb.append('{');

        for (Map.Entry<Symbol, Map<PrimaryProduction, Set<Symbol>>> outerEntry : selects.entrySet()) {
            sb.append('\"');
            sb.append(outerEntry.getKey().toReadableJSONString());
            sb.append('\"');
            sb.append(':');

            sb.append('{');

            for (Map.Entry<PrimaryProduction, Set<Symbol>> innerEntry : outerEntry.getValue().entrySet()) {
                sb.append('\"');
                sb.append(outerEntry.getKey().toReadableJSONString())
                        .append(" → ")
                        .append(innerEntry.getKey().toReadableJSONString());
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

    private String getReadableJSONStringFor(Map<Symbol, Set<Symbol>> map, boolean containsTerminator, boolean containsNonTerminator) {
        StringBuilder sb = new StringBuilder();

        sb.append('{');

        if (containsTerminator) {
            sb.append("\"terminator\":");
            sb.append('{');

            for (Symbol terminator : terminatorSymbols) {
                sb.append('\"').append(terminator.toReadableJSONString()).append("\":");
                sb.append('\"');

                assertFalse(map.get(terminator).isEmpty());

                for (Symbol firstSymbol : map.get(terminator)) {
                    sb.append(firstSymbol).append(',');
                }

                sb.setLength(sb.length() - 1);

                sb.append('\"');
                sb.append(',');
            }

            assertFalse(terminatorSymbols.isEmpty());
            sb.setLength(sb.length() - 1);

            sb.append('}');
        }

        if (containsTerminator && containsNonTerminator) {
            sb.append(',');
        }

        if (containsNonTerminator) {
            sb.append("\"nonTerminator\":");
            sb.append('{');

            for (Symbol nonTerminator : nonTerminatorSymbols) {
                sb.append('\"').append(nonTerminator.toReadableJSONString()).append("\":");
                sb.append('\"');

                assertFalse(map.get(nonTerminator).isEmpty());

                for (Symbol firstSymbol : map.get(nonTerminator)) {
                    sb.append(firstSymbol).append(',');
                }

                sb.setLength(sb.length() - 1);

                sb.append('\"');
                sb.append(',');
            }

            assertFalse(nonTerminatorSymbols.isEmpty());
            sb.setLength(sb.length() - 1);


            sb.append('}');
        }
        sb.append('}');

        return sb.toString();
    }

    @Override
    public String getForecastAnalysisTable() {
        StringBuilder sb = new StringBuilder();

        String separator = "|";

        // 第一行：表头，各个终结符符号
        sb.append(separator)
                .append(' ')
                .append("非终结符\\终结符")
                .append(' ');

        for (Symbol terminator : terminatorSymbols) {
            sb.append(separator)
                    .append(' ')
                    .append(terminator.toReadableJSONString())
                    .append(' ');
        }

        sb.append(separator).append('\n');

        // 第二行：对齐格式
        sb.append(separator);

        for (int i = 0; i < terminatorSymbols.size(); i++) {
            sb.append(":--")
                    .append(separator);
        }
        sb.append(":--")
                .append(separator);

        sb.append('\n');

        // 其余行：每一行代表某个非终结符在不同终结符下的产生式
        // A → α
        for (Symbol _A : nonTerminatorSymbols) {
            // 第一列，产生式
            sb.append(separator)
                    .append(' ')
                    .append(_A.toReadableJSONString())
                    .append(' ');

            for (Symbol terminator : terminatorSymbols) {

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
                            .append(_A.toReadableJSONString())
                            .append(" → ")
                            .append(_PPA.toReadableJSONString())
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
