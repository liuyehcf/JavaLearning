package org.liuyehcf.compile.definition;

import java.util.Arrays;
import java.util.List;

/**
 * 文法符号串
 */
public class SymbolSequence {
    // 符号串
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
