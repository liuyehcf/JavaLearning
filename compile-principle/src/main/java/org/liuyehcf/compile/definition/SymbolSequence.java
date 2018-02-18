package org.liuyehcf.compile.definition;

import java.util.Arrays;
import java.util.List;

public class SymbolSequence {
    private final List<Symbol> symbols;

    public SymbolSequence(Symbol... symbols) {
        this.symbols = Arrays.asList(symbols);
    }

    public SymbolSequence(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    @Override
    public String toString() {
        return "{" +
                "\"symbols\":" + symbols +
                '}';
    }
}
