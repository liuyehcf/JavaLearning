package org.liuyehcf.grammar;

import org.liuyehcf.grammar.core.MorphemeType;
import org.liuyehcf.grammar.core.definition.*;

import static org.liuyehcf.grammar.cfg.TestLexicalAnalyzer.getIdRegex;
import static org.liuyehcf.grammar.core.definition.Symbol.*;

public abstract class GrammarCase {

    public static abstract class LL1_CASE1 {

        public static Grammar GRAMMAR = Grammar.create(
                createNonTerminator("E"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("E"),
                                SymbolString.create(
                                        createNonTerminator("T"),
                                        createNonTerminator("E′")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("E′"),
                                SymbolString.create(
                                        createTerminator("+"),
                                        createNonTerminator("T"),
                                        createNonTerminator("E′")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("E′"),
                                SymbolString.create(
                                        Symbol.EPSILON
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("T"),
                                SymbolString.create(
                                        createNonTerminator("F"),
                                        createNonTerminator("T′")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("T′"),
                                SymbolString.create(
                                        createTerminator("*"),
                                        createNonTerminator("F"),
                                        createNonTerminator("T′")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("T′"),
                                SymbolString.create(
                                        Symbol.EPSILON
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("F"),
                                SymbolString.create(
                                        createTerminator("("),
                                        createNonTerminator("E"),
                                        createTerminator(")")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("F"),
                                SymbolString.create(
                                        createRegexTerminator("id")
                                )
                        )
                )
        );

        public static LexicalAnalyzer JDK_LEXICAL_ANALYZER = JdkLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .build();

        public static LexicalAnalyzer NFA_LEXICAL_ANALYZER = NfaLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .build();

        public static String[] TRUE_CASES = new String[]{
                "(a)",
                "a+b*c",
                "(a+b)*c",
                "(a+b*c+(d+e)*(f*g))",
                "(a+b*cA+(name+e)*(age*hello))",
        };

        public static String[] FALSE_CASES = new String[]{
                "",
                "a+0*c",
                "(a+b)*1",
                "(a+b*2B+(d+e)*(3*g))",
                "(a+b*1A+(name+e)*(age*hello))",
                "()",
        };
    }

    public static abstract class LL1_CASE2 {

        public static LexicalAnalyzer JDK_LEXICAL_ANALYZER = JdkLexicalAnalyzer.builder()
                .addMorpheme("program ")
                .addMorpheme(":")
                .addMorpheme(";")
                .addMorpheme(" end")
                .addMorpheme("id")
                .addMorpheme(",")
                .addMorpheme("s")
                .addMorpheme("real")
                .addMorpheme("int")
                .build();

        public static LexicalAnalyzer NFA_LEXICAL_ANALYZER = NfaLexicalAnalyzer.builder()
                .addMorpheme("program ")
                .addMorpheme(":")
                .addMorpheme(";")
                .addMorpheme(" end")
                .addMorpheme("id")
                .addMorpheme(",")
                .addMorpheme("s")
                .addMorpheme("real")
                .addMorpheme("int")
                .build();

        public static String[] TRUE_CASES = new String[]{
                "program id,id,id:real;s;s end",
                "program id:int;s;s end",
                "program id,id:int;s end",
        };

        public static String[] FALSE_CASES = new String[]{
                "",
                "id,id,id:real;s;s end",
                "program :real;s;s end",
                "program id,id,id:double;s;s end",
                "program id,id,id:real;s;s",
                "program id,id,id:real;s,s end",
        };

        private static String PROGRAM = "PROGRAM";
        private static String DECLIST = "DECLIST";
        private static String DECLISTN = "DECLISTN";
        private static String STLIST = "STLIST";
        private static String STLISTN = "STLISTN";
        private static String TYPE = "TYPE";

        public static Grammar GRAMMAR = Grammar.create(
                createNonTerminator(PROGRAM),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator(PROGRAM),
                                SymbolString.create(
                                        createTerminator("program "),
                                        createNonTerminator(DECLIST),
                                        createTerminator(":"),
                                        createNonTerminator(TYPE),
                                        createTerminator(";"),
                                        createNonTerminator(STLIST),
                                        createTerminator(" end")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator(DECLIST),
                                SymbolString.create(
                                        createTerminator("id"),
                                        createNonTerminator(DECLISTN)
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator(DECLISTN),
                                SymbolString.create(
                                        createTerminator(","),
                                        createTerminator("id"),
                                        createNonTerminator(DECLISTN)
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator(DECLISTN),
                                SymbolString.create(
                                        Symbol.EPSILON
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator(STLIST),
                                SymbolString.create(
                                        createTerminator("s"),
                                        createNonTerminator(STLISTN)
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator(STLISTN),
                                SymbolString.create(
                                        createTerminator(";"),
                                        createTerminator("s"),
                                        createNonTerminator(STLISTN)
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator(STLISTN),
                                SymbolString.create(
                                        Symbol.EPSILON
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator(TYPE),
                                SymbolString.create(
                                        createTerminator("real")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator(TYPE),
                                SymbolString.create(
                                        createTerminator("int")
                                )
                        )
                )
        );


    }

    public static abstract class LL1_CASE3 {

        public static Grammar GRAMMAR = Grammar.create(
                createNonTerminator("A"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createTerminator("b")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createTerminator("b"),
                                        createTerminator("c")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createTerminator("b"),
                                        createTerminator("c"),
                                        createTerminator("d")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("b")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("b"),
                                        createTerminator("c")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("b"),
                                        createTerminator("c"),
                                        createTerminator("d")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("c")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("c"),
                                        createTerminator("d")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("d")
                                )
                        )
                )
        );

        public static LexicalAnalyzer JDK_LEXICAL_ANALYZER = JdkLexicalAnalyzer.builder()
                .addMorpheme("a")
                .addMorpheme("ab")
                .addMorpheme("abc")
                .addMorpheme("abcd")
                .addMorpheme("b")
                .addMorpheme("bc")
                .addMorpheme("bcd")
                .addMorpheme("c")
                .addMorpheme("cd")
                .addMorpheme("d")
                .build();

        public static LexicalAnalyzer NFA_LEXICAL_ANALYZER = NfaLexicalAnalyzer.builder()
                .addMorpheme("a")
                .addMorpheme("ab")
                .addMorpheme("abc")
                .addMorpheme("abcd")
                .addMorpheme("b")
                .addMorpheme("bc")
                .addMorpheme("bcd")
                .addMorpheme("c")
                .addMorpheme("cd")
                .addMorpheme("d")
                .build();

        public static String[] TRUE_CASES = new String[]{
                "a",
                "ab",
                "abc",
                "abcd",
                "b",
                "bc",
                "bcd",
                "c",
                "cd",
                "d",
        };

        public static String[] FALSE_CASES = new String[]{
                "",
                "e",
                "ba",
                "ac",
                "bdc",
        };
    }

    public static abstract class LR0_CASE1 {

        public static Grammar GRAMMAR = Grammar.create(
                createNonTerminator("S"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("S"),
                                SymbolString.create(
                                        createNonTerminator("B"),
                                        createNonTerminator("B")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("B"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createNonTerminator("B")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("B"),
                                SymbolString.create(
                                        createTerminator("b")
                                )
                        )
                )
        );

        public static LexicalAnalyzer JDK_LEXICAL_ANALYZER = JdkLexicalAnalyzer.builder()
                .addMorpheme("a")
                .addMorpheme("b")
                .build();

        public static LexicalAnalyzer NFA_LEXICAL_ANALYZER = NfaLexicalAnalyzer.builder()
                .addMorpheme("a")
                .addMorpheme("b")
                .build();

        public static String[] TRUE_CASES = new String[]{
                "bab",
                "bb",
                "aaaabab",
        };

        public static String[] FALSE_CASES = new String[]{
                "",
                "a",
                "b",
                "aba",
        };
    }

    public static abstract class SLR_CASE1 {

        public static Grammar GRAMMAR = Grammar.create(
                createNonTerminator("E"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("E"),
                                SymbolString.create(
                                        createNonTerminator("E"),
                                        createTerminator("+"),
                                        createNonTerminator("T")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("E"),
                                SymbolString.create(
                                        createNonTerminator("T")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("T"),
                                SymbolString.create(
                                        createNonTerminator("T"),
                                        createTerminator("*"),
                                        createNonTerminator("F")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("T"),
                                SymbolString.create(
                                        createNonTerminator("F")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("F"),
                                SymbolString.create(
                                        createTerminator("("),
                                        createNonTerminator("E"),
                                        createTerminator(")")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("F"),
                                SymbolString.create(
                                        createRegexTerminator("id")
                                )
                        )
                )
        );

        public static LexicalAnalyzer JDK_LEXICAL_ANALYZER = JdkLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("*")
                .addMorpheme("+")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .build();

        public static LexicalAnalyzer NFA_LEXICAL_ANALYZER = NfaLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("*")
                .addMorpheme("+")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .build();

        public static String[] TRUE_CASES = new String[]{
                "a",
                "a1+a2",
                "a+b*c+d",
                "a+b+c+d",
                "a+(b*c)+d",
                "(a+b*c)+d",
                "(a+b+c+d)",
        };

        public static String[] FALSE_CASES = new String[]{
                "",
                "a1-a2",
                "a-b*c+d",
                "a+(b-c)+d",
                "(a+b-c)+d",
        };
    }

    public static abstract class SLR_CASE2 {

        public static Grammar GRAMMAR = Grammar.create(
                createNonTerminator("T"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("T"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createNonTerminator("B"),
                                        createTerminator("d")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("T"),
                                SymbolString.create(
                                        EPSILON
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("B"),
                                SymbolString.create(
                                        createNonTerminator("T"),
                                        createTerminator("b")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("B"),
                                SymbolString.create(
                                        EPSILON
                                )
                        )
                )
        );

        public static LexicalAnalyzer JDK_LEXICAL_ANALYZER = JdkLexicalAnalyzer.builder()
                .addMorpheme("a")
                .addMorpheme("b")
                .addMorpheme("d")
                .build();

        public static LexicalAnalyzer NFA_LEXICAL_ANALYZER = NfaLexicalAnalyzer.builder()
                .addMorpheme("a")
                .addMorpheme("b")
                .addMorpheme("d")
                .build();

        public static String[] TRUE_CASES = new String[]{
                "",
                "ad",
                "aadbd",
                "aaadbdbd",
        };

        public static String[] FALSE_CASES = new String[]{
                "a",
                "aadd",
        };
    }

    public static abstract class LR1_CASE1 {

        public static Grammar GRAMMAR = Grammar.create(
                createNonTerminator("S"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("S"),
                                SymbolString.create(
                                        createNonTerminator("L"),
                                        createTerminator("="),
                                        createNonTerminator("R")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("S"),
                                SymbolString.create(
                                        createNonTerminator("R")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("L"),
                                SymbolString.create(
                                        createTerminator("*"),
                                        createNonTerminator("R")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("L"),
                                SymbolString.create(
                                        createRegexTerminator("id")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("R"),
                                SymbolString.create(
                                        createNonTerminator("L")
                                )
                        )
                )
        );

        public static LexicalAnalyzer JDK_LEXICAL_ANALYZER = JdkLexicalAnalyzer.builder()
                .addMorpheme("*")
                .addMorpheme("=")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .build();

        public static LexicalAnalyzer NFA_LEXICAL_ANALYZER = NfaLexicalAnalyzer.builder()
                .addMorpheme("*")
                .addMorpheme("=")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .build();

        public static String[] TRUE_CASES = new String[]{
                "a1=b2",
                "a=*b",
                "a=**b",
                "*a=b",
                "*a=*b",
                "*a=**b",
                "**a=b",
                "**a=*b",
                "**a=**b",
        };

        public static String[] FALSE_CASES = new String[]{
                "",
                "*",
                "a==*b",
                "a*b"
        };
    }

    public static abstract class Ambiguity_CASE1 {

        public static Grammar GRAMMAR = Grammar.create(
                createNonTerminator("E"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("E"),
                                SymbolString.create(
                                        createNonTerminator("E"),
                                        createTerminator("+"),
                                        createNonTerminator("E")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("E"),
                                SymbolString.create(
                                        createNonTerminator("E"),
                                        createTerminator("*"),
                                        createNonTerminator("E")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("E"),
                                SymbolString.create(
                                        createTerminator("("),
                                        createNonTerminator("E"),
                                        createTerminator(")")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("E"),
                                SymbolString.create(
                                        createRegexTerminator("id")
                                )
                        )
                )
        );
        public static LexicalAnalyzer JDK_LEXICAL_ANALYZER = JdkLexicalAnalyzer.builder()
                .addMorpheme("*")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .addMorpheme("=")
                .build();

        public static LexicalAnalyzer NFA_LEXICAL_ANALYZER = NfaLexicalAnalyzer.builder()
                .addMorpheme("*")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .addMorpheme("=")
                .build();
    }

}
