package org.liuyehcf.grammar.rg.utils;

import org.liuyehcf.grammar.definition.Symbol;

import java.util.*;

public abstract class EscapedUtil {
    private static List<Symbol> escaped_any;
    private static List<Symbol> escaped_or;
    private static List<Symbol> escaped_star;
    private static List<Symbol> escaped_add;
    private static List<Symbol> escaped_leftMiddleParenthesis;
    private static List<Symbol> escaped_rightMiddleParenthesis;
    private static List<Symbol> escaped_leftSmallParenthesis;
    private static List<Symbol> escaped_rightSmallParenthesis;

    private static List<Symbol> escaped_d;
    private static List<Symbol> escaped_D;
    private static List<Symbol> escaped_w;
    private static List<Symbol> escaped_W;
    private static List<Symbol> escaped_s;
    private static List<Symbol> escaped_S;

    static {
        initializeEscaped_any();
        initializeEscaped_or();
        initializeEscaped_star();
        initializeEscaped_add();
        initializeEscaped_leftMiddleParenthesis();
        initializeEscaped_rightMiddleParenthesis();
        initializeEscaped_leftSmallParenthesis();
        initializeEscaped_rightSmallParenthesis();
        initializeEscaped_d();
        initializeEscaped_D();
        initializeEscaped_w();
        initializeEscaped_W();
        initializeEscaped_s();
        initializeEscaped_S();
    }

    public static List<Symbol> getSymbolsOfEscapedChar(char c) {
        switch (c) {
            case '.':
                return escaped_any;
            case '|':
                return escaped_or;
            case '*':
                return escaped_star;
            case '+':
                return escaped_add;
            case '[':
                return escaped_leftMiddleParenthesis;
            case ']':
                return escaped_rightMiddleParenthesis;
            case '(':
                return escaped_leftSmallParenthesis;
            case ')':
                return escaped_rightSmallParenthesis;
            case 'd':
                return escaped_d;
            case 'D':
                return escaped_D;
            case 'w':
                return escaped_w;
            case 'W':
                return escaped_W;
            case 's':
                return escaped_s;
            case 'S':
                return escaped_S;
            default:
                throw new RuntimeException();
        }
    }

    public static List<Symbol> getSymbolsOfEscapedCharInMiddleParenthesis(char c) {
        switch (c) {
            case '[':
                return escaped_leftMiddleParenthesis;
            case ']':
                return escaped_rightMiddleParenthesis;
            case 'd':
                return escaped_d;
            case 'D':
                return escaped_D;
            case 'w':
                return escaped_w;
            case 'W':
                return escaped_W;
            case 's':
                return escaped_s;
            case 'S':
                return escaped_S;
            default:
                throw new RuntimeException();
        }
    }

    private static void initializeEscaped_any() {
        escaped_any = Collections.unmodifiableList(
                Arrays.asList(
                        SymbolUtils.getAlphabetSymbolWithChar('.')));
    }

    private static void initializeEscaped_or() {
        escaped_or = Collections.unmodifiableList(
                Arrays.asList(
                        SymbolUtils.getAlphabetSymbolWithChar('|')));
    }

    private static void initializeEscaped_star() {
        escaped_star = Collections.unmodifiableList(
                Arrays.asList(
                        SymbolUtils.getAlphabetSymbolWithChar('*')));
    }

    private static void initializeEscaped_add() {
        escaped_add = Collections.unmodifiableList(
                Arrays.asList(
                        SymbolUtils.getAlphabetSymbolWithChar('+')));
    }

    private static void initializeEscaped_leftMiddleParenthesis() {
        escaped_leftMiddleParenthesis = Collections.unmodifiableList(
                Arrays.asList(
                        SymbolUtils.getAlphabetSymbolWithChar('[')));
    }

    private static void initializeEscaped_rightMiddleParenthesis() {
        escaped_rightMiddleParenthesis = Collections.unmodifiableList(
                Arrays.asList(
                        SymbolUtils.getAlphabetSymbolWithChar(']')));
    }

    private static void initializeEscaped_leftSmallParenthesis() {
        escaped_leftSmallParenthesis = Collections.unmodifiableList(
                Arrays.asList(
                        SymbolUtils.getAlphabetSymbolWithChar('(')));
    }

    private static void initializeEscaped_rightSmallParenthesis() {
        escaped_rightSmallParenthesis = Collections.unmodifiableList(
                Arrays.asList(
                        SymbolUtils.getAlphabetSymbolWithChar(')')));
    }

    private static void initializeEscaped_d() {
        List<Symbol> symbols = new ArrayList<>();
        for (char c = '0'; c <= '9'; c++) {
            symbols.add(SymbolUtils.getAlphabetSymbolWithChar(c));
        }
        escaped_d = Collections.unmodifiableList(symbols);
    }

    private static void initializeEscaped_D() {
        Set<Symbol> symbols = new HashSet<>(SymbolUtils.getAlphabetSymbols());
        symbols.removeAll(escaped_d);
        escaped_D = Collections.unmodifiableList(new ArrayList<>(symbols));
    }

    private static void initializeEscaped_w() {
        List<Symbol> symbols = new ArrayList<>();
        for (char c = '0'; c <= '9'; c++) {
            symbols.add(SymbolUtils.getAlphabetSymbolWithChar(c));
        }
        for (char c = 'a'; c <= 'z'; c++) {
            symbols.add(SymbolUtils.getAlphabetSymbolWithChar(c));
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            symbols.add(SymbolUtils.getAlphabetSymbolWithChar(c));
        }
        symbols.add(SymbolUtils.getAlphabetSymbolWithChar('_'));
        escaped_w = Collections.unmodifiableList(symbols);
    }

    private static void initializeEscaped_W() {
        Set<Symbol> symbols = new HashSet<>(SymbolUtils.getAlphabetSymbols());
        symbols.removeAll(escaped_w);
        escaped_W = Collections.unmodifiableList(new ArrayList<>(symbols));
    }

    private static void initializeEscaped_s() {
        List<Symbol> symbols = new ArrayList<>();
        symbols.add(SymbolUtils.getAlphabetSymbolWithChar((char) 9));
        symbols.add(SymbolUtils.getAlphabetSymbolWithChar((char) 10));
        symbols.add(SymbolUtils.getAlphabetSymbolWithChar((char) 11));
        symbols.add(SymbolUtils.getAlphabetSymbolWithChar((char) 12));
        symbols.add(SymbolUtils.getAlphabetSymbolWithChar((char) 13));
        symbols.add(SymbolUtils.getAlphabetSymbolWithChar((char) 32));
        escaped_s = Collections.unmodifiableList(new ArrayList<>(symbols));
    }

    private static void initializeEscaped_S() {
        Set<Symbol> symbols = new HashSet<>(SymbolUtils.getAlphabetSymbols());
        symbols.removeAll(escaped_s);
        escaped_S = Collections.unmodifiableList(new ArrayList<>(symbols));
    }
}
