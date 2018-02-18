package org.liuyehcf.compile.utils;

import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.Production;
import org.liuyehcf.compile.definition.Symbol;
import org.liuyehcf.compile.definition.SymbolSequence;

import java.util.ArrayList;
import java.util.List;

import static org.liuyehcf.compile.utils.AssertUtils.assertTrue;

public class DefinitionUtils {
    public static Symbol createTerminator(String value) {
        return new Symbol(true, value);
    }

    public static Symbol createNonTerminator(String value) {
        return new Symbol(false, value);
    }

    public static SymbolSequence createSymbolSequence(Symbol... symbols) {
        return new SymbolSequence(symbols);
    }

    public static SymbolSequence createSymbolSequence(List<Symbol> symbols) {
        return new SymbolSequence(symbols);
    }

    public static Production createProduction(Symbol left, SymbolSequence... right) {
        return new Production(left, right);
    }

    public static Production createProduction(Symbol left, List<SymbolSequence> right) {
        return new Production(left, right);
    }

    public static Grammar createGrammar(Production... productions) {
        return new Grammar(productions);
    }

    public static Grammar createGrammar(List<Production> productions) {
        return new Grammar(productions);
    }

    public static Production parallelProduction(Production p1, Production p2) {
        assertTrue(p1.getLeft().equals(p2.getLeft()));

        List<SymbolSequence> right = new ArrayList<>(p1.getRight());
        right.addAll(p2.getRight());

        return createProduction(
                p1.getLeft(),
                right);
    }
}
