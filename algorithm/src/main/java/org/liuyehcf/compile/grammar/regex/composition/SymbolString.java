package org.liuyehcf.compile.grammar.regex.composition;

import org.liuyehcf.compile.grammar.regex.symbol.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liuye on 2017/10/21.
 */
public class SymbolString {
    private final List<Symbol> symbols;

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public SymbolString(Object... objects) {
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

    private void addCharToSymbols(char c) {
        this.symbols.add(Symbol.getAlphabetSymbolWithChar(c));
    }

    private void addNonAlphabetSymbol(Symbol symbol) {
        assert !symbol.isOfAlphabet();
        this.symbols.add(symbol);
    }

    public SymbolString(List<Symbol> symbols) {
        this.symbols = symbols;
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
