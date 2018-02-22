package org.liuyehcf.algorithm.compile.grammar.regex.composition;

import org.liuyehcf.algorithm.compile.grammar.regex.symbol.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liuye on 2017/10/21.
 */
public class PrimaryProduction {
    private final List<Symbol> symbols;

    public PrimaryProduction(Object... objects) {
        this.symbols = new ArrayList<>();
        for (Object obj : objects) {
            if (obj instanceof Character) {
                addCharToSymbols((char) obj);
            } else if (obj instanceof String) {
                for (char c : ((String) obj).toCharArray()) {
                    addCharToSymbols(c);
                }
            } else if (obj instanceof Symbol) {
                addNonAlphabetSymbol((Symbol) obj);
            }
        }
    }

    public PrimaryProduction(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    private void addCharToSymbols(char c) {
        this.symbols.add(Symbol.getAlphabetSymbolWithChar(c));
    }

    private void addNonAlphabetSymbol(Symbol symbol) {
        assert !symbol.isTerminator();
        this.symbols.add(symbol);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : symbols) {
            sb.append(symbol);
        }
        return sb.toString();
    }
}
