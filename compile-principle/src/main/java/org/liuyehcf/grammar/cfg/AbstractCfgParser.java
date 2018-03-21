package org.liuyehcf.grammar.cfg;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Production;
import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.core.definition.converter.GrammarConverterPipeline;
import org.liuyehcf.grammar.utils.SetUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;
import static org.liuyehcf.grammar.utils.AssertUtils.assertNotNull;

public abstract class AbstractCfgParser implements CfgParser {

    // 词法分析器
    protected final LexicalAnalyzer lexicalAnalyzer;

    // 原始文法
    private final Grammar originalGrammar;

    // 文法转换流水线
    private final GrammarConverterPipeline grammarConverterPipeline;

    // 转换后的文法
    protected Grammar grammar;

    // 非终结符->产生式的映射
    private Map<Symbol, Production> productionMap;

    // first集
    private Map<Symbol, Set<Symbol>> firsts;

    // follow集
    private Map<Symbol, Set<Symbol>> follows;

    protected AbstractCfgParser(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar, GrammarConverterPipeline grammarConverterPipeline) {
        if (originalGrammar == null || lexicalAnalyzer == null) {
            throw new NullPointerException();
        }
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.originalGrammar = originalGrammar;
        this.grammarConverterPipeline = grammarConverterPipeline;

        productionMap = new HashMap<>();
        firsts = new HashMap<>();
        follows = new HashMap<>();
    }

    public Map<Symbol, Production> getProductionMap() {
        return productionMap;
    }

    public Map<Symbol, Set<Symbol>> getFirsts() {
        return firsts;
    }

    public Map<Symbol, Set<Symbol>> getFollows() {
        return follows;
    }

    @Override
    public final Grammar getGrammar() {
        return grammar;
    }

    @Override
    public void init() {
        // 转换给定文法，包括消除直接/间接左递归；提取公因子
        convertGrammar();

        // 计算first集
        calculateFirst();

        // 计算follow集
        calculateFollow();

        // 后续初始化动作
        postInit();
    }

    protected abstract void postInit();

    private void convertGrammar() {
        this.grammar = grammarConverterPipeline.convert(originalGrammar);

        for (Production _P : grammar.getProductions()) {
            assertFalse(productionMap.containsKey(_P.getLeft()));
            productionMap.put(_P.getLeft(), _P);
        }

    }

    private void calculateFirst() {
        // 首先，处理所有的终结符
        for (Symbol symbol : this.grammar.getTerminators()) {
            firsts.put(symbol, SetUtils.of(symbol));
        }

        // 处理非终结符
        boolean canBreak = false;
        while (!canBreak) {
            Map<Symbol, Set<Symbol>> newFirsts = copyFirst();

            for (Symbol _X : this.grammar.getNonTerminators()) {
                Production _PX = productionMap.get(_X);

                assertNotNull(_PX);

                // 如果X是一个非终结符，且X→Y1...Yk∈P(k≥1)
                // 那么如果对于某个i，a在FIRST(Yi)中且ε在所有的FIRST(Y1),...,FIRST(Yi−1)中(即Y1...Yi−1⇒∗ε)，就把a加入到FIRST(X)中
                // 如果对于所有的j=1,2,...,k，ε在FIRST(Yj)中，那么将ε加入到FIRST(X)

                // 这里需要遍历每个子产生式
                for (PrimaryProduction _PPX : _PX.getPrimaryProductions()) {
                    boolean canReachEpsilon = true;

                    for (int i = 0; i < _PPX.getRight().getSymbols().size(); i++) {
                        Symbol _YI = _PPX.getRight().getSymbols().get(i);
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
        follows.put(this.grammar.getStart(), SetUtils.of(Symbol.DOLLAR));

        boolean canBreak = false;
        while (!canBreak) {
            Map<Symbol, Set<Symbol>> newFollows = copyFollow();

            for (Symbol _A : this.grammar.getNonTerminators()) {
                Production _PA = productionMap.get(_A);

                assertNotNull(_PA);

                for (PrimaryProduction _PPA : _PA.getPrimaryProductions()) {
                    for (int i = 0; i < _PPA.getRight().getSymbols().size(); i++) {
                        Symbol _B = _PPA.getRight().getSymbols().get(i);
                        Symbol _BetaFirst = null;

                        if (_B.isTerminator()) {
                            continue;
                        }

                        if (i < _PPA.getRight().getSymbols().size() - 1) {
                            _BetaFirst = _PPA.getRight().getSymbols().get(i + 1);
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
        for (Symbol nonTerminator : this.grammar.getNonTerminators()) {
            assertFalse(follows.get(nonTerminator).isEmpty());
        }
    }

    private Map<Symbol, Set<Symbol>> copyFirst() {
        Map<Symbol, Set<Symbol>> copy = new HashMap<>();
        for (Map.Entry<Symbol, Set<Symbol>> entry : firsts.entrySet()) {
            copy.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return copy;
    }

    private Map<Symbol, Set<Symbol>> copyFollow() {
        Map<Symbol, Set<Symbol>> copy = new HashMap<>();
        for (Map.Entry<Symbol, Set<Symbol>> entry : follows.entrySet()) {
            copy.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        return copy;
    }


    @Override
    public final String getFirstJSONString() {
        return getJSONStringFor(this.getFirsts(), true);
    }

    @Override
    public final String getFollowJSONString() {
        return getJSONStringFor(this.getFollows(), false);
    }

    private String getJSONStringFor(Map<Symbol, Set<Symbol>> map, boolean containsTerminator) {
        StringBuilder sb = new StringBuilder();

        sb.append('{');

        if (containsTerminator) {
            sb.append("\"terminator\":");
            sb.append('{');

            for (Symbol terminator : this.grammar.getTerminators()) {
                sb.append('\"').append(terminator.toJSONString()).append("\":");
                sb.append('\"');

                assertFalse(map.get(terminator).isEmpty());

                for (Symbol firstSymbol : map.get(terminator)) {
                    sb.append(firstSymbol).append(',');
                }

                sb.setLength(sb.length() - 1);

                sb.append('\"');
                sb.append(',');
            }

            assertFalse(this.grammar.getTerminators().isEmpty());
            sb.setLength(sb.length() - 1);

            sb.append('}');
        }

        if (containsTerminator) {
            sb.append(',');
        }

        sb.append("\"nonTerminator\":");
        sb.append('{');

        for (Symbol nonTerminator : this.grammar.getNonTerminators()) {
            sb.append('\"').append(nonTerminator.toJSONString()).append("\":");
            sb.append('\"');

            assertFalse(map.get(nonTerminator).isEmpty());

            for (Symbol firstSymbol : map.get(nonTerminator)) {
                sb.append(firstSymbol).append(',');
            }

            sb.setLength(sb.length() - 1);

            sb.append('\"');
            sb.append(',');
        }

        assertFalse(this.grammar.getNonTerminators().isEmpty());
        sb.setLength(sb.length() - 1);


        sb.append('}');
        sb.append('}');

        return sb.toString();
    }
}
