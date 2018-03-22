package org.liuyehcf.grammar.cfg.ll;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.JdkLexicalAnalyzer;
import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.NfaLexicalAnalyzer;
import org.liuyehcf.grammar.core.definition.Grammar;

import static org.junit.Assert.*;

public class TestLL1 {
    @Test
    public void testStatus1() {
        Grammar grammar = GrammarCase.GRAMMAR_CASE_3;

        LLParser parser = LL1.create(getDefaultLexicalAnalyzer(), grammar);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"productions\":[\"E′ → + T E′ | __EPSILON__\",\"T′ → * F T′ | __EPSILON__\",\"T → ( E ) T′ | id T′\",\"E → ( E ) T′ E′ | id T′ E′\",\"F → ( E ) | id\"]}",
                parser.getGrammar()
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
    public void testStatus2() {
        Grammar grammar = GrammarCase.GRAMMAR_CASE_4;

        LLParser parser = LL1.create(getDefaultLexicalAnalyzer(), grammar);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"productions\":[\"PROGRAM → program DECLIST : TYPE ; STLIST end\",\"DECLISTN → , id DECLISTN | __EPSILON__\",\"STLIST → s STLISTN\",\"TYPE → real | int\",\"STLISTN → ; s STLISTN | __EPSILON__\",\"DECLIST → id DECLISTN\"]}",
                parser.getGrammar()
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
    public void testMatchCase1WithJdkLexicalAnalyzer() {
        Grammar grammar = GrammarCase.GRAMMAR_CASE_3;

        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id")
                .build();

        LLParser parser = LL1.create(analyzer, grammar);

        assertTrue(parser.isLegal());

        assertTrue(parser.matches("id+id*id"));
        assertTrue(parser.matches("(id+id)*id"));
        assertTrue(parser.matches("id+(id*id)"));
        assertTrue(parser.matches("(id)+(id*id)"));
    }

    @Test
    public void testMatchCase1WithNfaLexicalAnalyzer() {
        Grammar grammar = GrammarCase.GRAMMAR_CASE_3;

        LexicalAnalyzer analyzer = NfaLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id")
                .build();

        LLParser parser = LL1.create(analyzer, grammar);

        assertTrue(parser.isLegal());

        assertTrue(parser.matches("id+id*id"));
        assertTrue(parser.matches("(id+id)*id"));
        assertTrue(parser.matches("id+(id*id)"));
        assertTrue(parser.matches("(id)+(id*id)"));
    }

    @Test
    public void testMatchCase2WithJdkLexicalAnalyzer() {
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

        assertTrue(parser.isLegal());

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
    public void testMatchCase2WithNfaLexicalAnalyzer() {
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

        assertTrue(parser.isLegal());

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
    public void testMatchCase3WithJdkLexicalAnalyzer() {
        LLParser parser = LL1.create(GrammarCase.LL1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE1.GRAMMAR);

//        assertTrue(parser.isLegal());
//
//        for (String input : GrammarCase.LL1_CASE1.TRUE_CASES) {
//            assertTrue(parser.matches(input));
//        }
//
//        for (String input : GrammarCase.LL1_CASE1.FALSE_CASES) {
//            assertFalse(parser.matches(input));
//        }


        parser = LL1.create(GrammarCase.LL1_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.LL1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE1.TRUE_CASES) {
            System.out.println(input);
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }


    @Test
    public void testMatchCase4WithJdkLexicalAnalyzer() {
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

        assertTrue(parser.isLegal());

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
    public void testMatchCase4WithNfaLexicalAnalyzer() {
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

        assertTrue(parser.isLegal());

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

