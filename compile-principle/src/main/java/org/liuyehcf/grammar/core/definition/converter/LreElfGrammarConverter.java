package org.liuyehcf.grammar.core.definition.converter;

import org.liuyehcf.grammar.core.definition.*;
import org.liuyehcf.grammar.utils.ListUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;
import static org.liuyehcf.grammar.utils.AssertUtils.assertTrue;

/**
 * 左递归消除以及提取左公因子文法转换
 * LRE：Left recursive elimination
 * ELF：Extract Left Factory
 */
public class LreElfGrammarConverter extends AbstractGrammarConverter {

    // 非终结符 -> 产生式 的映射表
    private Map<Symbol, Production> productionMap;

    // 根据依赖关系将非终结符进行排序后的结果（有向图遍历）
    private List<Symbol> sortedNonTerminators;

    public LreElfGrammarConverter(Grammar originalGrammar) {
        super(originalGrammar);
    }

    @Override
    protected Grammar doConvert() {
        check();

        init();

        for (int i = 0; i < sortedNonTerminators.size(); i++) {
            Symbol _AI = sortedNonTerminators.get(i);
            for (int j = 0; j < i; j++) {
                Symbol _AJ = sortedNonTerminators.get(j);
                // 如果非终结符I的产生式里第一个非终结符是J，那么用J的产生式替换掉非终结符J
                substitutionNonTerminator(_AI, _AJ);
            }
            // 消除非终结符I的直接左递归
            eliminateDirectLeftRecursion(_AI);

            // 提取左公因子
            extractLeftCommonFactor(_AI);
        }

        return createNewGrammar();
    }

    /**
     * 检查待转换的文法是否符合LL1文法的要求
     */
    private void check() {

    }

    /**
     * 初始化一些变量
     */
    private void init() {
        if (productionMap == null) {
            productionMap = new HashMap<>();
            for (Production _P : originalGrammar.getProductions()) {
                Symbol nonTerminator = _P.getLeft();
                assertFalse(nonTerminator.isTerminator());

                // 必然不包含相同左部的产生式（在Grammar构造时已经合并过了）
                assertFalse(productionMap.containsKey(nonTerminator));
                productionMap.put(nonTerminator, _P);
            }
        }

        initSortedNonTerminators();
    }

    private void initSortedNonTerminators() {
        // 有向边，从key指向value
        Map<Symbol, List<Symbol>> edges = new HashMap<>();

        // 顶点的度，度为0的顶点才能访问
        Map<Symbol, Integer> degrees = new HashMap<>();

        // 初始化edges以及degrees
        for (Symbol symbol : productionMap.keySet()) {
            edges.put(symbol, new ArrayList<>());
            degrees.put(symbol, 0);
        }

        for (Map.Entry<Symbol, Production> entry : productionMap.entrySet()) {
            Symbol toSymbol = entry.getKey();

            for (PrimaryProduction _PP : entry.getValue().getPrimaryProductions()) {
                List<Symbol> symbols = _PP.getRight().getSymbols();

                assertFalse(symbols.isEmpty());

                if (!symbols.get(0).isTerminator()
                        && !symbols.get(0).equals(toSymbol)) {
                    Symbol fromSymbol = symbols.get(0);

                    assertTrue(edges.containsKey(fromSymbol));

                    edges.get(fromSymbol).add(toSymbol);

                    degrees.put(toSymbol, degrees.get(toSymbol) + 1);
                }
            }
        }

        Queue<Symbol> queue = new LinkedList<>();

        List<Symbol> visitedSymbol = new ArrayList<>();

        for (Map.Entry<Symbol, Integer> entry : degrees.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            Symbol curSymbol = queue.poll();
            visitedSymbol.add(curSymbol);

            // 有向邻接节点
            List<Symbol> adjList = edges.get(curSymbol);

            for (Symbol adjSymbol : adjList) {
                degrees.put(adjSymbol, degrees.get(adjSymbol) - 1);
                // 度为0，可以访问
                if (degrees.get(adjSymbol) == 0) {
                    queue.offer(adjSymbol);
                }
            }
        }

        assertTrue(visitedSymbol.size() == productionMap.size());

        this.sortedNonTerminators = visitedSymbol;
    }

    /**
     * 将已消除直接/间接左递归的非终结符_B的产生式，代入左部为非终结符_A的产生式中
     * A → Bα
     *
     * @param _A 被替换的非终结符
     * @param _B 已消除左递归的非终结符
     */
    private void substitutionNonTerminator(Symbol _A, Symbol _B) {

        Production _PA = productionMap.get(_A);
        Production _PB = productionMap.get(_B);

        // 标记是否发生替换
        boolean isSubstituted = false;

        List<PrimaryProduction> _PPAsModified = new ArrayList<>();

        // 遍历产生式I的每一个子产生式
        for (PrimaryProduction _PPA : _PA.getPrimaryProductions()) {
            List<Symbol> symbolsOfPPA = _PPA.getRight().getSymbols();

            assertFalse(symbolsOfPPA.isEmpty());

            // 如果子产生式第一个符号是symbolJ，那么进行替换
            if (symbolsOfPPA.get(0).equals(_B)) {
                isSubstituted = true;

                // 遍历终结符J的每个子产生式
                for (PrimaryProduction _PPB : _PB.getPrimaryProductions()) {

                    _PPAsModified.add(
                            PrimaryProduction.create(
                                    _A,
                                    SymbolString.create(
                                            ListUtils.of(
                                                    _PPB.getRight().getSymbols(),
                                                    ListUtils.subListExceptFirstElement(symbolsOfPPA)
                                            )
                                    )
                            )
                    );
                }

            } else {
                _PPAsModified.add(_PPA);
            }
        }

        if (isSubstituted) {
            productionMap.put(_A,
                    Production.create(
                            _PPAsModified)
            );
        }
    }

    /**
     * 消除直接左递归，将产生式p1改造成p2
     * p1: A  → Aα1|Aα2|...|Aαn|β1|β2|...|βm
     * p2: A  → β1A′|β2A′|...|βmA′
     * p3: A′ → α1A′|α2A′|...|αnA′|ε
     *
     * @param _A 产生式左侧非终结符
     */
    private void eliminateDirectLeftRecursion(Symbol _A) {
        Production _P1 = productionMap.get(_A);

        // β1|β2|...|βm
        List<PrimaryProduction> _Betas = new ArrayList<>();

        // Aα1|Aα2|...|Aαn
        List<PrimaryProduction> _Alphas = new ArrayList<>();

        for (PrimaryProduction _PP1 : _P1.getPrimaryProductions()) {
            List<Symbol> symbols = _PP1.getRight().getSymbols();

            assertFalse(symbols.isEmpty());

            if (symbols.get(0).equals(_A)) {
                _Alphas.add(_PP1);
            } else {
                _Betas.add(_PP1);
            }
        }

        if (_Alphas.isEmpty()) {
            return;
        }

        List<PrimaryProduction> pp3 = new ArrayList<>();

        Symbol _APrimed = createPrimedSymbolFor(_A);

        for (PrimaryProduction _PPAlpha : _Alphas) {

            pp3.add(
                    // αiA′
                    PrimaryProduction.create(
                            _APrimed,
                            SymbolString.create(
                                    ListUtils.of(
                                            // αi
                                            ListUtils.subListExceptFirstElement(_PPAlpha.getRight().getSymbols()),
                                            // A′
                                            _APrimed
                                    )
                            )
                    )
            );
        }

        // ε
        pp3.add(
                PrimaryProduction.create(
                        _APrimed,
                        SymbolString.create(Symbol.EPSILON)
                )
        );

        Production _P3 = Production.create(
                pp3
        );

        List<PrimaryProduction> pp2 = new ArrayList<>();

        for (PrimaryProduction _PPBeta : _Betas) {

            // 构造βmA′
            pp2.add(
                    PrimaryProduction.create(
                            _A,
                            SymbolString.create(
                                    ListUtils.of(
                                            // βm
                                            _PPBeta.getRight().getSymbols(),
                                            // A′
                                            _APrimed
                                    )
                            )
                    )

            );
        }

        productionMap.put(_APrimed, _P3);

        Production _P2 = Production.create(
                pp2
        );

        productionMap.put(_A, _P2);
    }

    /**
     * 提取左公因子，直到任意两个子产生式没有公共前缀
     * p1: A  → aβ1|aβ2|...|aβn|γ1|γ2|...|γm
     * p2: A  → aA′|γ1|γ2|...|γm
     * p3: A′ → β1|β2|...|βn
     *
     * @param _A 产生式左部的非终结符
     */
    private void extractLeftCommonFactor(Symbol _A) {

        Production _P1 = productionMap.get(_A);

        // 所有平凡前缀计数
        Map<Symbol, Integer> commonPrefixes = new HashMap<>();

        // 初始化commonPrefixes
        for (PrimaryProduction _PP1 : _P1.getPrimaryProductions()) {
            List<Symbol> symbols = _PP1.getRight().getSymbols();

            assertTrue(!symbols.isEmpty() && symbols.get(0).isTerminator());

            Symbol firstSymbol = symbols.get(0);

            // 跳过非平凡前缀
            if (firstSymbol.equals(Symbol.EPSILON)) {
                continue;
            }

            if (!commonPrefixes.containsKey(firstSymbol)) {
                commonPrefixes.put(firstSymbol, 0);
            }

            commonPrefixes.put(firstSymbol, commonPrefixes.get(firstSymbol) + 1);
        }

        filterPrefixWithSingleCount(commonPrefixes);

        // 循环提取左公因子
        if (!commonPrefixes.isEmpty()) {

            // 产生一个异变符号（例如，A′），同时需要保证是没被使用过的
            Symbol _APrimed = createPrimedSymbolFor(_A);

            Symbol prefixSymbol = commonPrefixes.keySet().iterator().next();

            List<PrimaryProduction> _Betas = new ArrayList<>();
            List<PrimaryProduction> _Gammas = new ArrayList<>();

            for (PrimaryProduction _PP1 : _P1.getPrimaryProductions()) {
                if (_PP1.getRight().getSymbols().get(0).equals(prefixSymbol)) {
                    if (_PP1.getRight().getSymbols().size() > 1) {
                        _Betas.add(
                                PrimaryProduction.create(
                                        _APrimed, // A′
                                        SymbolString.create(
                                                ListUtils.subListExceptFirstElement(_PP1.getRight().getSymbols())
                                        )
                                )
                        );
                    } else {
                        _Betas.add(
                                PrimaryProduction.create(
                                        _APrimed, // A′
                                        SymbolString.create(
                                                Symbol.EPSILON
                                        )
                                )
                        );
                    }
                } else {
                    _Gammas.add(_PP1);
                }
            }

            Production _P3 = Production.create(
                    _Betas // β1|β2|...|βn
            );

            assertFalse(productionMap.containsKey(_APrimed));
            productionMap.put(_APrimed, _P3);

            Production _P2 = Production.create(
                    ListUtils.of(
                            PrimaryProduction.create(
                                    _A, // A
                                    // aA′
                                    SymbolString.create(
                                            prefixSymbol,
                                            _APrimed
                                    )
                            ),
                            // γ1|γ2|...|γm
                            _Gammas
                    )
            );

            productionMap.put(_A, _P2);

            // 递归调用
            extractLeftCommonFactor(_A);
            extractLeftCommonFactor(_APrimed);
        }

    }

    /**
     * 除去计数值为1的公共前缀
     *
     * @param commonPrefixes 公共前缀映射表
     */
    private void filterPrefixWithSingleCount(Map<Symbol, Integer> commonPrefixes) {
        List<Symbol> removeKeys = new ArrayList<>();
        for (Map.Entry<Symbol, Integer> entry : commonPrefixes.entrySet()) {
            if (entry.getValue() == 1) {
                removeKeys.add(entry.getKey());
            }
        }
        for (Symbol key : removeKeys) {
            commonPrefixes.remove(key);
        }
    }

    private Symbol createPrimedSymbolFor(Symbol symbol) {
        Symbol primedSymbol = symbol;

        do {
            primedSymbol = primedSymbol.getPrimedSymbol();
        } while (productionMap.containsKey(primedSymbol));

        return primedSymbol;
    }

    private Grammar createNewGrammar() {
        return Grammar.create(
                originalGrammar.getStart(),
                productionMap.entrySet()
                        .stream()
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList())
        );
    }
}
