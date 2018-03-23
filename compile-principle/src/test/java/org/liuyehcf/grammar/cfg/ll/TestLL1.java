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
                "{\"productions\":[\"E → ( E ) T′ E′ | id T′ E′\",\"E′ → + T E′ | __ε__\",\"F → ( E ) | id\",\"T → ( E ) T′ | id T′\",\"T′ → * F T′ | __ε__\"]}",
                parser.getGrammar().toString()
        );

        assertEquals(
                "{\"terminator\":{\"(\":\"(\",\")\":\")\",\"*\":\"*\",\"+\":\"+\",\"__ε__\":\"__ε__\",\"id\":\"id\"},\"nonTerminator\":{\"E\":\"id,(\",\"E′\":\"__ε__,+\",\"F\":\"id,(\",\"T\":\"id,(\",\"T′\":\"__ε__,*\"}}",
                parser.getFirstJSONString()
        );

        assertEquals(
                "{\"nonTerminator\":{\"E\":\"__$__,)\",\"E′\":\"__$__,)\",\"F\":\"__$__,),*,+\",\"T\":\"__$__,),+\",\"T′\":\"__$__,),+\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"E′\":{\"E′ → + T E′\":\"+\",\"E′ → __ε__\":\"__$__,)\"},\"T′\":{\"T′ → * F T′\":\"*\",\"T′ → __ε__\":\"__$__,),+\"},\"T\":{\"T → ( E ) T′\":\"(\",\"T → id T′\":\"id\"},\"E\":{\"E → ( E ) T′ E′\":\"(\",\"E → id T′ E′\":\"id\"},\"F\":{\"F → ( E )\":\"(\",\"F → id\":\"id\"}}",
                parser.getSelectJSONString()
        );

        assertEquals(
                "| 非终结符\\终结符 | ( | ) | * | + | __ε__ | id |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| E | E → ( E ) T′ E′ | \\ | \\ | \\ | \\ | E → id T′ E′ |\n" +
                        "| E′ | \\ | E′ → __ε__ | \\ | E′ → + T E′ | \\ | \\ |\n" +
                        "| F | F → ( E ) | \\ | \\ | \\ | \\ | F → id |\n" +
                        "| T | T → ( E ) T′ | \\ | \\ | \\ | \\ | T → id T′ |\n" +
                        "| T′ | \\ | T′ → __ε__ | T′ → * F T′ | T′ → __ε__ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }

    @Test
    public void testLL1Status2() {
        LLParser parser = LL1.create(GrammarCase.LL1_CASE2.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"productions\":[\"DECLIST → id DECLISTN\",\"DECLISTN → , id DECLISTN | __ε__\",\"PROGRAM → program  DECLIST : TYPE ; STLIST  end\",\"STLIST → s STLISTN\",\"STLISTN → ; s STLISTN | __ε__\",\"TYPE → int | real\"]}",
                parser.getGrammar().toString()
        );

        assertEquals(
                "{\"terminator\":{\" end\":\" end\",\",\":\",\",\":\":\":\",\";\":\";\",\"__ε__\":\"__ε__\",\"id\":\"id\",\"int\":\"int\",\"program \":\"program \",\"real\":\"real\",\"s\":\"s\"},\"nonTerminator\":{\"DECLIST\":\"id\",\"DECLISTN\":\"__ε__,,\",\"PROGRAM\":\"program \",\"STLIST\":\"s\",\"STLISTN\":\"__ε__,;\",\"TYPE\":\"real,int\"}}",
                parser.getFirstJSONString()
        );

        assertEquals(
                "{\"nonTerminator\":{\"DECLIST\":\":\",\"DECLISTN\":\":\",\"PROGRAM\":\"__$__\",\"STLIST\":\" end\",\"STLISTN\":\" end\",\"TYPE\":\";\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"DECLISTN\":{\"DECLISTN → __ε__\":\":\",\"DECLISTN → , id DECLISTN\":\",\"},\"PROGRAM\":{\"PROGRAM → program  DECLIST : TYPE ; STLIST  end\":\"program \"},\"STLIST\":{\"STLIST → s STLISTN\":\"s\"},\"TYPE\":{\"TYPE → real\":\"real\",\"TYPE → int\":\"int\"},\"STLISTN\":{\"STLISTN → ; s STLISTN\":\";\",\"STLISTN → __ε__\":\" end\"},\"DECLIST\":{\"DECLIST → id DECLISTN\":\"id\"}}",
                parser.getSelectJSONString()
        );

        assertEquals(
                "| 非终结符\\终结符 |  end | , | : | ; | __ε__ | id | int | program  | real | s |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| DECLIST | \\ | \\ | \\ | \\ | \\ | DECLIST → id DECLISTN | \\ | \\ | \\ | \\ |\n" +
                        "| DECLISTN | \\ | DECLISTN → , id DECLISTN | DECLISTN → __ε__ | \\ | \\ | \\ | \\ | \\ | \\ | \\ |\n" +
                        "| PROGRAM | \\ | \\ | \\ | \\ | \\ | \\ | \\ | PROGRAM → program  DECLIST : TYPE ; STLIST  end | \\ | \\ |\n" +
                        "| STLIST | \\ | \\ | \\ | \\ | \\ | \\ | \\ | \\ | \\ | STLIST → s STLISTN |\n" +
                        "| STLISTN | STLISTN → __ε__ | \\ | \\ | STLISTN → ; s STLISTN | \\ | \\ | \\ | \\ | \\ | \\ |\n" +
                        "| TYPE | \\ | \\ | \\ | \\ | \\ | \\ | TYPE → int | \\ | TYPE → real | \\ |\n",
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

