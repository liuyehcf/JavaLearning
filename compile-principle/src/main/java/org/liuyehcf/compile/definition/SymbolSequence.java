package org.liuyehcf.compile.definition;

import java.util.Arrays;
import java.util.List;

/**
 * 文法符号串
 */
public class SymbolSequence {
    // 文法符号串
    private final List<Symbol> symbols;

    private SymbolSequence(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    public static SymbolSequence createSymbolSequence(Symbol... symbols) {
        return new SymbolSequence(Arrays.asList(symbols));
    }

    public static SymbolSequence createSymbolSequence(List<Symbol> symbols) {
        return new SymbolSequence(symbols);
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public String toJSONString() {
        return '{' +
                "\"symbols\":" + symbols +
                '}';
    }

    public String toReadableJSONString() {
        StringBuilder sb = new StringBuilder();

        for (Symbol symbol : symbols) {
            sb.append(symbol.toReadableJSONString());
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toReadableJSONString();
    }
}
