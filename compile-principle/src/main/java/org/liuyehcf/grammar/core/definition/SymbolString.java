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

    // 状态索引（仅用于LR文法）
    private final int indexOfDot;

    private SymbolString(List<Symbol> symbols, int indexOfDot) {
        this.symbols = Collections.unmodifiableList(symbols);
        this.indexOfDot = indexOfDot;
    }

    public static SymbolString create(Symbol... symbols) {
        return new SymbolString(ListUtils.of(symbols), -1);
    }

    public static SymbolString create(List<Symbol> symbols, int indexOfDot) {
        return new SymbolString(symbols, indexOfDot);
    }

    public static SymbolString create(List<Symbol> symbols) {
        return new SymbolString(symbols, -1);
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public int getIndexOfDot() {
        return indexOfDot;
    }

    public String toJSONString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < symbols.size(); i++) {
            if (i != 0) {
                sb.append(' ');
            }

            if (indexOfDot == i) {
                sb.append("· ");
            }
            sb.append(symbols.get(i).toJSONString());
        }

        if (indexOfDot == symbols.size()) {
            sb.append(" ·");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    @Override
    public int hashCode() {
        int hash = 0;

        hash += indexOfDot;

        for (Symbol symbol : symbols) {
            hash += symbol.hashCode();
        }

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SymbolString) {
            SymbolString that = (SymbolString) obj;
            if (that.indexOfDot == this.indexOfDot
                    && that.symbols.size() == this.symbols.size()) {
                for (int i = 0; i < this.symbols.size(); i++) {
                    if (!that.symbols.get(i).equals(this.symbols.get(i))) {
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