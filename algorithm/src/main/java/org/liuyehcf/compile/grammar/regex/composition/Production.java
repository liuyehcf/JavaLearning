package org.liuyehcf.compile.grammar.regex.composition;

import org.liuyehcf.compile.grammar.regex.symbol.Symbol;

/**
 * Created by Liuye on 2017/10/21.
 */
public class Production {
    private final Symbol nonAlphabetSymbol;
    private final SymbolString symbolString;

    public Production(Symbol nonAlphabetSymbol,
                      SymbolString symbolString) {
        this.nonAlphabetSymbol = nonAlphabetSymbol;
        this.symbolString = symbolString;
    }

    public Symbol getNonAlphabetSymbol() {
        return nonAlphabetSymbol;
    }

    public SymbolString getSymbolString() {
        return symbolString;
    }

    @Override
    public String toString() {
        String s = "";
        s += nonAlphabetSymbol.toString();
        s += " --> ";
        s += symbolString.toString();
        s += "\n";
        return s;
    }
}

