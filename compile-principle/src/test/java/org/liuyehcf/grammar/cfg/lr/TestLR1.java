package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;

import static org.junit.Assert.*;

public class TestLR1 {
    @Test
    public void testAmbiguityStatus1() {
        LRParser parser = LR1.create(GrammarCase.Ambiguity_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.Ambiguity_CASE1.GRAMMAR);

        assertFalse(parser.isLegal());

        assertEquals(
                "| 状态\\文法符号 | ( | ) | * | + | __$__ | id | E |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | MOVE_IN \"2\" | \\ | \\ | \\ | \\ | MOVE_IN \"3\" | JUMP \"1\" |\n" +
                        "| 1 | \\ | \\ | MOVE_IN \"4\" | MOVE_IN \"5\" | ACCEPT \"__S__ → E\" | \\ | \\ |\n" +
                        "| 2 | MOVE_IN \"7\" | \\ | \\ | \\ | \\ | MOVE_IN \"8\" | JUMP \"6\" |\n" +
                        "| 3 | \\ | \\ | REDUCTION \"E → id\" | REDUCTION \"E → id\" | REDUCTION \"E → id\" | \\ | \\ |\n" +
                        "| 4 | MOVE_IN \"2\" | \\ | \\ | \\ | \\ | MOVE_IN \"3\" | JUMP \"9\" |\n" +
                        "| 5 | MOVE_IN \"2\" | \\ | \\ | \\ | \\ | MOVE_IN \"3\" | JUMP \"10\" |\n" +
                        "| 6 | \\ | MOVE_IN \"11\" | MOVE_IN \"12\" | MOVE_IN \"13\" | \\ | \\ | \\ |\n" +
                        "| 7 | MOVE_IN \"7\" | \\ | \\ | \\ | \\ | MOVE_IN \"8\" | JUMP \"14\" |\n" +
                        "| 8 | \\ | REDUCTION \"E → id\" | REDUCTION \"E → id\" | REDUCTION \"E → id\" | \\ | \\ | \\ |\n" +
                        "| 9 | \\ | \\ | MOVE_IN \"4\" / REDUCTION \"E → E * E\" | REDUCTION \"E → E * E\" / MOVE_IN \"5\" | REDUCTION \"E → E * E\" | \\ | \\ |\n" +
                        "| 10 | \\ | \\ | MOVE_IN \"4\" / REDUCTION \"E → E + E\" | MOVE_IN \"5\" / REDUCTION \"E → E + E\" | REDUCTION \"E → E + E\" | \\ | \\ |\n" +
                        "| 11 | \\ | \\ | REDUCTION \"E → ( E )\" | REDUCTION \"E → ( E )\" | REDUCTION \"E → ( E )\" | \\ | \\ |\n" +
                        "| 12 | MOVE_IN \"7\" | \\ | \\ | \\ | \\ | MOVE_IN \"8\" | JUMP \"15\" |\n" +
                        "| 13 | MOVE_IN \"7\" | \\ | \\ | \\ | \\ | MOVE_IN \"8\" | JUMP \"16\" |\n" +
                        "| 14 | \\ | MOVE_IN \"17\" | MOVE_IN \"12\" | MOVE_IN \"13\" | \\ | \\ | \\ |\n" +
                        "| 15 | \\ | REDUCTION \"E → E * E\" | MOVE_IN \"12\" / REDUCTION \"E → E * E\" | REDUCTION \"E → E * E\" / MOVE_IN \"13\" | \\ | \\ | \\ |\n" +
                        "| 16 | \\ | REDUCTION \"E → E + E\" | MOVE_IN \"12\" / REDUCTION \"E → E + E\" | MOVE_IN \"13\" / REDUCTION \"E → E + E\" | \\ | \\ | \\ |\n" +
                        "| 17 | \\ | REDUCTION \"E → ( E )\" | REDUCTION \"E → ( E )\" | REDUCTION \"E → ( E )\" | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }

    @Test
    public void testLR1Status1() {

        LRParser parser = LR1.create(GrammarCase.LR1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LR1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"nonTerminator\":{\"L\":\"__$__,=\",\"R\":\"__$__,=\",\"S\":\"__$__\",\"__S__\":\"__$__\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"1\":\"[0, *] → 2\",\"2\":\"[0, id] → 3\",\"3\":\"[0, L] → 4\",\"4\":\"[0, R] → 5\",\"5\":\"[0, S] → 1\",\"6\":\"[2, *] → 2\",\"7\":\"[2, id] → 3\",\"8\":\"[2, L] → 7\",\"9\":\"[2, R] → 6\",\"10\":\"[4, =] → 8\",\"11\":\"[8, *] → 10\",\"12\":\"[8, id] → 11\",\"13\":\"[8, L] → 12\",\"14\":\"[8, R] → 9\",\"15\":\"[10, *] → 10\",\"16\":\"[10, id] → 11\",\"17\":\"[10, L] → 12\",\"18\":\"[10, R] → 13\"}",
                parser.getClosureTransferTableJSONString()
        );

        assertEquals(
                "{\"closures:\":[{\"id\":\"0\",\"coreItems\":\"[__S__ → · S, [__$__]]\",\"equalItems\":\"[L → · * R, [=, __$__], L → · id, [=, __$__], R → · L, [__$__], S → · L = R, [__$__], S → · R, [__$__]]\"}, {\"id\":\"1\",\"coreItems\":\"[__S__ → S ·, [__$__]]\",\"equalItems\":\"[]\"}, {\"id\":\"2\",\"coreItems\":\"[L → * · R, [=, __$__]]\",\"equalItems\":\"[L → · * R, [=, __$__], L → · id, [=, __$__], R → · L, [=, __$__]]\"}, {\"id\":\"3\",\"coreItems\":\"[L → id ·, [=, __$__]]\",\"equalItems\":\"[]\"}, {\"id\":\"4\",\"coreItems\":\"[R → L ·, [__$__], S → L · = R, [__$__]]\",\"equalItems\":\"[]\"}, {\"id\":\"5\",\"coreItems\":\"[S → R ·, [__$__]]\",\"equalItems\":\"[]\"}, {\"id\":\"6\",\"coreItems\":\"[L → * R ·, [=, __$__]]\",\"equalItems\":\"[]\"}, {\"id\":\"7\",\"coreItems\":\"[R → L ·, [=, __$__]]\",\"equalItems\":\"[]\"}, {\"id\":\"8\",\"coreItems\":\"[S → L = · R, [__$__]]\",\"equalItems\":\"[L → · * R, [__$__], L → · id, [__$__], R → · L, [__$__]]\"}, {\"id\":\"9\",\"coreItems\":\"[S → L = R ·, [__$__]]\",\"equalItems\":\"[]\"}, {\"id\":\"10\",\"coreItems\":\"[L → * · R, [__$__]]\",\"equalItems\":\"[L → · * R, [__$__], L → · id, [__$__], R → · L, [__$__]]\"}, {\"id\":\"11\",\"coreItems\":\"[L → id ·, [__$__]]\",\"equalItems\":\"[]\"}, {\"id\":\"12\",\"coreItems\":\"[R → L ·, [__$__]]\",\"equalItems\":\"[]\"}, {\"id\":\"13\",\"coreItems\":\"[L → * R ·, [__$__]]\",\"equalItems\":\"[]\"}]}",
                parser.getClosureJSONString()
        );

        assertEquals(
                "| 状态\\文法符号 | * | = | __$__ | id | L | R | S |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | MOVE_IN \"2\" | \\ | \\ | MOVE_IN \"3\" | JUMP \"4\" | JUMP \"5\" | JUMP \"1\" |\n" +
                        "| 1 | \\ | \\ | ACCEPT \"__S__ → S\" | \\ | \\ | \\ | \\ |\n" +
                        "| 2 | MOVE_IN \"2\" | \\ | \\ | MOVE_IN \"3\" | JUMP \"7\" | JUMP \"6\" | \\ |\n" +
                        "| 3 | \\ | REDUCTION \"L → id\" | REDUCTION \"L → id\" | \\ | \\ | \\ | \\ |\n" +
                        "| 4 | \\ | MOVE_IN \"8\" | REDUCTION \"R → L\" | \\ | \\ | \\ | \\ |\n" +
                        "| 5 | \\ | \\ | REDUCTION \"S → R\" | \\ | \\ | \\ | \\ |\n" +
                        "| 6 | \\ | REDUCTION \"L → * R\" | REDUCTION \"L → * R\" | \\ | \\ | \\ | \\ |\n" +
                        "| 7 | \\ | REDUCTION \"R → L\" | REDUCTION \"R → L\" | \\ | \\ | \\ | \\ |\n" +
                        "| 8 | MOVE_IN \"10\" | \\ | \\ | MOVE_IN \"11\" | JUMP \"12\" | JUMP \"9\" | \\ |\n" +
                        "| 9 | \\ | \\ | REDUCTION \"S → L = R\" | \\ | \\ | \\ | \\ |\n" +
                        "| 10 | MOVE_IN \"10\" | \\ | \\ | MOVE_IN \"11\" | JUMP \"12\" | JUMP \"13\" | \\ |\n" +
                        "| 11 | \\ | \\ | REDUCTION \"L → id\" | \\ | \\ | \\ | \\ |\n" +
                        "| 12 | \\ | \\ | REDUCTION \"R → L\" | \\ | \\ | \\ | \\ |\n" +
                        "| 13 | \\ | \\ | REDUCTION \"L → * R\" | \\ | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );

    }

    @Test
    public void testLR1Case1() {
        LRParser parser = LR1.create(GrammarCase.LR1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LR1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LR1_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LR1_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LR1.create(GrammarCase.LR1_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.LR1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LR1_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LR1_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }

    @Test
    public void testLL1Case1() {
        LRParser parser = LR1.create(GrammarCase.LL1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LR1.create(GrammarCase.LL1_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.LL1_CASE1.GRAMMAR);

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
        LRParser parser = LR1.create(GrammarCase.LL1_CASE2.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE2.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE2.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LR1.create(GrammarCase.LL1_CASE2.NFA_LEXICAL_ANALYZER, GrammarCase.LL1_CASE2.GRAMMAR);

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
        LRParser parser = LR1.create(GrammarCase.LL1_CASE3.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE3.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE3.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE3.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LR1.create(GrammarCase.LL1_CASE3.NFA_LEXICAL_ANALYZER, GrammarCase.LL1_CASE3.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE3.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE3.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }

    @Test
    public void testLR0Case1() {
        LRParser parser = LR1.create(GrammarCase.LR0_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LR0_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LR0_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LR0_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LR1.create(GrammarCase.LR0_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.LR0_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LR0_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LR0_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }

    @Test
    public void testSLRCase1() {
        LRParser parser = LR1.create(GrammarCase.SLR_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.SLR_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.SLR_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.SLR_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LR1.create(GrammarCase.SLR_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.SLR_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.SLR_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.SLR_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }

    @Test
    public void testSLRCase2() {
        LRParser parser = LR1.create(GrammarCase.SLR_CASE2.JDK_LEXICAL_ANALYZER, GrammarCase.SLR_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.SLR_CASE2.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.SLR_CASE2.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LR1.create(GrammarCase.SLR_CASE2.NFA_LEXICAL_ANALYZER, GrammarCase.SLR_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.SLR_CASE2.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.SLR_CASE2.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }
}
