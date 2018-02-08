package org.liuyehcf.algorithm.compile.grammar.regex.composition;

import org.liuyehcf.algorithm.compile.grammar.regex.symbol.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Liuye on 2017/10/21.
 */
public class GrammarDefinition {
    private static Symbol NONE_NON_ALPHABET = Symbol.createNonAlphabetSymbol("NONE_NON_ALPHABET");
    Map<Symbol, SymbolString> finalSymbolSymbolStringMap;
    private List<Symbol> nonAlphabetSymbols;
    private Map<Symbol, Production> nonAlphabetSymbolProductionMap;
    private SymbolString finalSymbolString;

    public GrammarDefinition(Production... productions) {
        nonAlphabetSymbols = new ArrayList<>();
        nonAlphabetSymbolProductionMap = new HashMap<>();
        for (Production production : productions) {
            Symbol nonAlphabetSymbol = production.getNonAlphabetSymbol();
            if (nonAlphabetSymbolProductionMap.containsKey(nonAlphabetSymbol)) throw new RuntimeException();
            nonAlphabetSymbols.add(nonAlphabetSymbol);
            nonAlphabetSymbolProductionMap.put(nonAlphabetSymbol, production);
        }
    }

    public static GrammarDefinition createGrammarDefinitionOfNormalRegex(String regex) {
        GrammarDefinition grammarDefinition = new GrammarDefinition(
                new Production(NONE_NON_ALPHABET, new SymbolString(regex))
        );

        return grammarDefinition;
    }

    public SymbolString getFinalSymbolString() {
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

        SymbolString symbolString = production.getSymbolString();

        for (Symbol symbol : symbolString.getSymbols()) {
            if (!symbol.isOfAlphabet())
                throw new RuntimeException("正则语法第一条产生式包含非字母表字符");
        }
    }

    private void initFinalSymbolStringMap() {
        finalSymbolSymbolStringMap = new HashMap<>();
        for (int i = 0; i < nonAlphabetSymbols.size(); i++) {
            List<Symbol> finalSymbolsOfCurProduction = new ArrayList<>();
            Symbol nonAlphabetSymbolOfCurProduction = nonAlphabetSymbols.get(i);

            Production curProduction = nonAlphabetSymbolProductionMap.get(nonAlphabetSymbolOfCurProduction);
            for (Symbol symbol : curProduction.getSymbolString().getSymbols()) {
                if (!symbol.isOfAlphabet()) {
                    finalSymbolsOfCurProduction.add(Symbol._leftSmallParenthesis);
                    finalSymbolsOfCurProduction.addAll(finalSymbolSymbolStringMap.get(symbol).getSymbols());
                    finalSymbolsOfCurProduction.add(Symbol._rightSmallParenthesis);
                } else {
                    finalSymbolsOfCurProduction.add(symbol);
                }
            }

            finalSymbolSymbolStringMap.put(nonAlphabetSymbolOfCurProduction, new SymbolString(finalSymbolsOfCurProduction));
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
