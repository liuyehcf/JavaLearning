package org.liuyehcf.compile.definition;

import org.liuyehcf.compile.utils.ListUtils;

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

    public static SymbolSequence create(Symbol... symbols) {
        return new SymbolSequence(ListUtils.of(symbols));
    }

    public static SymbolSequence create(List<Symbol> symbols) {
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
