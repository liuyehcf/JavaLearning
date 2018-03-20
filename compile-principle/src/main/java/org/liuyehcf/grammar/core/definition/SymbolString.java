package org.liuyehcf.grammar.core.definition;

import org.liuyehcf.grammar.utils.ListUtils;

import java.util.Collections;
import java.util.List;

/**
 * 文法符号串
 */
public class SymbolString {
    // 文法符号串
    private final List<Symbol> symbols;

    private SymbolString(List<Symbol> symbols) {
        this.symbols = Collections.unmodifiableList(symbols);
    }

    public static SymbolString create(Symbol... symbols) {
        return new SymbolString(ListUtils.of(symbols));
    }

    public static SymbolString create(List<Symbol> symbols) {
        return new SymbolString(symbols);
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

        for (int i = 0; i < symbols.size(); i++) {
            if (i != 0) {
                sb.append(' ');
            }
            sb.append(symbols.get(i).toReadableJSONString());
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toReadableJSONString();
    }

    @Override
    public int hashCode() {
        int hash = 0;

        for (Symbol symbol : symbols) {
            hash += symbol.hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SymbolString) {
            SymbolString other = (SymbolString) obj;
            if (other.symbols.size() == this.symbols.size()) {
                for (int i = 0; i < this.symbols.size(); i++) {
                    if (!other.symbols.get(i).equals(this.symbols.get(i))) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
