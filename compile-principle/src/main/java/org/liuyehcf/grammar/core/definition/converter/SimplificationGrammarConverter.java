package org.liuyehcf.grammar.core.definition.converter;

import org.liuyehcf.grammar.core.definition.*;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.*;

/**
 * 转换成只有一个产生式的文法，一般用于正则文法
 */
public class SimplificationGrammarConverter extends AbstractGrammarConverter {

    // 从非终结符 -> PrimaryProduction 的映射表
    private Map<Symbol, SymbolString> symbolStringMap = new HashMap<>();

    // 按依赖关系排序的非终结符集合
    private List<Symbol> sortedNonTerminators = new ArrayList<>();

    // 从非终结符 -> Production 的映射表
    private Map<Symbol, Production> productionMap = new HashMap<>();

    public SimplificationGrammarConverter(Grammar originalGrammar) {
        super(originalGrammar);
    }

    @Override
    protected Grammar doConvert() {
        init();

        checkIfFirstProductionContainsNonTerminator();

        iterativeReplacement();

        Symbol lastNonTerminator = sortedNonTerminators.get(sortedNonTerminators.size() - 1);

        return Grammar.create(
                lastNonTerminator,
                Production.create(
                        PrimaryProduction.create(
                                lastNonTerminator,
                                symbolStringMap.get(lastNonTerminator)
                        )
                )
        );
    }

    /**
     * 初始化一些字段，包括非终结符集合，Production映射表
     */
    private void init() {
        for (Production _P : originalGrammar.getProductions()) {
            Symbol nonTerminator = _P.getLeft();

            assertFalse(productionMap.containsKey(nonTerminator));

            this.sortedNonTerminators.add(nonTerminator);
            productionMap.put(nonTerminator, _P);
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
                for (Symbol fromSymbol : _PP.getRight().getSymbols()) {
                    if (!fromSymbol.isTerminator()) {
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
     * 检查正则文法第一条语句是否合法 TODO
     */
    private void checkIfFirstProductionContainsNonTerminator() {
        Production _P = productionMap.get(sortedNonTerminators.get(0));

        assertTrue(_P.getPrimaryProductions().size() == 1);
        PrimaryProduction _PP = _P.getPrimaryProductions().get(0);

        for (Symbol symbol : _PP.getRight().getSymbols()) {
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

            Production _P = productionMap.get(nonTerminator);
            assertTrue(_P.getPrimaryProductions().size() == 1);
            PrimaryProduction _PP = _P.getPrimaryProductions().get(0);

            for (Symbol symbol : _PP.getRight().getSymbols()) {
                if (!symbol.isTerminator()) {
                    modifiedSymbols.add(SymbolUtils._leftSmallParenthesis);

                    // 当前产生式包含的非终结符必定在之前循环中已经出现过
                    assertNotNull(symbolStringMap.get(symbol));

                    modifiedSymbols.addAll(symbolStringMap.get(symbol).getSymbols());

                    modifiedSymbols.add(SymbolUtils._rightSmallParenthesis);
                } else {
                    modifiedSymbols.add(symbol);
                }
            }

            symbolStringMap.put(nonTerminator, SymbolString.create(modifiedSymbols));
        }
    }
}
