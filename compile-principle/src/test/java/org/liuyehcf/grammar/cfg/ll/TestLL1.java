package org.liuyehcf.grammar.cfg.ll;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;

import static org.junit.Assert.*;

public class TestLL1 {
    @Test
    public void testLL1Status1() {
        LLParser parser = LL1.create(GrammarCase.LL1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"productions\":[\"E′ → + T E′ | __EPSILON__\",\"T′ → * F T′ | __EPSILON__\",\"T → ( E ) T′ | id T′\",\"E → ( E ) T′ E′ | id T′ E′\",\"F → ( E ) | id\"]}",
                parser.getGrammar().toString()
        );

        assertEquals(
                "{\"terminator\":{\"__EPSILON__\":\"__EPSILON__\",\"id\":\"id\",\"(\":\"(\",\")\":\")\",\"*\":\"*\",\"+\":\"+\"},\"nonTerminator\":{\"E′\":\"__EPSILON__,+\",\"T′\":\"__EPSILON__,*\",\"T\":\"id,(\",\"E\":\"id,(\",\"F\":\"id,(\"}}",
                parser.getFirstJSONString()
        );

        assertEquals(
                "{\"nonTerminator\":{\"E′\":\"),__DOLLAR__\",\"T′\":\"),+,__DOLLAR__\",\"T\":\"),+,__DOLLAR__\",\"E\":\"),__DOLLAR__\",\"F\":\"),*,+,__DOLLAR__\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"E′\":{\"E′ → + T E′\":\"+\",\"E′ → __EPSILON__\":\"),__DOLLAR__\"},\"T′\":{\"T′ → * F T′\":\"*\",\"T′ → __EPSILON__\":\"),+,__DOLLAR__\"},\"T\":{\"T → ( E ) T′\":\"(\",\"T → id T′\":\"id\"},\"E\":{\"E → ( E ) T′ E′\":\"(\",\"E → id T′ E′\":\"id\"},\"F\":{\"F → ( E )\":\"(\",\"F → id\":\"id\"}}",
                parser.getSelectJSONString()
        );

        assertEquals(
                "| 非终结符\\终结符 | __EPSILON__ | id | ( | ) | * | + |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| E′ | \\ | \\ | \\ | E′ → __EPSILON__ | \\ | E′ → + T E′ |\n" +
                        "| T′ | \\ | \\ | \\ | T′ → __EPSILON__ | T′ → * F T′ | T′ → __EPSILON__ |\n" +
                        "| T | \\ | T → id T′ | T → ( E ) T′ | \\ | \\ | \\ |\n" +
                        "| E | \\ | E → id T′ E′ | E → ( E ) T′ E′ | \\ | \\ | \\ |\n" +
                        "| F | \\ | F → id | F → ( E ) | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }

    @Test
    public void testLL1Status2() {
        LLParser parser = LL1.create(GrammarCase.LL1_CASE2.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"productions\":[\"PROGRAM → program  DECLIST : TYPE ; STLIST  end\",\"DECLISTN → , id DECLISTN | __EPSILON__\",\"STLIST → s STLISTN\",\"TYPE → real | int\",\"STLISTN → ; s STLISTN | __EPSILON__\",\"DECLIST → id DECLISTN\"]}",
                parser.getGrammar().toString()
        );

        assertEquals(
                "{\"terminator\":{\"__EPSILON__\":\"__EPSILON__\",\"s\":\"s\",\"program \":\"program \",\":\":\":\",\";\":\";\",\" end\":\" end\",\"id\":\"id\",\"real\":\"real\",\",\":\",\",\"int\":\"int\"},\"nonTerminator\":{\"PROGRAM\":\"program \",\"STLIST\":\"s\",\"DECLISTN\":\"__EPSILON__,,\",\"TYPE\":\"real,int\",\"STLISTN\":\"__EPSILON__,;\",\"DECLIST\":\"id\"}}",
                parser.getFirstJSONString()
        );

        assertEquals(
                "{\"nonTerminator\":{\"PROGRAM\":\"__DOLLAR__\",\"STLIST\":\" end\",\"DECLISTN\":\":\",\"TYPE\":\";\",\"STLISTN\":\" end\",\"DECLIST\":\":\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"PROGRAM\":{\"PROGRAM → program  DECLIST : TYPE ; STLIST  end\":\"program \"},\"STLIST\":{\"STLIST → s STLISTN\":\"s\"},\"DECLISTN\":{\"DECLISTN → __EPSILON__\":\":\",\"DECLISTN → , id DECLISTN\":\",\"},\"TYPE\":{\"TYPE → real\":\"real\",\"TYPE → int\":\"int\"},\"STLISTN\":{\"STLISTN → __EPSILON__\":\" end\",\"STLISTN → ; s STLISTN\":\";\"},\"DECLIST\":{\"DECLIST → id DECLISTN\":\"id\"}}",
                parser.getSelectJSONString()
        );

        assertEquals(
                "| 非终结符\\终结符 | __EPSILON__ | s | program  | : | ; |  end | id | real | , | int |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| PROGRAM | \\ | \\ | PROGRAM → program  DECLIST : TYPE ; STLIST  end | \\ | \\ | \\ | \\ | \\ | \\ | \\ |\n" +
                        "| STLIST | \\ | STLIST → s STLISTN | \\ | \\ | \\ | \\ | \\ | \\ | \\ | \\ |\n" +
                        "| DECLISTN | \\ | \\ | \\ | DECLISTN → __EPSILON__ | \\ | \\ | \\ | \\ | DECLISTN → , id DECLISTN | \\ |\n" +
                        "| TYPE | \\ | \\ | \\ | \\ | \\ | \\ | \\ | TYPE → real | \\ | TYPE → int |\n" +
                        "| STLISTN | \\ | \\ | \\ | \\ | STLISTN → ; s STLISTN | STLISTN → __EPSILON__ | \\ | \\ | \\ | \\ |\n" +
                        "| DECLIST | \\ | \\ | \\ | \\ | \\ | \\ | DECLIST → id DECLISTN | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }

    @Test
    public void testLL1Case1() {
        LLParser parser = LL1.create(GrammarCase.LL1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }


        parser = LL1.create(GrammarCase.LL1_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.LL1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }

    @Test
    public void testLL1Case2() {
        LLParser parser = LL1.create(GrammarCase.LL1_CASE2.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE2.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE2.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }


        parser = LL1.create(GrammarCase.LL1_CASE2.NFA_LEXICAL_ANALYZER, GrammarCase.LL1_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE2.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE2.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }

    @Test
    public void testLL1Case3() {
        LLParser parser = LL1.create(GrammarCase.LL1_CASE3.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE3.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE3.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE3.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }


        parser = LL1.create(GrammarCase.LL1_CASE3.NFA_LEXICAL_ANALYZER, GrammarCase.LL1_CASE3.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE3.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE3.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }
}

