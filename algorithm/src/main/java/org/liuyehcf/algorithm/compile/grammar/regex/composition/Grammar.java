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
    private static Symbol NONE_NON_ALPHABET = Symbol.createNonAlphabetSymbol("NONE_NON_ALPHABET");
    Map<Symbol, PrimeProduction> finalSymbolSymbolStringMap;
    private List<Symbol> nonAlphabetSymbols;
    private Map<Symbol, Production> nonAlphabetSymbolProductionMap;
    private PrimeProduction finalSymbolString;

    public Grammar(Production... productions) {
        nonAlphabetSymbols = new ArrayList<>();
        nonAlphabetSymbolProductionMap = new HashMap<>();
        for (Production production : productions) {
            Symbol nonAlphabetSymbol = production.getLeft();
            if (nonAlphabetSymbolProductionMap.containsKey(nonAlphabetSymbol)) throw new RuntimeException();
            nonAlphabetSymbols.add(nonAlphabetSymbol);
            nonAlphabetSymbolProductionMap.put(nonAlphabetSymbol, production);
        }
    }

    public static Grammar createGrammarDefinitionOfNormalRegex(String regex) {
        Grammar grammarDefinition = new Grammar(
                new Production(NONE_NON_ALPHABET, new PrimeProduction(regex))
        );

        return grammarDefinition;
    }

    public PrimeProduction getFinalSymbolString() {
        if (finalSymbolString == null) {
            parseFinalSymbolString();
        }
        return finalSymbolString;
    }

    public void parseFinalSymbolString() {

        checkIfFirstProductionHasNonAlphabetSymbol();

        initFinalSymbolStringMap();

        Symbol lastNonAlphabetSymbol = nonAlphabetSymbols.get(nonAlphabetSymbols.size() - 1);
        this.finalSymbolString = finalSymbolSymbolStringMap.get(lastNonAlphabetSymbol);
    }

    private void checkIfFirstProductionHasNonAlphabetSymbol() {
        Production production = nonAlphabetSymbolProductionMap.get(nonAlphabetSymbols.get(0));

        PrimeProduction symbolString = production.getRight();

        for (Symbol symbol : symbolString.getSymbols()) {
            if (!symbol.isTerminator())
                throw new RuntimeException("正则语法第一条产生式包含非字母表字符");
        }
    }

    private void initFinalSymbolStringMap() {
        finalSymbolSymbolStringMap = new HashMap<>();
        for (int i = 0; i < nonAlphabetSymbols.size(); i++) {
            List<Symbol> finalSymbolsOfCurProduction = new ArrayList<>();
            Symbol nonAlphabetSymbolOfCurProduction = nonAlphabetSymbols.get(i);

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

            finalSymbolSymbolStringMap.put(nonAlphabetSymbolOfCurProduction, new PrimeProduction(finalSymbolsOfCurProduction));
        }
    }

    @Override
    public String toString() {
        String s = "";
        for (Symbol symbol : nonAlphabetSymbols) {
            s += nonAlphabetSymbolProductionMap.get(symbol).toString();
        }
        return s;
    }
}
