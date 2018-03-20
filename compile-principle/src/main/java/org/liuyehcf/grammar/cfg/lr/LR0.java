package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.core.definition.*;
import org.liuyehcf.grammar.core.definition.converter.*;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.*;

public class LR0 implements LRParser {

    // 原始文法
    private final Grammar originalGrammar;

    // 文法转换流水线
    private final GrammarConverterPipeline grammarConverterPipeline;

    // 转换后的文法
    private Grammar grammar;

    // 非终结符 -> 产生式的映射
    private Map<Symbol, Production> symbolProductionMap = new HashMap<>();

    // 项目集闭包
    private List<Closure> closures = new ArrayList<>();

    // 状态转移表 [ClosureId, Symbol] -> ClosureId
    private Map<Integer, Map<Symbol, Integer>> closureTransferTable = new HashMap<>();

    private Map<Integer, Map<Symbol, Operation>> analysisTable = new HashMap<>();

    public LR0(Grammar grammar) {
        this.originalGrammar = grammar;
        this.grammarConverterPipeline = GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(AugmentedGrammarConverter.class)
                .registerGrammarConverter(StatusExpandGrammarConverter.class)
                .registerGrammarConverter(MergeGrammarConverter.class)
                .build();

        init();
    }

    private static PrimaryProduction successor(PrimaryProduction _PP) {

        List<Symbol> symbols = _PP.getRight().getSymbols();

        int indexOfDot = symbols.indexOf(Symbol.DOT);

        assertFalse(indexOfDot == -1);

        if (indexOfDot == symbols.size() - 1) {
            return null;
        }

        List<Symbol> successorSymbols = new ArrayList<>();

        if (indexOfDot > 0) {
            successorSymbols.addAll(symbols.subList(0, indexOfDot));
        }

        successorSymbols.add(symbols.get(indexOfDot + 1));

        successorSymbols.add(Symbol.DOT);

        if (indexOfDot + 2 < symbols.size()) {
            successorSymbols.addAll(symbols.subList(indexOfDot + 2, symbols.size()));
        }

        return PrimaryProduction.create(
                _PP.getLeft(),
                SymbolString.create(
                        successorSymbols
                )

        );
    }

    private static Symbol nextSymbol(PrimaryProduction _PP) {

        List<Symbol> symbols = _PP.getRight().getSymbols();

        int indexOfDot = symbols.indexOf(Symbol.DOT);

        assertFalse(indexOfDot == -1);

        if (indexOfDot == symbols.size() - 1) {
            return null;
        }

        return symbols.get(indexOfDot + 1);
    }

    private void init() {
        // 文法转换
        convertGrammar();

        // 初始化项目集闭包
        initClosure();

        // 初始化分析表
        initAnalysisTable();

        System.out.println(closures);
    }

    private void convertGrammar() {
        this.grammar = grammarConverterPipeline.convert(originalGrammar);

        // 初始化symbolProductionMap
        for (Production _P : grammar.getProductions()) {
            Symbol left = _P.getLeft();
            assertFalse(symbolProductionMap.containsKey(left));
            symbolProductionMap.put(left, _P);
        }
    }

    @Override
    public boolean matches(String input) {
        LinkedList<Integer> statusStack = new LinkedList<>();
        LinkedList<Symbol> symbolStack = new LinkedList<>();
        Queue<Symbol> remainSymbols = new LinkedList<>();

        statusStack.push(0);
        symbolStack.push(Symbol.DOLLAR);
        for (char c : input.toCharArray()) {
            remainSymbols.offer(Symbol.createTerminator(c));
        }
        remainSymbols.offer(Symbol.DOLLAR);

//        while (true) {
//
//        }

        return false;
    }

    private void moveIn() {

    }

    private void reduction() {

    }

    private void accept() {

    }

    private void error() {

    }

    @Override
    public Grammar getGrammar() {
        return grammar;
    }

    private void initClosure() {
        PrimaryProduction _PPOrigin; // origin production

        assertTrue(symbolProductionMap.get(Symbol.START).getPrimaryProductions().size() == 2);

        if (symbolProductionMap.get(Symbol.START).getPrimaryProductions().get(0) // 第一个子产生式
                .getRight().getSymbols().get(0).equals(Symbol.DOT) // 第一个符号
                ) {
            _PPOrigin = PrimaryProduction.create(
                    Symbol.START,
                    SymbolString.create(
                            symbolProductionMap.get(Symbol.START).getPrimaryProductions().get(0).getRight().getSymbols()
                    )

            );
        } else {
            _PPOrigin = PrimaryProduction.create(
                    Symbol.START,
                    SymbolString.create(
                            symbolProductionMap.get(Symbol.START).getPrimaryProductions().get(1).getRight().getSymbols()
                    )

            );
        }

        boolean canBreak = false;

        // 初始化，添加闭包0
        closures.add(closure(_PPOrigin));

        while (!canBreak) {
            canBreak = true;

            int preSize = closures.size();
            // 避免遍历时修改容器
            for (int i = 0; i < preSize; i++) {
                Closure preClosure = closures.get(i);

                // 遍历闭包中的产生式
                for (PrimaryProduction _PPre : preClosure.getPrimaryProductions()) {

                    PrimaryProduction _PPNext = successor(_PPre);

                    // 有后继
                    if (_PPNext != null) {
                        Symbol nextSymbol = nextSymbol(_PPre);
                        assertNotNull(nextSymbol);

                        Closure nextClosure;

                        int existsClosureId;

                        if ((existsClosureId = indexOf(_PPNext)) == -1) {
                            closures.add(closure(_PPNext));
                            nextClosure = closures.get(closures.size() - 1);
                        } else {
                            nextClosure = closures.get(existsClosureId);
                        }

                        if (!closureTransferTable.containsKey(preClosure.getId())) {
                            closureTransferTable.put(preClosure.getId(), new HashMap<>());
                        }

                        assertTrue(!closureTransferTable.get(preClosure.getId()).containsKey(nextSymbol)
                                || closureTransferTable.get(preClosure.getId()).get(nextSymbol).equals(nextClosure.getId()));

                        if (!closureTransferTable.get(preClosure.getId()).containsKey(nextSymbol)) {
                            closureTransferTable.get(preClosure.getId()).put(nextSymbol, nextClosure.getId());
                            canBreak = false;
                        }
                    }
                }
            }
        }
    }

    private Closure closure(PrimaryProduction _PPOrigin) {
        Set<PrimaryProduction> primaryProductions = new HashSet<>();

        primaryProductions.add(_PPOrigin);

        boolean canBreak = false;

        while (!canBreak) {
            int preSize = primaryProductions.size();
            List<PrimaryProduction> newAddedPrimaryProductions = new ArrayList<>();
            for (PrimaryProduction _PP : primaryProductions) {

                int indexOfDot = _PP.getRight().getSymbols().indexOf(Symbol.DOT);
                Symbol symbol;

                // '·'后面跟的是非终结符
                if (indexOfDot < _PP.getRight().getSymbols().size() - 1
                        && !(symbol = _PP.getRight().getSymbols().get(indexOfDot + 1)).isTerminator()) {

                    newAddedPrimaryProductions.addAll(findOriginalStatusPrimaryProductions(symbol));
                }
            }

            primaryProductions.addAll(newAddedPrimaryProductions);

            if (preSize == primaryProductions.size()) {
                canBreak = true;
            }
        }

        return new Closure(_PPOrigin, new ArrayList<>(primaryProductions));
    }

    private int indexOf(PrimaryProduction _PP) {
        for (int i = 0; i < closures.size(); i++) {
            if (closures.get(i).isCorePrimaryProduction(_PP)) {
                return i;
            }
        }
        return -1;
    }

    private List<PrimaryProduction> findOriginalStatusPrimaryProductions(Symbol symbol) {
        List<PrimaryProduction> result = new ArrayList<>();

        Production _P = symbolProductionMap.get(symbol);

        for (PrimaryProduction _PP : _P.getPrimaryProductions()) {
            if (Symbol.DOT.equals(_PP.getRight().getSymbols().get(0))) {
                result.add(_PP);
            }
        }

        return result;
    }

    private void initAnalysisTable() {
        for (Production _P : symbolProductionMap.values()) {
            for (PrimaryProduction _PP : _P.getPrimaryProductions()) {
                for (Closure closure : closures) {
                    if (closure.getPrimaryProductions().contains(_PP)) {
                        Symbol nextSymbol = nextSymbol(_PP);

                        if (nextSymbol == null) {

                        } else if (nextSymbol.isTerminator()) {

                        } else {

                        }

                    }
                }
            }
        }
    }
}
