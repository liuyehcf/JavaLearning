package org.liuyehcf.grammar.rg;

import org.liuyehcf.grammar.definition.Grammar;
import org.liuyehcf.grammar.definition.PrimaryProduction;
import org.liuyehcf.grammar.definition.Production;
import org.liuyehcf.grammar.definition.Symbol;
import org.liuyehcf.grammar.rg.dfa.Dfa;
import org.liuyehcf.grammar.rg.nfa.Nfa;
import org.liuyehcf.grammar.rg.utils.GrammarUtils;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.*;

public class RGBuilder {

    // 正则文法
    private final Grammar grammar;

    // nfa自动机
    private Nfa nfa;

    // dfa自动机
    private Dfa dfa;

    private RGBuilder(Grammar grammar) {
        this.grammar = grammar;
        this.nfa = null;
        this.dfa = null;
    }

    public static RGBuilder compile(String regex) {

        Grammar grammar = GrammarConverter.convert(
                GrammarUtils.createGrammarWithRegex(regex)
        );

        return new RGBuilder(grammar);
    }

    public RGParser buildNfa() {
        if (nfa == null) {
            nfa = new Nfa(grammar);
        }
        return nfa;
    }

    public RGParser buildDfa() {
        if (dfa == null) {
            if (nfa == null) {
                buildNfa();
            }
            dfa = new Dfa(nfa);
        }
        return dfa;
    }

    public static final class GrammarConverter {

        // 待转换的文法
        private final Grammar grammar;

        // 从非终结符 -> PrimaryProduction 的映射表
        Map<Symbol, PrimaryProduction> primaryProductionMap = new HashMap<>();

        // 按依赖关系排序的非终结符集合
        private List<Symbol> sortedNonTerminators = new ArrayList<>();

        // 从非终结符 -> Production 的映射表
        private Map<Symbol, Production> productionMap = new HashMap<>();

        private GrammarConverter(Grammar grammar) {
            this.grammar = grammar;
        }

        public static Grammar convert(Grammar grammar) {
            return new GrammarConverter(grammar)
                    .convert();
        }

        private Grammar convert() {
            init();

            checkIfFirstProductionContainsNonTerminator();

            iterativeReplacement();

            Symbol lastNonTerminator = sortedNonTerminators.get(sortedNonTerminators.size() - 1);

            return Grammar.create(
                    lastNonTerminator,
                    Production.create(
                            lastNonTerminator,
                            primaryProductionMap.get(lastNonTerminator)
                    )
            );
        }

        /**
         * 初始化一些字段，包括非终结符集合，Production映射表
         */
        private void init() {
            for (Production production : grammar.getProductions()) {
                Symbol nonTerminator = production.getLeft();

                assertFalse(productionMap.containsKey(nonTerminator));

                this.sortedNonTerminators.add(nonTerminator);
                productionMap.put(nonTerminator, production);
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
                    for (Symbol fromSymbol : pp.getSymbols()) {
                        if (!fromSymbol.isTerminator()) {
                            assertTrue(edges.containsKey(fromSymbol));

                            edges.get(fromSymbol).add(toSymbol);

                            degrees.put(toSymbol, degrees.get(toSymbol) + 1);
                        }
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
         * 检查正则文法第一条语句是否合法
         */
        private void checkIfFirstProductionContainsNonTerminator() {
            Production production = productionMap.get(sortedNonTerminators.get(0));

            assertTrue(production.getRight().size() == 1);
            PrimaryProduction pp = production.getRight().get(0);

            for (Symbol symbol : pp.getSymbols()) {
                // 正则语法第一条产生式不能包含非终结符
                assertTrue(symbol.isTerminator());
            }
        }

        /**
         * 用第1~(i-1) 个产生式替换掉第i个产生式中的非终结符
         */
        private void iterativeReplacement() {
            for (Symbol nonTerminator : sortedNonTerminators) {

                List<Symbol> modifiedSymbols = new ArrayList<>();

                Production p = productionMap.get(nonTerminator);
                assertTrue(p.getRight().size() == 1);
                PrimaryProduction pp = p.getRight().get(0);

                for (Symbol symbol : pp.getSymbols()) {
                    if (!symbol.isTerminator()) {
                        modifiedSymbols.add(SymbolUtils._leftSmallParenthesis);

                        // 当前产生式包含的非终结符必定在之前循环中已经出现过
                        assertNotNull(primaryProductionMap.get(symbol));

                        modifiedSymbols.addAll(primaryProductionMap.get(symbol).getSymbols());

                        modifiedSymbols.add(SymbolUtils._rightSmallParenthesis);
                    } else {
                        modifiedSymbols.add(symbol);
                    }
                }

                primaryProductionMap.put(nonTerminator, PrimaryProduction.create(modifiedSymbols));
            }
        }
    }
}
