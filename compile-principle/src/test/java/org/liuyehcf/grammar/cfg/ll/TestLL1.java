package org.liuyehcf.grammar.cfg.ll;

import org.junit.Test;
import org.liuyehcf.grammar.JdkLexicalAnalyzer;
import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.NfaLexicalAnalyzer;
import org.liuyehcf.grammar.core.MorphemeType;
import org.liuyehcf.grammar.core.definition.*;

import static org.junit.Assert.*;
import static org.liuyehcf.grammar.cfg.TestLexicalAnalyzer.getIdRegex;
import static org.liuyehcf.grammar.core.definition.Symbol.*;

public class TestLL1 {
    @Test
    public void testFirstFollowSelectCase1() {
        Grammar grammar = createGrammar2();

        LLParser parser = new LL1(grammar, getDefaultLexicalAnalyzer());
        Grammar convertedGrammar = parser.getGrammar();

        assertEquals(
                "{\"productions\":[\"E′ → + T E′ | __EPSILON__\",\"T′ → * F T′ | __EPSILON__\",\"T → ( E ) T′ | id T′\",\"E → ( E ) T′ E′ | id T′ E′\",\"F → ( E ) | id\"]}",
                convertedGrammar.toReadableJSONString()
        );
        assertEquals(
                "{\"FIRST\":{\"terminator\":{\"__EPSILON__\":\"__EPSILON__\",\"(\":\"(\",\")\":\")\",\"*\":\"*\",\"+\":\"+\",\"id\":\"id\"},\"nonTerminator\":{\"E′\":\"__EPSILON__,+\",\"T′\":\"__EPSILON__,*\",\"T\":\"(,id\",\"E\":\"(,id\",\"F\":\"(,id\"}},\"FOLLOW\":{\"nonTerminator\":{\"E′\":\"),__DOLLAR__\",\"T′\":\"),+,__DOLLAR__\",\"T\":\"),+,__DOLLAR__\",\"E\":\"),__DOLLAR__\",\"F\":\"),*,+,__DOLLAR__\"}},\"SELECT\":{\"E′\":{\"E′ → + T E′\":\"+\",\"E′ → __EPSILON__\":\"),__DOLLAR__\"},\"T′\":{\"T′ → * F T′\":\"*\",\"T′ → __EPSILON__\":\"),+,__DOLLAR__\"},\"T\":{\"T → id T′\":\"id\",\"T → ( E ) T′\":\"(\"},\"E\":{\"E → ( E ) T′ E′\":\"(\",\"E → id T′ E′\":\"id\"},\"F\":{\"F → id\":\"id\",\"F → ( E )\":\"(\"}}}",
                parser.getStatus()
        );
        assertEquals(
                "| 非终结符\\终结符 | __EPSILON__ | ( | ) | * | + | id |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| E′ | \\ | \\ | E′ → __EPSILON__ | \\ | E′ → + T E′ | \\ |\n" +
                        "| T′ | \\ | \\ | T′ → __EPSILON__ | T′ → * F T′ | T′ → __EPSILON__ | \\ |\n" +
                        "| T | \\ | T → ( E ) T′ | \\ | \\ | \\ | T → id T′ |\n" +
                        "| E | \\ | E → ( E ) T′ E′ | \\ | \\ | \\ | E → id T′ E′ |\n" +
                        "| F | \\ | F → ( E ) | \\ | \\ | \\ | F → id |\n",
                parser.getForecastAnalysisTable()
        );
    }

    @Test
    public void testFirstFollowSelectCase2() {
        Grammar grammar = createGrammar3();

        LLParser parser = new LL1(grammar, getDefaultLexicalAnalyzer());
        Grammar convertedGrammar = parser.getGrammar();

        assertEquals(
                "{\"productions\":[\"PROGRAM → program DECLIST : TYPE ; STLIST end\",\"DECLISTN → , id DECLISTN | __EPSILON__\",\"STLIST → s STLISTN\",\"TYPE → real | int\",\"STLISTN → ; s STLISTN | __EPSILON__\",\"DECLIST → id DECLISTN\"]}",
                convertedGrammar.toReadableJSONString()
        );
        assertEquals(
                "{\"FIRST\":{\"terminator\":{\"__EPSILON__\":\"__EPSILON__\",\"s\":\"s\",\":\":\":\",\"end\":\"end\",\"program\":\"program\",\";\":\";\",\"id\":\"id\",\"real\":\"real\",\",\":\",\",\"int\":\"int\"},\"nonTerminator\":{\"PROGRAM\":\"program\",\"STLIST\":\"s\",\"DECLISTN\":\"__EPSILON__,,\",\"TYPE\":\"real,int\",\"STLISTN\":\"__EPSILON__,;\",\"DECLIST\":\"id\"}},\"FOLLOW\":{\"nonTerminator\":{\"PROGRAM\":\"__DOLLAR__\",\"STLIST\":\"end\",\"DECLISTN\":\":\",\"TYPE\":\";\",\"STLISTN\":\"end\",\"DECLIST\":\":\"}},\"SELECT\":{\"PROGRAM\":{\"PROGRAM → program DECLIST : TYPE ; STLIST end\":\"program\"},\"STLIST\":{\"STLIST → s STLISTN\":\"s\"},\"DECLISTN\":{\"DECLISTN → __EPSILON__\":\":\",\"DECLISTN → , id DECLISTN\":\",\"},\"TYPE\":{\"TYPE → real\":\"real\",\"TYPE → int\":\"int\"},\"STLISTN\":{\"STLISTN → __EPSILON__\":\"end\",\"STLISTN → ; s STLISTN\":\";\"},\"DECLIST\":{\"DECLIST → id DECLISTN\":\"id\"}}}",
                parser.getStatus()
        );
        assertEquals(
                "| 非终结符\\终结符 | __EPSILON__ | s | : | end | program | ; | id | real | , | int |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| PROGRAM | \\ | \\ | \\ | \\ | PROGRAM → program DECLIST : TYPE ; STLIST end | \\ | \\ | \\ | \\ | \\ |\n" +
                        "| STLIST | \\ | STLIST → s STLISTN | \\ | \\ | \\ | \\ | \\ | \\ | \\ | \\ |\n" +
                        "| DECLISTN | \\ | \\ | DECLISTN → __EPSILON__ | \\ | \\ | \\ | \\ | \\ | DECLISTN → , id DECLISTN | \\ |\n" +
                        "| TYPE | \\ | \\ | \\ | \\ | \\ | \\ | \\ | TYPE → real | \\ | TYPE → int |\n" +
                        "| STLISTN | \\ | \\ | \\ | STLISTN → __EPSILON__ | \\ | STLISTN → ; s STLISTN | \\ | \\ | \\ | \\ |\n" +
                        "| DECLIST | \\ | \\ | \\ | \\ | \\ | \\ | DECLIST → id DECLISTN | \\ | \\ | \\ |\n",
                parser.getForecastAnalysisTable()
        );
    }

    @Test
    public void testParseCase1WithJdkLexicalAnalyzer() {
        Grammar grammar = createGrammar2();

        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id")
                .build();

        LLParser parser = new LL1(grammar, analyzer);

        assertTrue(parser.matches("id+id*id"));
        assertTrue(parser.matches("(id+id)*id"));
        assertTrue(parser.matches("id+(id*id)"));
        assertTrue(parser.matches("(id)+(id*id)"));
    }

    @Test
    public void testParseCase1WithNfaLexicalAnalyzer() {
        Grammar grammar = createGrammar2();

        LexicalAnalyzer analyzer = NfaLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id")
                .build();

        LLParser parser = new LL1(grammar, analyzer);

        assertTrue(parser.matches("id+id*id"));
        assertTrue(parser.matches("(id+id)*id"));
        assertTrue(parser.matches("id+(id*id)"));
        assertTrue(parser.matches("(id)+(id*id)"));
    }

    @Test
    public void testParseCase2WithJdkLexicalAnalyzer() {
        Grammar grammar = createGrammar3();

        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("program")
                .addMorpheme(":")
                .addMorpheme(";")
                .addMorpheme("end")
                .addMorpheme("id")
                .addMorpheme(",")
                .addMorpheme("s")
                .addMorpheme("real")
                .addMorpheme("int")
                .build();

        LLParser parser = new LL1(grammar, analyzer);

        assertTrue(parser.matches("program id, id, id: real; s; s end"));
        assertTrue(parser.matches("program id: int; s; s end"));
        assertTrue(parser.matches("program id, id: int; s end"));

        assertFalse(parser.matches(" id, id, id: real; s; s end"));
        assertFalse(parser.matches("program : real; s; s end"));
        assertFalse(parser.matches("program id, id, id: double; s; s end"));
        assertFalse(parser.matches("program id, id, id: real; s; s"));
        assertFalse(parser.matches("program id, id, id: real; s, s end"));
    }

    @Test
    public void testParseCase2WithNfaLexicalAnalyzer() {
        Grammar grammar = createGrammar3();

        LexicalAnalyzer analyzer = NfaLexicalAnalyzer.builder()
                .addMorpheme("program")
                .addMorpheme(":")
                .addMorpheme(";")
                .addMorpheme("end")
                .addMorpheme("id")
                .addMorpheme(",")
                .addMorpheme("s")
                .addMorpheme("real")
                .addMorpheme("int")
                .build();

        LLParser parser = new LL1(grammar, analyzer);

        assertTrue(parser.matches("program id, id, id: real; s; s end"));
        assertTrue(parser.matches("program id: int; s; s end"));
        assertTrue(parser.matches("program id, id: int; s end"));

        assertFalse(parser.matches(" id, id, id: real; s; s end"));
        assertFalse(parser.matches("program : real; s; s end"));
        assertFalse(parser.matches("program id, id, id: double; s; s end"));
        assertFalse(parser.matches("program id, id, id: real; s; s"));
        assertFalse(parser.matches("program id, id, id: real; s, s end"));
    }

    @Test
    public void testParseCase3WithJdkLexicalAnalyzer() {
        Grammar grammar = createGrammar1();

        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .build();


        LLParser parser = new LL1(grammar, analyzer);

        assertTrue(parser.matches("A12+B*D"));
        assertTrue(parser.matches("(a+b01)*d03"));
        assertTrue(parser.matches("(asdfsdfDASDF323+ASDFC0102D*d23234+(asdf+dd)*(d1d*k9))"));
        assertFalse(parser.matches("000+(id*id)"));
        assertFalse(parser.matches("()"));
    }

    @Test
    public void testParseCase3WithNfaLexicalAnalyzer() {
        Grammar grammar = createGrammar1();

        LexicalAnalyzer analyzer = NfaLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .build();


        LLParser parser = new LL1(grammar, analyzer);

        assertTrue(parser.matches("A12+B*D"));
        assertTrue(parser.matches("(a+b01)*d03"));
        assertTrue(parser.matches("(asdfsdfDASDF323+ASDFC0102D*d23234+(asdf+dd)*(d1d*k9))"));
        assertFalse(parser.matches("000+(id*id)"));
        assertFalse(parser.matches("()"));
    }

    @Test
    public void testParseCase4WithJdkLexicalAnalyzer() {
        Grammar grammar = createGrammar4();

        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
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


        LLParser parser = new LL1(grammar, analyzer);

        assertTrue(parser.matches("a"));
        assertTrue(parser.matches("ab"));
        assertTrue(parser.matches("abc"));
        assertTrue(parser.matches("abcd"));
        assertTrue(parser.matches("b"));
        assertTrue(parser.matches("bc"));
        assertTrue(parser.matches("bcd"));
        assertTrue(parser.matches("c"));
        assertTrue(parser.matches("cd"));
        assertTrue(parser.matches("d"));

        assertFalse(parser.matches("e"));
        assertFalse(parser.matches("ba"));
        assertFalse(parser.matches("ac"));
        assertFalse(parser.matches("bdc"));
    }

    @Test
    public void testParseCase4WithNfaLexicalAnalyzer() {
        Grammar grammar = createGrammar4();

        LexicalAnalyzer analyzer = NfaLexicalAnalyzer.builder()
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


        LLParser parser = new LL1(grammar, analyzer);

        assertTrue(parser.matches("a"));
        assertTrue(parser.matches("ab"));
        assertTrue(parser.matches("abc"));
        assertTrue(parser.matches("abcd"));
        assertTrue(parser.matches("b"));
        assertTrue(parser.matches("bc"));
        assertTrue(parser.matches("bcd"));
        assertTrue(parser.matches("c"));
        assertTrue(parser.matches("cd"));
        assertTrue(parser.matches("d"));

        assertFalse(parser.matches("e"));
        assertFalse(parser.matches("ba"));
        assertFalse(parser.matches("ac"));
        assertFalse(parser.matches("bdc"));
    }

    private LexicalAnalyzer getDefaultLexicalAnalyzer() {
        return JdkLexicalAnalyzer.builder()
                .addMorpheme("NULL")
                .build();
    }

    private Grammar createGrammar1() {
        return Grammar.create(
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
    }

    private Grammar createGrammar2() {
        return Grammar.create(
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
                                        createTerminator("id")
                                )
                        )
                )
        );
    }

    private Grammar createGrammar3() {
        String PROGRAM = "PROGRAM";
        String DECLIST = "DECLIST";
        String DECLISTN = "DECLISTN";
        String STLIST = "STLIST";
        String STLISTN = "STLISTN";
        String TYPE = "TYPE";

        return Grammar.create(
                createNonTerminator(PROGRAM),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator(PROGRAM),
                                SymbolString.create(
                                        createTerminator("program"),
                                        createNonTerminator(DECLIST),
                                        createTerminator(":"),
                                        createNonTerminator(TYPE),
                                        createTerminator(";"),
                                        createNonTerminator(STLIST),
                                        createTerminator("end")
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

    private Grammar createGrammar4() {
        return Grammar.create(
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
    }
}

