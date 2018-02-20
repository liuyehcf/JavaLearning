package org.liuyehcf.compile.compiler;

import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.Production;
import org.liuyehcf.compile.definition.Symbol;
import org.liuyehcf.compile.definition.SymbolSequence;
import org.liuyehcf.compile.utils.ListUtils;
import org.liuyehcf.compile.utils.SetUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.liuyehcf.compile.definition.Grammar.parallelProduction;
import static org.liuyehcf.compile.utils.AssertUtils.*;

public class LL1Compiler implements Compiler {

    // 原始文法
    private final Grammar originGrammar;

    // 转换后的文法
    private Grammar grammar;

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

    public LL1Compiler(Grammar originGrammar) {
        this.originGrammar = originGrammar;
        symbols = new HashSet<>();
        nonTerminatorSymbols = new HashSet<>();
        terminatorSymbols = new HashSet<>();
        productionMap = new HashMap<>();
        firsts = new HashMap<>();
        follows = new HashMap<>();
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
    }

    private void convertGrammar() {
        this.grammar = GrammarConverter.convert(originGrammar);

        for (Production production : grammar.getProductions()) {
            nonTerminatorSymbols.add(production.getLeft());

            for (SymbolSequence symbolSequence : production.getRight()) {
                for (Symbol symbol : symbolSequence.getSymbols()) {
                    if (symbol.isTerminator()) {
                        terminatorSymbols.add(symbol);
                    } else {
                        nonTerminatorSymbols.add(symbol);
                    }
                }
            }

            assertFalse(productionMap.containsKey(production.getLeft()));
            productionMap.put(production.getLeft(), production);
        }

        symbols.addAll(nonTerminatorSymbols);
        symbols.addAll(terminatorSymbols);
    }

    private void calculateFirst() {
        // 首先，处理所有的终结符
        for (Symbol symbol : symbols) {
            if (symbol.isTerminator()) {
                firsts.put(symbol, SetUtils.of(symbol));
            }
        }

        // 处理非终结符
        boolean canBreak = false;
        while (!canBreak) {
            Map<Symbol, Set<Symbol>> newFirsts = new HashMap<>(this.firsts);

            for (Symbol _X : symbols) {
                if (!_X.isTerminator()) {
                    Production pX = productionMap.get(_X);

                    assertNotNull(pX);

                    // 如果X是一个非终结符，且X→Y1...Yk∈P(k≥1)
                    // 那么如果对于某个i，a在FIRST(Yi)中且ε在所有的FIRST(Y1),...,FIRST(Yi−1)中(即Y1...Yi−1⇒∗ε)，就把a加入到FIRST(X)中
                    // 如果对于所有的j=1,2,...,k，ε在FIRST(Yj)中，那么将ε加入到FIRST(X)

                    // 这里需要遍历每个子产生式
                    for (SymbolSequence symbolSequence : pX.getRight()) {
                        boolean canReachEpsilon = true;

                        for (int i = 0; i < symbolSequence.getSymbols().size(); i++) {
                            Symbol _YI = symbolSequence.getSymbols().get(i);
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
        follows.put(Symbol.START, SetUtils.of(Symbol.DOLLAR));

        boolean canBreak = false;
        while (!canBreak) {
            Map<Symbol, Set<Symbol>> newFollows = new HashMap<>(this.follows);

            for (Symbol _A : symbols) {
                if (!_A.isTerminator()) {
                    Production pA = productionMap.get(_A);

                    assertNotNull(pA);

                    for (SymbolSequence symbolSequence : pA.getRight()) {
                        for (int i = 0; i < symbolSequence.getSymbols().size(); i++) {
                            Symbol _B = symbolSequence.getSymbols().get(i);
                            Symbol _BetaFirst = null;

                            if (i < symbolSequence.getSymbols().size() - 1) {
                                _BetaFirst = symbolSequence.getSymbols().get(i + 1);
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
            }

            if (newFollows.equals(this.follows)) {
                canBreak = true;
            } else {
                this.follows = newFollows;
                canBreak = false;
            }
        }
    }

    @Override
    public boolean isSentence(String sequence) {
        return false;
    }

    @Override
    public Grammar getGrammar() {
        if (this.grammar == null) {
            init();
        }
        return this.grammar;
    }

    /**
     * 打印JSON格式的FIRST集
     *
     * @return
     */
    public String getFirstReadableJSONString() {
        StringBuilder sb = new StringBuilder();

        sb.append('{');
        sb.append("\"terminator\":");
        sb.append('{');

        for (Symbol terminator : terminatorSymbols) {
            sb.append('\"').append(terminator.toReadableJSONString()).append("\":");
            sb.append('\"');

            assertFalse(firsts.get(terminator).isEmpty());

            for (Symbol firstSymbol : firsts.get(terminator)) {
                sb.append(firstSymbol).append(',');
            }

            sb.setLength(sb.length() - 1);

            sb.append('\"');
            sb.append(',');
        }

        assertFalse(terminatorSymbols.isEmpty());
        sb.setLength(sb.length() - 1);

        sb.append('}');
        sb.append(',');
        sb.append("\"nonTerminator\":");
        sb.append('{');

        for (Symbol nonTerminator : nonTerminatorSymbols) {
            sb.append('\"').append(nonTerminator.toReadableJSONString()).append("\":");
            sb.append('\"');

            assertFalse(firsts.get(nonTerminator).isEmpty());

            for (Symbol firstSymbol : firsts.get(nonTerminator)) {
                sb.append(firstSymbol).append(',');
            }

            sb.setLength(sb.length() - 1);

            sb.append('\"');
            sb.append(',');
        }

        assertFalse(nonTerminatorSymbols.isEmpty());
        sb.setLength(sb.length() - 1);


        sb.append('}');
        sb.append('}');

        return sb.toString();
    }

    /**
     * 打印JSON格式的FOLLOW集
     *
     * @return
     */
    public String getFollowReadableJSONString() {
        StringBuilder sb = new StringBuilder();

        sb.append('{');
        sb.append("\"terminator\":");
        sb.append('{');

        for (Symbol terminator : terminatorSymbols) {
            sb.append('\"').append(terminator.toReadableJSONString()).append("\":");
            sb.append('\"');

            assertFalse(follows.get(terminator).isEmpty());

            for (Symbol firstSymbol : follows.get(terminator)) {
                sb.append(firstSymbol).append(',');
            }

            sb.setLength(sb.length() - 1);

            sb.append('\"');
            sb.append(',');
        }

        assertFalse(terminatorSymbols.isEmpty());
        sb.setLength(sb.length() - 1);

        sb.append('}');
        sb.append(',');
        sb.append("\"nonTerminator\":");
        sb.append('{');

        for (Symbol nonTerminator : nonTerminatorSymbols) {
            sb.append('\"').append(nonTerminator.toReadableJSONString()).append("\":");
            sb.append('\"');

            assertFalse(follows.get(nonTerminator).isEmpty());

            for (Symbol firstSymbol : follows.get(nonTerminator)) {
                sb.append(firstSymbol).append(',');
            }

            sb.setLength(sb.length() - 1);

            sb.append('\"');
            sb.append(',');
        }

        assertFalse(nonTerminatorSymbols.isEmpty());
        sb.setLength(sb.length() - 1);


        sb.append('}');
        sb.append('}');

        return sb.toString();
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

        private GrammarConverter(Grammar grammar) {
            this.grammar = grammar;
        }

        private static Grammar convert(Grammar grammar) {
            return new GrammarConverter(grammar)
                    .convert();
        }

        private Grammar convert() {
            check();

            init();

            for (int i = 0; i < sortedSymbols.size(); i++) {
                Symbol _AI = sortedSymbols.get(i);
                for (int j = 0; j < i; j++) {
                    Symbol _AJ = sortedSymbols.get(j);
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
                for (Production production : grammar.getProductions()) {
                    Symbol nonTerminator = production.getLeft();
                    assertFalse(nonTerminator.isTerminator());

                    // 合并相同非终结符的产生式
                    if (productionMap.containsKey(nonTerminator)) {
                        productionMap.put(
                                nonTerminator,
                                parallelProduction(
                                        productionMap.get(nonTerminator),
                                        production
                                )
                        );
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

            this.sortedSymbols = visitedSymbol;
        }

        /**
         * 将已消除直接/间接左递归的非终结符j的产生式代入产生式i中
         *
         * @param _AI
         * @param _AJ
         */
        private void substitutionNonTerminator(Symbol _AI, Symbol _AJ) {

            Production pI = productionMap.get(_AI);
            Production pJ = productionMap.get(_AJ);

            // 标记是否发生替换
            boolean isSubstituted = false;

            List<SymbolSequence> modifiedSymbolSequencesI = new ArrayList<>();

            // 遍历产生式I的每一个子产生式
            for (SymbolSequence originSymbolSequenceI : pI.getRight()) {
                List<Symbol> symbolsI = originSymbolSequenceI.getSymbols();

                assertFalse(symbolsI.isEmpty());

                // 如果子产生式第一个符号是symbolJ，那么进行替换
                if (symbolsI.get(0).equals(_AJ)) {
                    isSubstituted = true;

                    // 遍历终结符J的每个子产生式
                    for (SymbolSequence symbolSequenceJ : pJ.getRight()) {

                        modifiedSymbolSequencesI.add(
                                SymbolSequence.create(
                                        ListUtils.of(
                                                symbolSequenceJ.getSymbols(),
                                                ListUtils.subListExceptFirstElement(symbolsI)
                                        )
                                )
                        );
                    }

                } else {
                    modifiedSymbolSequencesI.add(originSymbolSequenceI);
                }
            }

            if (isSubstituted) {
                productionMap.put(_AI,
                        Production.create(
                                _AI,
                                modifiedSymbolSequencesI));
            }
        }

        /**
         * 消除直接左递归，将产生式p1改造成p2
         * p1: A  → Aα1|Aα2|...|Aαn|β1|β2|...|βm
         * p2: A  → β1A′|β2A′|...|βmA′
         * p3: A′ → α1A′|α2A′|...|αnA′|ε
         *
         * @param _A
         */
        private void eliminateDirectLeftRecursion(Symbol _A) {
            Production p1 = productionMap.get(_A);

            // β1|β2|...|βm
            List<SymbolSequence> _Betas = new ArrayList<>();

            // Aα1|Aα2|...|Aαn
            List<SymbolSequence> _Alphas = new ArrayList<>();

            for (SymbolSequence symbolSequenceOfP1 : p1.getRight()) {
                List<Symbol> symbols = symbolSequenceOfP1.getSymbols();

                assertFalse(symbols.isEmpty());

                if (symbols.get(0).equals(_A)) {
                    _Alphas.add(symbolSequenceOfP1);
                } else {
                    _Betas.add(symbolSequenceOfP1);
                }
            }

            if (_Alphas.isEmpty()) {
                return;
            }

            List<SymbolSequence> symbolSequenceOfP3 = new ArrayList<>();

            Symbol _APrimed = createPrimedSymbolFor(_A);

            for (SymbolSequence alphaSymbolSequence : _Alphas) {

                symbolSequenceOfP3.add(
                        // αiA′
                        SymbolSequence.create(
                                ListUtils.of(
                                        // αi
                                        ListUtils.subListExceptFirstElement(alphaSymbolSequence.getSymbols()),
                                        // A′
                                        _APrimed
                                )
                        )
                );
            }

            // ε
            symbolSequenceOfP3.add(
                    SymbolSequence.create(Symbol.EPSILON)
            );

            Production p3 = Production.create(
                    _APrimed,
                    symbolSequenceOfP3
            );

            List<SymbolSequence> symbolSequenceOfP2 = new ArrayList<>();

            for (SymbolSequence betaSymbolSequence : _Betas) {

                // 构造βmA′
                symbolSequenceOfP2.add(
                        SymbolSequence.create(
                                ListUtils.of(
                                        // βm
                                        betaSymbolSequence.getSymbols(),
                                        // A′
                                        _APrimed
                                )
                        )
                );
            }

            productionMap.put(_APrimed, p3);

            Production p2 = Production.create(
                    _A,
                    symbolSequenceOfP2
            );

            productionMap.put(_A, p2);
        }

        /**
         * 提取左公因子，直到任意两个子产生式没有公共前缀
         * p1: A  → aβ1|aβ2|...|aβn|γ1|γ2|...|γm
         * p2: A  → aA′|γ1|γ2|...|γm
         * p3: A′ → β1|β2|...|βn
         *
         * @param _A
         */
        private void extractLeftCommonFactor(Symbol _A) {
            Production p1 = productionMap.get(_A);

            // 所有平凡前缀计数
            Map<Symbol, Integer> commonPrefixes = new HashMap<>();

            // 初始化commonPrefixes
            for (SymbolSequence symbolSequenceOfP1 : p1.getRight()) {
                List<Symbol> symbols = symbolSequenceOfP1.getSymbols();

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

                List<SymbolSequence> _Betas = new ArrayList<>();
                List<SymbolSequence> _Gammas = new ArrayList<>();

                for (SymbolSequence symbolSequenceOfP1 : p1.getRight()) {
                    if (symbolSequenceOfP1.getSymbols().get(0).equals(prefixSymbol)) {
                        if (symbolSequenceOfP1.getSymbols().size() > 1) {
                            _Betas.add(
                                    SymbolSequence.create(
                                            ListUtils.subListExceptFirstElement(symbolSequenceOfP1.getSymbols())
                                    )
                            );
                        } else {
                            _Betas.add(
                                    SymbolSequence.create(
                                            Symbol.EPSILON
                                    )
                            );
                        }
                    } else {
                        _Gammas.add(symbolSequenceOfP1);
                    }
                }

                Production p3 = Production.create(
                        _APrimed, // A′
                        _Betas // β1|β2|...|βn
                );

                assertFalse(productionMap.containsKey(_APrimed));
                productionMap.put(_APrimed, p3);

                Production p2 = Production.create(
                        _A, // A
                        ListUtils.of(
                                // aA′
                                SymbolSequence.create(
                                        prefixSymbol,
                                        _APrimed
                                ),
                                // γ1|γ2|...|γm
                                _Gammas
                        )
                );

                productionMap.put(_A, p2);

                // 递归调用
                extractLeftCommonFactor(_A);
                extractLeftCommonFactor(_APrimed);
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

        private Symbol createPrimedSymbolFor(Symbol symbol) {
            Symbol primedSymbol = symbol;

            do {
                primedSymbol = primedSymbol.getPrimedSymbol();
            } while (productionMap.containsKey(primedSymbol));

            return primedSymbol;
        }

        private Grammar createNewGrammar() {
            return Grammar.create(
                    productionMap.entrySet()
                            .stream()
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList())
            );
        }
    }
}
