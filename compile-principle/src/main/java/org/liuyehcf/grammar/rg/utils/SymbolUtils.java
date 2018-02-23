package org.liuyehcf.grammar.rg.utils;

import org.liuyehcf.grammar.core.definition.Symbol;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.liuyehcf.grammar.core.definition.Symbol.createTerminator;
import static org.liuyehcf.grammar.utils.AssertUtils.assertTrue;

public abstract class SymbolUtils {

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


    public static Symbol getAlphabetSymbolWithChar(char symbol) {
        if (alphabetSymbols == null) {
            alphabetSymbols = new ArrayList<>();
            alphabetSymbolsMatchesAny = new ArrayList<>();
            for (char c = 0; c < 256; c++) {
                alphabetSymbols.add(createTerminator("" + c));
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

    public static char getChar(Symbol symbol) {
        assertTrue(symbol.getValue().length() == 1);
        return symbol.getValue().charAt(0);
    }
}
