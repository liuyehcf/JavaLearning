package org.liuyehcf.compile.definition;

import org.liuyehcf.compile.utils.ListUtils;

import java.util.List;

/**
 * 文法符号串
 */
public class PrimaryProduction {
    // 文法符号串
    private final List<Symbol> symbols;

    private PrimaryProduction(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    public static PrimaryProduction create(Symbol... symbols) {
        return new PrimaryProduction(ListUtils.of(symbols));
    }

    public static PrimaryProduction create(List<Symbol> symbols) {
        return new PrimaryProduction(symbols);
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
        if (obj instanceof PrimaryProduction) {
            PrimaryProduction other = (PrimaryProduction) obj;
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
