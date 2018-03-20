package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.core.definition.*;
import org.liuyehcf.grammar.core.definition.converter.*;
import org.liuyehcf.grammar.utils.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Production, Closure> closureMap = new HashMap<>();

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

    private static Production successor(Production _P) {
        assertTrue(_P.getPrimaryProductions().size() == 1);

        List<Symbol> symbols = _P.getPrimaryProductions().get(0).getRight().getSymbols();

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

        return Production.create(
                PrimaryProduction.create(
                        _P.getLeft(),
                        SymbolString.create(
                                successorSymbols
                        )
                )
        );
    }

    private static Symbol nextSymbol(Production _P) {
        assertTrue(_P.getPrimaryProductions().size() == 1);

        List<Symbol> symbols = _P.getPrimaryProductions().get(0).getRight().getSymbols();

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
        Production _originP; // origin production

        assertTrue(symbolProductionMap.get(Symbol.START).getPrimaryProductions().size() == 2);

        if (symbolProductionMap.get(Symbol.START).getPrimaryProductions().get(0) // 第一个子产生式
                .getRight().getSymbols().get(0).equals(Symbol.DOT) // 第一个符号
                ) {
            _originP = Production.create(
                    PrimaryProduction.create(
                            Symbol.START,
                            SymbolString.create(
                                    symbolProductionMap.get(Symbol.START).getPrimaryProductions().get(0).getRight().getSymbols()
                            )
                    )
            );
        } else {
            _originP = Production.create(
                    PrimaryProduction.create(
                            Symbol.START,
                            SymbolString.create(
                                    symbolProductionMap.get(Symbol.START).getPrimaryProductions().get(1).getRight().getSymbols()
                            )
                    )
            );
        }

        boolean canBreak = false;

        // 初始化，添加闭包0
        closureMap.put(_originP, closure(_originP));

        while (!canBreak) {
            int preSize = closureEdges.size();

            for (Closure preClosure : closureMap.values()) {

                // 遍历闭包中的产生式
                for (Production preP : preClosure.getProductions()) {
                    // 只能有一个产生式
                    assertTrue(preP.getPrimaryProductions().size() == 1);

                    Production nextP = successor(preP);

                    // 有后继
                    if (nextP != null) {
                        Symbol nextSymbol = nextSymbol(preP);

                        if (!closureMap.containsKey(nextP)) {
                            closureMap.put(nextP, closure(nextP));
                        }

                        Closure nextClosure = closureMap.get(nextP);
                        closureEdges.add(new Tuple<>(preClosure, nextClosure, nextSymbol));
                    }
                }
            }


            if (preSize == closureEdges.size()) {
                canBreak = true;
            }
        }
    }

    private Closure closure(Production _OP) {
        List<Production> productions = new ArrayList<>();

        productions.add(_OP);

        boolean canBreak = false;

        while (!canBreak) {
            for (Production _P : productions) {
                assertTrue(_P.getPrimaryProductions().size() == 1);

                for (Symbol symbol : _P.getPrimaryProductions().get(0).getRight().getSymbols()) {
                    if (!symbol.isTerminator()) {

                    }
                }
            }
        }

        return new Closure(_OP, productions);
    }

    // 项目集闭包
    private static final class Closure {
        private final Production coreProduction;

        private final List<Production> productions;

        Closure(Production coreProduction, List<Production> productions) {
            assertTrue(coreProduction.getPrimaryProductions().size() == 1);
            this.coreProduction = coreProduction;
            this.productions = productions;
        }

        public Production getCoreProduction() {
            return coreProduction;
        }

        public List<Production> getProductions() {
            return productions;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Closure) {
                Closure other = (Closure) obj;

                return other.coreProduction.getLeft().equals(this.coreProduction.getLeft())
                        && other.coreProduction.getPrimaryProductions().get(0).getRight().getSymbols().equals(
                        this.coreProduction.getPrimaryProductions().get(0).getRight().getSymbols());

            }
            return false;
        }
    }
}
