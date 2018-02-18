package org.liuyehcf.compile.compiler;

import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.Production;
import org.liuyehcf.compile.definition.Symbol;
import org.liuyehcf.compile.definition.SymbolSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.liuyehcf.compile.utils.AssertUtils.assertFalse;
import static org.liuyehcf.compile.utils.DefinitionUtils.*;

public class LL1Compiler implements Compiler {

    private final Grammar grammar;

    public LL1Compiler(Grammar grammar) {
        this.grammar = convert(grammar);
        System.out.println(this.grammar);
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
            init();

            for (int i = 0; i < sortedSymbols.size(); i++) {
                for (int j = 0; j < i; j++) {
                    substitutionNonTerminator(i, j);
                }
                eliminateDirectLeftRecursion();
            }

            return createNewGrammar();
        }

        private void init() {
            if (productionMap == null) {
                productionMap = new HashMap<>();
                for (Production production : grammar.getProductions()) {
                    Symbol nonTerminator = production.getLeft();
                    assertFalse(nonTerminator.getTerminator());

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
            // todo 这里需要按照有向图遍历进行排序
            this.sortedSymbols = new ArrayList<>(productionMap.keySet());
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

                // 如果子产生式第一个符号是symbolJ，那么进行替换
                if (!symbolsI.isEmpty()
                        && symbolsI.get(0).equals(symbolJ)) {
                    isSubstituted = true;

                    // 遍历终结符J的每个子产生式
                    for (SymbolSequence symbolSequenceJ : productionJ.getRight()) {

                        List<Symbol> combinedSymbols = symbolSequenceJ.getSymbols();

                        combinedSymbols.addAll(symbolsI.subList(1, symbolsI.size()));

                        symbolSequences.add(
                                createSymbolSequence(combinedSymbols)
                        );
                    }

                } else {
                    symbolSequences.add(symbolSequenceI);
                }
            }

            if (isSubstituted) {
                productionMap.put(symbolI,
                        createProduction(symbolI,
                                symbolSequences));
            }
        }

        private void eliminateDirectLeftRecursion() {

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
