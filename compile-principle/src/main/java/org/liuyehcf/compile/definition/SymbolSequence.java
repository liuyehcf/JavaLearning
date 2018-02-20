package org.liuyehcf.compile.definition;

import java.util.Arrays;
import java.util.List;

import static org.liuyehcf.compile.utils.AssertUtils.assertFalse;

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
