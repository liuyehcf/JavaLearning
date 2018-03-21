package org.liuyehcf.grammar.cfg.ll;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.JdkLexicalAnalyzer;
import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.NfaLexicalAnalyzer;
import org.liuyehcf.grammar.core.MorphemeType;
import org.liuyehcf.grammar.core.definition.Grammar;

import static org.junit.Assert.*;
import static org.liuyehcf.grammar.cfg.TestLexicalAnalyzer.getIdRegex;

public class TestLL1 {
    @Test
    public void testFirstFollowSelectCase1() {
        Grammar grammar = GrammarCase.GRAMMAR_CASE_3;

        LLParser parser = LL1.create(getDefaultLexicalAnalyzer(), grammar);
        Grammar convertedGrammar = parser.getGrammar();

        assertEquals(
                "{\"productions\":[\"E′ → + T E′ | __EPSILON__\",\"T′ → * F T′ | __EPSILON__\",\"T → ( E ) T′ | id T′\",\"E → ( E ) T′ E′ | id T′ E′\",\"F → ( E ) | id\"]}",
                convertedGrammar.toJSONString()
        );

        assertEquals(
                "{\"terminator\":{\"__EPSILON__\":\"__EPSILON__\",\"(\":\"(\",\")\":\")\",\"*\":\"*\",\"+\":\"+\",\"id\":\"id\"},\"nonTerminator\":{\"E′\":\"__EPSILON__,+\",\"T′\":\"__EPSILON__,*\",\"T\":\"(,id\",\"E\":\"(,id\",\"F\":\"(,id\"}}",
                parser.getFirstJSONString()
        );

        assertEquals(
                "{\"nonTerminator\":{\"E′\":\"),__DOLLAR__\",\"T′\":\"),+,__DOLLAR__\",\"T\":\"),+,__DOLLAR__\",\"E\":\"),__DOLLAR__\",\"F\":\"),*,+,__DOLLAR__\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"E′\":{\"E′ → + T E′\":\"+\",\"E′ → __EPSILON__\":\"),__DOLLAR__\"},\"T′\":{\"T′ → * F T′\":\"*\",\"T′ → __EPSILON__\":\"),+,__DOLLAR__\"},\"T\":{\"T → id T′\":\"id\",\"T → ( E ) T′\":\"(\"},\"E\":{\"E → id T′ E′\":\"id\",\"E → ( E ) T′ E′\":\"(\"},\"F\":{\"F → ( E )\":\"(\",\"F → id\":\"id\"}}",
                parser.getSelectJSONString()
        );

        assertEquals(
                "| 非终结符\\终结符 | __EPSILON__ | ( | ) | * | + | id |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| E′ | \\ | \\ | E′ → __EPSILON__ | \\ | E′ → + T E′ | \\ |\n" +
                        "| T′ | \\ | \\ | T′ → __EPSILON__ | T′ → * F T′ | T′ → __EPSILON__ | \\ |\n" +
                        "| T | \\ | T → ( E ) T′ | \\ | \\ | \\ | T → id T′ |\n" +
                        "| E | \\ | E → ( E ) T′ E′ | \\ | \\ | \\ | E → id T′ E′ |\n" +
                        "| F | \\ | F → ( E ) | \\ | \\ | \\ | F → id |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }

    @Test
    public void testFirstFollowSelectCase2() {
        Grammar grammar = GrammarCase.GRAMMAR_CASE_4;

        LLParser parser = LL1.create(getDefaultLexicalAnalyzer(), grammar);
        Grammar convertedGrammar = parser.getGrammar();

        assertEquals(
                "{\"productions\":[\"PROGRAM → program DECLIST : TYPE ; STLIST end\",\"DECLISTN → , id DECLISTN | __EPSILON__\",\"STLIST → s STLISTN\",\"TYPE → real | int\",\"STLISTN → ; s STLISTN | __EPSILON__\",\"DECLIST → id DECLISTN\"]}",
                convertedGrammar.toJSONString()
        );

        assertEquals(
                "{\"terminator\":{\"__EPSILON__\":\"__EPSILON__\",\"s\":\"s\",\":\":\":\",\"end\":\"end\",\"program\":\"program\",\";\":\";\",\"id\":\"id\",\"real\":\"real\",\",\":\",\",\"int\":\"int\"},\"nonTerminator\":{\"PROGRAM\":\"program\",\"STLIST\":\"s\",\"DECLISTN\":\"__EPSILON__,,\",\"TYPE\":\"real,int\",\"STLISTN\":\"__EPSILON__,;\",\"DECLIST\":\"id\"}}",
                parser.getFirstJSONString()
        );

        assertEquals(
                "{\"nonTerminator\":{\"PROGRAM\":\"__DOLLAR__\",\"STLIST\":\"end\",\"DECLISTN\":\":\",\"TYPE\":\";\",\"STLISTN\":\"end\",\"DECLIST\":\":\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"PROGRAM\":{\"PROGRAM → program DECLIST : TYPE ; STLIST end\":\"program\"},\"STLIST\":{\"STLIST → s STLISTN\":\"s\"},\"DECLISTN\":{\"DECLISTN → __EPSILON__\":\":\",\"DECLISTN → , id DECLISTN\":\",\"},\"TYPE\":{\"TYPE → real\":\"real\",\"TYPE → int\":\"int\"},\"STLISTN\":{\"STLISTN → __EPSILON__\":\"end\",\"STLISTN → ; s STLISTN\":\";\"},\"DECLIST\":{\"DECLIST → id DECLISTN\":\"id\"}}",
                parser.getSelectJSONString()
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
                parser.getAnalysisTableMarkdownString()
        );
    }

    @Test
    public void testParseCase1WithJdkLexicalAnalyzer() {
        Grammar grammar = GrammarCase.GRAMMAR_CASE_3;

        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id")
                .build();

        LLParser parser = LL1.create(analyzer, grammar);

        assertTrue(parser.matches("id+id*id"));
        assertTrue(parser.matches("(id+id)*id"));
        assertTrue(parser.matches("id+(id*id)"));
        assertTrue(parser.matches("(id)+(id*id)"));
    }

    @Test
    public void testParseCase1WithNfaLexicalAnalyzer() {
        Grammar grammar = GrammarCase.GRAMMAR_CASE_3;

        LexicalAnalyzer analyzer = NfaLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id")
                .build();

        LLParser parser = LL1.create(analyzer, grammar);

        assertTrue(parser.matches("id+id*id"));
        assertTrue(parser.matches("(id+id)*id"));
        assertTrue(parser.matches("id+(id*id)"));
        assertTrue(parser.matches("(id)+(id*id)"));
    }

    @Test
    public void testParseCase2WithJdkLexicalAnalyzer() {
        Grammar grammar = GrammarCase.GRAMMAR_CASE_4;

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

        LLParser parser = LL1.create(analyzer, grammar);

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
        Grammar grammar = GrammarCase.GRAMMAR_CASE_4;

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

        LLParser parser = LL1.create(analyzer, grammar);

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
        Grammar grammar = GrammarCase.GRAMMAR_CASE_2;

        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .build();


        LLParser parser = LL1.create(analyzer, grammar);

        assertTrue(parser.matches("A12+B*D"));
        assertTrue(parser.matches("(a+b01)*d03"));
        assertTrue(parser.matches("(asdfsdfDASDF323+ASDFC0102D*d23234+(asdf+dd)*(d1d*k9))"));
        assertFalse(parser.matches("000+(id*id)"));
        assertFalse(parser.matches("()"));
    }

    @Test
    public void testParseCase3WithNfaLexicalAnalyzer() {
        Grammar grammar = GrammarCase.GRAMMAR_CASE_2;

        LexicalAnalyzer analyzer = NfaLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .build();


        LLParser parser = LL1.create(analyzer, grammar);

        assertTrue(parser.matches("A12+B*D"));
        assertTrue(parser.matches("(a+b01)*d03"));
        assertTrue(parser.matches("(asdfsdfDASDF323+ASDFC0102D*d23234+(asdf+dd)*(d1d*k9))"));
        assertFalse(parser.matches("000+(id*id)"));
        assertFalse(parser.matches("()"));
    }

    @Test
    public void testParseCase4WithJdkLexicalAnalyzer() {
        Grammar grammar = GrammarCase.GRAMMAR_CASE_5;

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

        LLParser parser = LL1.create(analyzer, grammar);

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
        Grammar grammar = GrammarCase.GRAMMAR_CASE_5;

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

        LLParser parser = LL1.create(analyzer, grammar);

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
}

