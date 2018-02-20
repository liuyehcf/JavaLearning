package org.liuyehcf.compile.compiler;

import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.Production;
import org.liuyehcf.compile.definition.Symbol;
import org.liuyehcf.compile.definition.SymbolSequence;

import java.util.*;
import java.util.stream.Collectors;

import static org.liuyehcf.compile.utils.AssertUtils.assertFalse;
import static org.liuyehcf.compile.utils.AssertUtils.assertTrue;
import static org.liuyehcf.compile.utils.ListUtils.of;
import static org.liuyehcf.compile.utils.ListUtils.subListExceptFirstElement;
import static org.liuyehcf.compile.utils.DefinitionUtils.*;

public class LL1Compiler implements Compiler {

    private final Grammar grammar;

    private final Map<Symbol, List<Symbol>> firsts;

    private final Map<Symbol, List<Symbol>> follows;

    public LL1Compiler(Grammar grammar) {
        this.grammar = convert(grammar);
        firsts = new HashMap<>();
        follows = new HashMap<>();
    }

    /**
     * 转换给定文法，包括消除直接/间接左递归；提取公因子
     *
     * @param grammar
     * @return
     */
    private Grammar convert(Grammar grammar) {
        return GrammarConverter.convert(grammar);
    }

    @Override
    public boolean isSentence(String sequence) {
        return false;
    }

    @Override
    public Grammar getGrammar() {
        return this.grammar;
    }

    /**
     * 用于转换文法的静态内部类
     */
    private static final class GrammarConverter {
        /**
         * 文法定义
         */
        private final Grammar grammar;

        /**
         * 从非终结符映射到产生式的Map
         */
        private Map<Symbol, Production> productionMap;

        /**
         * 根据依赖关系将非终结符进行排序后的结果（有向图遍历）
         */
        private List<Symbol> sortedSymbols;

        public static Grammar convert(Grammar grammar) {
            return new GrammarConverter(grammar)
                    .convert();
        }

        private GrammarConverter(Grammar grammar) {
            this.grammar = grammar;
        }

        private Grammar convert() {
            check();

            init();

            for (int i = 0; i < sortedSymbols.size(); i++) {
                for (int j = 0; j < i; j++) {
                    // 如果非终结符I的产生式里第一个非终结符是J，那么用J的产生式替换掉非终结符J
                    substitutionNonTerminator(i, j);
                }
                // 消除非终结符I的直接左递归
                eliminateDirectLeftRecursion(i);

                // 提取左公因子
                extractLeftCommonFactor(i);
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
                for (Production production : grammar.getProductions()) {
                    Symbol nonTerminator = production.getLeft();
                    assertFalse(nonTerminator.isTerminator());

                    // 合并相同非终结符的产生式
                    if (productionMap.containsKey(nonTerminator)) {
                        productionMap.put(nonTerminator, parallelProduction(
                                productionMap.get(nonTerminator),
                                production
                        ));
                    } else {
                        productionMap.put(nonTerminator, production);
                    }

                }
            }

            createOrderedSymbols();
        }

        private void createOrderedSymbols() {
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

                for (SymbolSequence symbolSequence : entry.getValue().getRight()) {
                    List<Symbol> symbols = symbolSequence.getSymbols();

                    assertTrue(!symbols.isEmpty());

                    if (!symbols.get(0).isTerminator()
                            && !symbols.get(0).equals(toSymbol)) {
                        Symbol fromSymbol = symbols.get(0);

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

            this.sortedSymbols = visitedSymbol;
        }

        /**
         * 将已消除直接/间接左递归的非终结符j的产生式代入产生式i中
         *
         * @param i
         * @param j
         */
        private void substitutionNonTerminator(int i, int j) {
            Symbol symbolI = sortedSymbols.get(i);
            Symbol symbolJ = sortedSymbols.get(j);

            Production productionI = productionMap.get(symbolI);
            Production productionJ = productionMap.get(symbolJ);

            // 标记是否发生替换
            boolean isSubstituted = false;

            List<SymbolSequence> symbolSequences = new ArrayList<>();

            // 遍历产生式I的每一个子产生式
            for (SymbolSequence symbolSequenceI : productionI.getRight()) {
                List<Symbol> symbolsI = symbolSequenceI.getSymbols();

                assertTrue(!symbolsI.isEmpty());

                // 如果子产生式第一个符号是symbolJ，那么进行替换
                if (symbolsI.get(0).equals(symbolJ)) {
                    isSubstituted = true;

                    // 遍历终结符J的每个子产生式
                    for (SymbolSequence symbolSequenceJ : productionJ.getRight()) {

                        symbolSequences.add(
                                createSymbolSequence(
                                        of(
                                                symbolSequenceJ.getSymbols(),
                                                subListExceptFirstElement(symbolsI)
                                        )
                                )
                        );
                    }

                } else {
                    symbolSequences.add(symbolSequenceI);
                }
            }

            if (isSubstituted) {
                productionMap.put(symbolI,
                        createProduction(
                                productionJ.getExtraProductions(),
                                symbolI,
                                symbolSequences));
            }
        }

        /**
         * 消除直接左递归，将产生式p1改造成p2
         * p1: A  → Aα1|Aα2|...|Aαn|β1|β2|...|βm
         * p2: A  → β1A′|β2A′|...|βmA′
         * p3: A′ → α1A′|α2A′|...|αnA′|ε
         *
         * @param i
         */
        private void eliminateDirectLeftRecursion(int i) {
            Symbol _A = sortedSymbols.get(i);

            Production p1 = productionMap.get(_A);

            // β1|β2|...|βm
            List<SymbolSequence> _Betas = new ArrayList<>();

            // Aα1|Aα2|...|Aαn
            List<SymbolSequence> _Alphas = new ArrayList<>();

            for (SymbolSequence symbolSequence : p1.getRight()) {
                List<Symbol> symbols = symbolSequence.getSymbols();

                assertTrue(!symbols.isEmpty());

                if (symbols.get(0).equals(_A)) {
                    _Alphas.add(symbolSequence);
                } else {
                    _Betas.add(symbolSequence);
                }
            }

            if (_Alphas.isEmpty()) {
                return;
            }

            List<SymbolSequence> p3SymbolSequence = new ArrayList<>();

            Symbol _AFlip = _A.getMutatedSymbol();

            for (SymbolSequence alphaSymbolSequence : _Alphas) {

                p3SymbolSequence.add(
                        // αiA′
                        createSymbolSequence(
                                of(
                                        // αi
                                        subListExceptFirstElement(alphaSymbolSequence.getSymbols()),
                                        // A′
                                        _AFlip
                                )
                        )
                );
            }

            // ε
            p3SymbolSequence.add(
                    createSymbolSequence(Symbol.EPSILON)
            );

            Production p3 = createProduction(
                    _AFlip,
                    p3SymbolSequence
            );

            List<SymbolSequence> p2SymbolSequence = new ArrayList<>();

            for (SymbolSequence betaSymbolSequence : _Betas) {

                // 构造βmA′
                p2SymbolSequence.add(
                        createSymbolSequence(
                                of(
                                        // βm
                                        betaSymbolSequence.getSymbols(),
                                        // A′
                                        _AFlip
                                )
                        )
                );
            }

            Map<Symbol, Production> extraProductionsOfP3 = new HashMap<>();
            extraProductionsOfP3.put(_AFlip, p3);


            Production p2 = createProduction(
                    extraProductionsOfP3,
                    _A,
                    p2SymbolSequence
            );

            productionMap.put(_A, p2);
        }

        /**
         * 提取左公因子，直到任意两个子产生式没有公共前缀
         * p1: A  → aβ1|aβ2|...|aβn|γ1|γ2|...|γm
         * p2: A  → aA′|γ1|γ2|...|γm
         * p3: A′ → β1|β2|...|βn
         *
         * @param i
         */
        private void extractLeftCommonFactor(int i) {
            Symbol _A = sortedSymbols.get(i);
            Production p1 = productionMap.get(_A);

            Map<Symbol, Integer> commonPrefixes = new HashMap<>();

            // 初始化commonPrefixes
            for (SymbolSequence symbolSequence : p1.getRight()) {
                List<Symbol> symbols = symbolSequence.getSymbols();

                assertTrue(!symbols.isEmpty() && symbols.get(0).isTerminator());

                Symbol firstSymbol = symbols.get(0);

                if (!commonPrefixes.containsKey(firstSymbol)) {
                    commonPrefixes.put(firstSymbol, 0);
                }

                commonPrefixes.put(firstSymbol, commonPrefixes.get(firstSymbol) + 1);
            }

            filterPrefixWithSingleCount(commonPrefixes);

            // 循环提取左公因子
            if (!commonPrefixes.isEmpty()) {

                Map<Symbol, Production> extraProductions = p1.getExtraProductions();

                // 产生一个异变的A，同时需要保证是没被使用过的，因为该函数会被递归调用
                Symbol _AFlip = _A;
                do {
                    _AFlip = _AFlip.getMutatedSymbol();
                } while (extraProductions.containsKey(_AFlip));

                Symbol prefixSymbol = commonPrefixes.keySet().iterator().next();

                List<SymbolSequence> _Betas = new ArrayList<>();
                List<SymbolSequence> _Gammas = new ArrayList<>();

                for (SymbolSequence symbolSequence : p1.getRight()) {
                    if (symbolSequence.getSymbols().get(0).equals(prefixSymbol)) {
                        if (symbolSequence.getSymbols().size() > 1) {
                            _Betas.add(
                                    createSymbolSequence(
                                            subListExceptFirstElement(symbolSequence.getSymbols())
                                    )
                            );
                        }
                    } else {
                        _Gammas.add(symbolSequence);
                    }
                }

                Production p3 = createProduction(
                        _AFlip, // A′
                        _Betas // β1|β2|...|βn
                );

                assertFalse(extraProductions.containsKey(_AFlip));
                extraProductions.put(_AFlip, p3);

                Production p2 = createProduction(
                        extraProductions,
                        _A, // A
                        of(
                                // aA′
                                createSymbolSequence(
                                        prefixSymbol,
                                        _AFlip
                                ),
                                // γ1|γ2|...|γm
                                _Gammas
                        )
                );

                productionMap.put(_A, p2);

                // 递归调用
                extractLeftCommonFactor(i);
            }

        }

        /**
         * 除去计数值为1的公共前缀
         *
         * @param commonPrefixes
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

        private Grammar createNewGrammar() {
            return createGrammar(
                    productionMap.entrySet()
                            .stream()
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList())
            );
        }
    }
}
