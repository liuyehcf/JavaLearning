package org.liuyehcf.algorithm.compile.grammar.regex.composition;

import org.liuyehcf.algorithm.compile.grammar.regex.symbol.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liuye on 2017/10/21.
 */
public class Grammar {
    private static Symbol DEFAULT_NON_TERMINATOR = Symbol.createNonAlphabetSymbol("DEFAULT_NON_TERMINATOR");
    Map<Symbol, PrimaryProduction> finalSymbolSymbolStringMap;
    private List<Symbol> nonTerminator;
    private Map<Symbol, Production> nonAlphabetSymbolProductionMap;
    private PrimaryProduction finalSymbolString;

    public Grammar(Production... productions) {
        nonTerminator = new ArrayList<>();
        nonAlphabetSymbolProductionMap = new HashMap<>();
        for (Production production : productions) {
            Symbol nonAlphabetSymbol = production.getLeft();
            if (nonAlphabetSymbolProductionMap.containsKey(nonAlphabetSymbol)) throw new RuntimeException();
            nonTerminator.add(nonAlphabetSymbol);
            nonAlphabetSymbolProductionMap.put(nonAlphabetSymbol, production);
        }
    }

    public static Grammar createGrammarDefinitionOfNormalRegex(String regex) {
        Grammar grammarDefinition = new Grammar(
                new Production(DEFAULT_NON_TERMINATOR, new PrimaryProduction(regex))
        );

        return grammarDefinition;
    }

    public PrimaryProduction getFinalSymbolString() {
        if (finalSymbolString == null) {
            parseFinalSymbolString();
        }
        return finalSymbolString;
    }

    public void parseFinalSymbolString() {

        checkIfFirstProductionHasNonAlphabetSymbol();

        initFinalSymbolStringMap();

        Symbol lastNonAlphabetSymbol = nonTerminator.get(nonTerminator.size() - 1);
        this.finalSymbolString = finalSymbolSymbolStringMap.get(lastNonAlphabetSymbol);
    }

    private void checkIfFirstProductionHasNonAlphabetSymbol() {
        Production production = nonAlphabetSymbolProductionMap.get(nonTerminator.get(0));

        PrimaryProduction symbolString = production.getRight();

        for (Symbol symbol : symbolString.getSymbols()) {
            if (!symbol.isTerminator())
                throw new RuntimeException("正则语法第一条产生式包含非字母表字符");
        }
    }

    private void initFinalSymbolStringMap() {
        finalSymbolSymbolStringMap = new HashMap<>();
        for (int i = 0; i < nonTerminator.size(); i++) {
            List<Symbol> finalSymbolsOfCurProduction = new ArrayList<>();
            Symbol nonAlphabetSymbolOfCurProduction = nonTerminator.get(i);

            Production curProduction = nonAlphabetSymbolProductionMap.get(nonAlphabetSymbolOfCurProduction);
            for (Symbol symbol : curProduction.getRight().getSymbols()) {
                if (!symbol.isTerminator()) {
                    finalSymbolsOfCurProduction.add(Symbol._leftSmallParenthesis);
                    finalSymbolsOfCurProduction.addAll(finalSymbolSymbolStringMap.get(symbol).getSymbols());
                    finalSymbolsOfCurProduction.add(Symbol._rightSmallParenthesis);
                } else {
                    finalSymbolsOfCurProduction.add(symbol);
                }
            }

            finalSymbolSymbolStringMap.put(nonAlphabetSymbolOfCurProduction, new PrimaryProduction(finalSymbolsOfCurProduction));
        }
    }

    @Override
    public String toString() {
        String s = "";
        for (Symbol symbol : nonTerminator) {
            s += nonAlphabetSymbolProductionMap.get(symbol).toString();
        }
        return s;
    }
}
