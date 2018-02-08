package org.liuyehcf.algorithm.compile.grammar.regex.symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Liuye on 2017/10/21.
 */
public class Symbol {
    public static final Symbol _Epsilon = createAlphabetSymbol("Epsilon");
    private static List<Symbol> alphabetSymbols = null;
    private static List<Symbol> alphabetSymbolsMatchesAny = null;
    public static final Symbol _any = getAlphabetSymbolWithChar('.');
    public static final Symbol _or = getAlphabetSymbolWithChar('|');
    public static final Symbol _star = getAlphabetSymbolWithChar('*');
    public static final Symbol _add = getAlphabetSymbolWithChar('+');
    public static final Symbol _escaped = getAlphabetSymbolWithChar('\\');
    public static final Symbol _leftMiddleParenthesis = getAlphabetSymbolWithChar('[');
    public static final Symbol _rightMiddleParenthesis = getAlphabetSymbolWithChar(']');
    public static final Symbol _middleParenthesisNot = getAlphabetSymbolWithChar('^');
    public static final Symbol _leftSmallParenthesis = getAlphabetSymbolWithChar('(');
    public static final Symbol _rightSmallParenthesis = getAlphabetSymbolWithChar(')');
    private final String symbol;
    private final boolean isOfAlphabet;

    public Symbol(String symbol, boolean isOfAlphabet) {
        this.symbol = symbol;
        this.isOfAlphabet = isOfAlphabet;
    }

    public static Symbol getAlphabetSymbolWithChar(char symbol) {
        if (alphabetSymbols == null) {
            alphabetSymbols = new ArrayList<>();
            alphabetSymbolsMatchesAny = new ArrayList<>();
            for (char c = 0; c < 256; c++) {
                alphabetSymbols.add(new Symbol("" + c, true));
                // jump over undefined chars
                if (isLegalCharMatchesAny(c)) {
                    alphabetSymbolsMatchesAny.add(alphabetSymbols.get(c));
                }
            }
        }
        return alphabetSymbols.get(symbol);
    }

    public static boolean isLegalCharMatchesAny(char c) {
        return c != 10 && c != 13 && c != 133;
    }

    private static Symbol createAlphabetSymbol(String symbol) {
        return new Symbol(symbol, true);
    }

    public static Symbol createNonAlphabetSymbol(String symbol) {
        return new Symbol(symbol, false);
    }

    public static List<Symbol> getAlphabetSymbols() {
        return alphabetSymbols;
    }

    public static List<Symbol> getAlphabetSymbolsMatchesAny() {
        return alphabetSymbolsMatchesAny;
    }

    public static Set<Symbol> getOppositeSymbols(Set<Symbol> excludedSymbols) {
        Set<Symbol> oppositeSymbols = new HashSet<>();
        for (Symbol symbol : getAlphabetSymbols()) {
            oppositeSymbols.add(symbol);
        }
        oppositeSymbols.removeAll(excludedSymbols);
        return oppositeSymbols;
    }

    public String getSymbol() {
        return symbol;
    }

    public char getChar() {
        assert symbol.length() == 1;
        return symbol.charAt(0);
    }

    public boolean isOfAlphabet() {
        return isOfAlphabet;
    }

    protected String getTypeName() {
        return isOfAlphabet ? "Alphabet" : "NonAlphabet";
    }

    @Override
    public String toString() {
        return this.symbol;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.symbol.equals(((Symbol) obj).symbol)
                && this.isOfAlphabet() == ((Symbol) obj).isOfAlphabet;

    }
}

