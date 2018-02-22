package org.liuyehcf.grammar.cfg.ll;

import org.liuyehcf.grammar.cfg.LexicalAnalyzer;
import org.liuyehcf.grammar.core.MorphemeType;
import org.liuyehcf.grammar.core.ParseException;
import org.liuyehcf.grammar.definition.Grammar;
import org.liuyehcf.grammar.definition.PrimaryProduction;
import org.liuyehcf.grammar.definition.Production;
import org.liuyehcf.grammar.definition.Symbol;
import org.liuyehcf.grammar.parse.Token;
import org.liuyehcf.grammar.utils.ListUtils;
import org.liuyehcf.grammar.utils.SetUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.liuyehcf.grammar.utils.AssertUtils.*;


/**
 * LL1文法编译器
 */
public class LL1 implements LLParser {

    // 词法分析器
    private final LexicalAnalyzer lexicalAnalyzer;

    // 原始文法
    private final Grammar originGrammar;

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

    public LL1(Grammar originGrammar, LexicalAnalyzer lexicalAnalyzer) {
        if (originGrammar == null || lexicalAnalyzer == null) {
            throw new NullPointerException();
        }
        this.originGrammar = originGrammar;
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
        this.grammar = GrammarConverter.convert(originGrammar);

        this.start = this.grammar.getStart();

        for (Production p : grammar.getProductions()) {
            nonTerminatorSymbols.add(p.getLeft());

            for (PrimaryProduction pp : p.getRight()) {
                for (Symbol symbol : pp.getSymbols()) {
                    if (symbol.isTerminator()) {
                        terminatorSymbols.add(symbol);
                    } else {
                        nonTerminatorSymbols.add(symbol);
                    }
                }
            }

            assertFalse(productionMap.containsKey(p.getLeft()));
            productionMap.put(p.getLeft(), p);
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
                Production pX = productionMap.get(_X);

                assertNotNull(pX);

                // 如果X是一个非终结符，且X→Y1...Yk∈P(k≥1)
                // 那么如果对于某个i，a在FIRST(Yi)中且ε在所有的FIRST(Y1),...,FIRST(Yi−1)中(即Y1...Yi−1⇒∗ε)，就把a加入到FIRST(X)中
                // 如果对于所有的j=1,2,...,k，ε在FIRST(Yj)中，那么将ε加入到FIRST(X)

                // 这里需要遍历每个子产生式
                for (PrimaryProduction ppX : pX.getRight()) {
                    boolean canReachEpsilon = true;

                    for (int i = 0; i < ppX.getSymbols().size(); i++) {
                        Symbol _YI = ppX.getSymbols().get(i);
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
                Production pA = productionMap.get(_A);

                assertNotNull(pA);

                for (PrimaryProduction ppA : pA.getRight()) {
                    for (int i = 0; i < ppA.getSymbols().size(); i++) {
                        Symbol _B = ppA.getSymbols().get(i);
                        Symbol _BetaFirst = null;

                        if (_B.isTerminator()) {
                            continue;
                        }

                        if (i < ppA.getSymbols().size() - 1) {
                            _BetaFirst = ppA.getSymbols().get(i + 1);
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
            Production pA = productionMap.get(_A);

            for (PrimaryProduction ppA : pA.getRight()) {
                Symbol firstAlpha = ppA.getSymbols().get(0);

                if (!selects.containsKey(_A)) {
                    selects.put(_A, new HashMap<>());
                }
                assertFalse(selects.get(_A).containsKey(ppA));

                selects.get(_A).put(ppA, new HashSet<>());

                // 如果ε∉FIRST(α)，那么SELECT(A→α)=FIRST(α)
                if (!firsts.get(firstAlpha).contains(Symbol.EPSILON)) {

                    selects.get(_A).get(ppA).addAll(
                            firsts.get(firstAlpha)
                    );
                }
                // 如果ε∈FIRST(α)，那么SELECT(A→α)=(FIRST(α)−{ε})∪FOLLOW(A)
                else {
                    selects.get(_A).get(ppA).addAll(
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
                    throw new ParseException();
                }

                // 每次迭代都会消耗一个symbol
                symbol = symbolStack.pop();

                // 每次迭代未必会消耗一个token
                if (token == null) {
                    if (!tokenIterator.hasNext()) {
                        throw new ParseException();
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
                                throw new ParseException();
                            }
                        }

                        if (!token.getId().equals(symbol.getValue())) {
                            throw new ParseException();
                        }

                        // 消耗一个token
                        token = null;
                    }
                } else {
                    PrimaryProduction pp = findProductionByToken(symbol, token);

                    // System.out.println(symbol.getStatus() + " → " + pp.getStatus());

                    List<Symbol> reversedSymbols = new ArrayList<>(pp.getSymbols());

                    Collections.reverse(reversedSymbols);

                    for (Symbol nextSymbol : reversedSymbols) {
                        symbolStack.push(nextSymbol);
                    }
                }
            }
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    private PrimaryProduction findProductionByToken(Symbol symbol, Token token) throws ParseException {
        String key = token.getId();

        Map<PrimaryProduction, Set<Symbol>> map = selects.get(symbol);

        PrimaryProduction ppSelectedOne = null;

        for (Map.Entry<PrimaryProduction, Set<Symbol>> entry : map.entrySet()) {
            for (Symbol selectedSymbol : entry.getValue()) {
                if (selectedSymbol.getValue().equals(key)) {
                    ppSelectedOne = entry.getKey();
                }
            }
        }

        if (ppSelectedOne == null) {
            throw new ParseException();
        }

        return ppSelectedOne;
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

                PrimaryProduction ppA = null;

                for (Map.Entry<PrimaryProduction, Set<Symbol>> entry : selects.get(_A).entrySet()) {

                    Set<Symbol> selectsOfA = entry.getValue();

                    if (selectsOfA.contains(terminator)) {
                        ppA = entry.getKey();
                        break;
                    }
                }

                if (ppA != null) {
                    sb.append(separator)
                            .append(' ')
                            .append(_A.toReadableJSONString())
                            .append(" → ")
                            .append(ppA.toReadableJSONString())
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


    /**
     * 用于转换文法的静态内部类
     */
    public static final class GrammarConverter {

        // 待转换的文法
        private final Grammar grammar;

        // 非终结符 -> 产生式 的映射表
        private Map<Symbol, Production> productionMap;

        // 根据依赖关系将非终结符进行排序后的结果（有向图遍历）
        private List<Symbol> sortedNonTerminators;

        private GrammarConverter(Grammar grammar) {
            this.grammar = grammar;
        }

        public static Grammar convert(Grammar grammar) {
            return new GrammarConverter(grammar)
                    .convert();
        }

        private Grammar convert() {
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
                for (Production p : grammar.getProductions()) {
                    Symbol nonTerminator = p.getLeft();
                    assertFalse(nonTerminator.isTerminator());

                    // 必然不包含相同左部的产生式（在Grammar构造时已经合并过了）
                    assertFalse(productionMap.containsKey(nonTerminator));
                    productionMap.put(nonTerminator, p);
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

                for (PrimaryProduction pp : entry.getValue().getRight()) {
                    List<Symbol> symbols = pp.getSymbols();

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
         * 将已消除直接/间接左递归的非终结符_AJ的产生式，代入左部为非终结符_AI的产生式中
         *
         * @param _AI 被替换的非终结符
         * @param _AJ 用于替换的非终结符
         */
        private void substitutionNonTerminator(Symbol _AI, Symbol _AJ) {

            Production pI = productionMap.get(_AI);
            Production pJ = productionMap.get(_AJ);

            // 标记是否发生替换
            boolean isSubstituted = false;

            List<PrimaryProduction> ppIModified = new ArrayList<>();

            // 遍历产生式I的每一个子产生式
            for (PrimaryProduction ppIOrigin : pI.getRight()) {
                List<Symbol> symbolsI = ppIOrigin.getSymbols();

                assertFalse(symbolsI.isEmpty());

                // 如果子产生式第一个符号是symbolJ，那么进行替换
                if (symbolsI.get(0).equals(_AJ)) {
                    isSubstituted = true;

                    // 遍历终结符J的每个子产生式
                    for (PrimaryProduction ppJ : pJ.getRight()) {

                        ppIModified.add(
                                PrimaryProduction.create(
                                        ListUtils.of(
                                                ppJ.getSymbols(),
                                                ListUtils.subListExceptFirstElement(symbolsI)
                                        )
                                )
                        );
                    }

                } else {
                    ppIModified.add(ppIOrigin);
                }
            }

            if (isSubstituted) {
                productionMap.put(_AI,
                        Production.create(
                                _AI,
                                ppIModified));
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
            Production p1 = productionMap.get(_A);

            // β1|β2|...|βm
            List<PrimaryProduction> _Betas = new ArrayList<>();

            // Aα1|Aα2|...|Aαn
            List<PrimaryProduction> _Alphas = new ArrayList<>();

            for (PrimaryProduction pp1 : p1.getRight()) {
                List<Symbol> symbols = pp1.getSymbols();

                assertFalse(symbols.isEmpty());

                if (symbols.get(0).equals(_A)) {
                    _Alphas.add(pp1);
                } else {
                    _Betas.add(pp1);
                }
            }

            if (_Alphas.isEmpty()) {
                return;
            }

            List<PrimaryProduction> pp3 = new ArrayList<>();

            Symbol _APrimed = createPrimedSymbolFor(_A);

            for (PrimaryProduction ppAlpha : _Alphas) {

                pp3.add(
                        // αiA′
                        PrimaryProduction.create(
                                ListUtils.of(
                                        // αi
                                        ListUtils.subListExceptFirstElement(ppAlpha.getSymbols()),
                                        // A′
                                        _APrimed
                                )
                        )
                );
            }

            // ε
            pp3.add(
                    PrimaryProduction.create(Symbol.EPSILON)
            );

            Production p3 = Production.create(
                    _APrimed,
                    pp3
            );

            List<PrimaryProduction> pp2 = new ArrayList<>();

            for (PrimaryProduction ppBeta : _Betas) {

                // 构造βmA′
                pp2.add(
                        PrimaryProduction.create(
                                ListUtils.of(
                                        // βm
                                        ppBeta.getSymbols(),
                                        // A′
                                        _APrimed
                                )
                        )
                );
            }

            productionMap.put(_APrimed, p3);

            Production p2 = Production.create(
                    _A,
                    pp2
            );

            productionMap.put(_A, p2);
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

            Production p1 = productionMap.get(_A);

            // 所有平凡前缀计数
            Map<Symbol, Integer> commonPrefixes = new HashMap<>();

            // 初始化commonPrefixes
            for (PrimaryProduction pp1 : p1.getRight()) {
                List<Symbol> symbols = pp1.getSymbols();

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

                for (PrimaryProduction pp1 : p1.getRight()) {
                    if (pp1.getSymbols().get(0).equals(prefixSymbol)) {
                        if (pp1.getSymbols().size() > 1) {
                            _Betas.add(
                                    PrimaryProduction.create(
                                            ListUtils.subListExceptFirstElement(pp1.getSymbols())
                                    )
                            );
                        } else {
                            _Betas.add(
                                    PrimaryProduction.create(
                                            Symbol.EPSILON
                                    )
                            );
                        }
                    } else {
                        _Gammas.add(pp1);
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
                                PrimaryProduction.create(
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
                    grammar.getStart(),
                    productionMap.entrySet()
                            .stream()
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList())
            );
        }
    }
}
