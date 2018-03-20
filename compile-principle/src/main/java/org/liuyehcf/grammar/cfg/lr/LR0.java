package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.core.definition.*;
import org.liuyehcf.grammar.core.definition.converter.*;
import org.liuyehcf.grammar.utils.Tuple;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;
import static org.liuyehcf.grammar.utils.AssertUtils.assertTrue;

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
    private Map<PrimaryProduction, Closure> closureMap = new HashMap<>();

    // 闭包有向边，Tuple存的就是closures中的索引号，以及转移输入符号。
    private List<Tuple<Closure, Closure, Symbol>> closureEdges = new ArrayList<>();

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

        assertTrue(indexOfDot < symbols.size() - 1);

        return symbols.get(indexOfDot + 1);
    }

    private void init() {
        // 文法转换
        convertGrammar();

        // 初始化项目集闭包
        initClosure();

        System.out.println(closureMap);
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
        closureMap.put(_PPOrigin, closure(_PPOrigin));

        while (!canBreak) {
            int preSize = closureMap.size();
            // 避免遍历时修改容器
            Map<PrimaryProduction, Closure> newAddedClosureMap = new HashMap<>();

            for (Closure preClosure : closureMap.values()) {

                // 遍历闭包中的产生式
                for (PrimaryProduction _PPre : preClosure.getPrimaryProductions()) {

                    PrimaryProduction _PPNext = successor(_PPre);

                    // 有后继
                    if (_PPNext != null) {
                        Symbol nextSymbol = nextSymbol(_PPre);
                        Closure nextClosure;

                        if (!closureMap.containsKey(_PPNext)) {
                            newAddedClosureMap.put(_PPNext, closure(_PPNext));
                            nextClosure = newAddedClosureMap.get(_PPNext);
                        } else {
                            nextClosure = closureMap.get(_PPNext);
                        }

                        // closureEdges.add(new Tuple<>(preClosure, nextClosure, nextSymbol));
                    }
                }
            }

            closureMap.putAll(newAddedClosureMap);

            if (preSize == closureMap.size()) {
                canBreak = true;
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

    // 项目集闭包
    private static final class Closure {
        private static int count=0;

        private final int id =count++;

        private final PrimaryProduction corePrimaryProduction;

        private final List<PrimaryProduction> primaryProductions;

        Closure(PrimaryProduction corePrimaryProduction, List<PrimaryProduction> primaryProductions) {
            this.corePrimaryProduction = corePrimaryProduction;
            this.primaryProductions = primaryProductions;
        }

        public PrimaryProduction getCorePrimaryProduction() {
            return corePrimaryProduction;
        }

        public List<PrimaryProduction> getPrimaryProductions() {
            return primaryProductions;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Closure) {
                Closure that = (Closure) obj;

                return that.corePrimaryProduction.getRight().getSymbols().equals(
                        this.corePrimaryProduction.getRight().getSymbols());

            }
            return false;
        }

        @Override
        public String toString() {
            return "Closure{" +
                    "id=" + id +
                    ", corePrimaryProduction=" + corePrimaryProduction +
                    ", primaryProductions=" + primaryProductions +
                    '}';
        }
    }
}
