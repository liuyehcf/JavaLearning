package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;

import static org.junit.Assert.*;

public class TestLALR1 {
    @Test
    public void testLR1Status1() {

        LRParser parser = LALR1.create(GrammarCase.LR1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LR1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"nonTerminator\":{\"L\":\"__$__,=\",\"R\":\"__$__,=\",\"S\":\"__$__\",\"__S__\":\"__$__\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"1\":\"[0, *] → 1\",\"2\":\"[0, id] → 2\",\"3\":\"[0, L] → 3\",\"4\":\"[0, R] → 4\",\"5\":\"[0, S] → 5\",\"6\":\"[1, *] → 1\",\"7\":\"[1, id] → 2\",\"8\":\"[1, L] → 7\",\"9\":\"[1, R] → 6\",\"10\":\"[3, =] → 8\",\"11\":\"[8, *] → 1\",\"12\":\"[8, id] → 2\",\"13\":\"[8, L] → 7\",\"14\":\"[8, R] → 12\"}",
                parser.getClosureTransferTableJSONString()
        );

        assertEquals(
                "{\"closures:\":[{\"id\":\"0\",\"coreItems\":{\"1\":\"__S__ → · S, [__$__]\"},\"equalItems\":{\"1\":\"L → · * R, [=, __$__]\",\"2\":\"L → · id, [=, __$__]\",\"3\":\"R → · L, [__$__]\",\"4\":\"S → · L = R, [__$__]\",\"5\":\"S → · R, [__$__]\"}}, {\"id\":\"1\",\"coreItems\":{\"1\":\"L → * · R, [=, __$__]\"},\"equalItems\":{\"1\":\"L → · * R, [=, __$__]\",\"2\":\"L → · id, [=, __$__]\",\"3\":\"R → · L, [=, __$__]\"}}, {\"id\":\"2\",\"coreItems\":{\"1\":\"L → id ·, [=, __$__]\"},\"equalItems\":{}}, {\"id\":\"3\",\"coreItems\":{\"1\":\"R → L ·, [__$__]\",\"2\":\"S → L · = R, [__$__]\"},\"equalItems\":{}}, {\"id\":\"4\",\"coreItems\":{\"1\":\"S → R ·, [__$__]\"},\"equalItems\":{}}, {\"id\":\"5\",\"coreItems\":{\"1\":\"__S__ → S ·, [__$__]\"},\"equalItems\":{}}, {\"id\":\"6\",\"coreItems\":{\"1\":\"L → * R ·, [=, __$__]\"},\"equalItems\":{}}, {\"id\":\"7\",\"coreItems\":{\"1\":\"R → L ·, [=, __$__]\"},\"equalItems\":{}}, {\"id\":\"8\",\"coreItems\":{\"1\":\"S → L = · R, [__$__]\"},\"equalItems\":{\"1\":\"L → · * R, [__$__]\",\"2\":\"L → · id, [__$__]\",\"3\":\"R → · L, [__$__]\"}}, {\"id\":\"12\",\"coreItems\":{\"1\":\"S → L = R ·, [__$__]\"},\"equalItems\":{}}]}",
                parser.getClosureJSONString()
        );

        assertEquals(
                "| 状态\\文法符号 | * | = | __$__ | id | L | R | S |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | MOVE_IN \"1\" | \\ | \\ | MOVE_IN \"2\" | JUMP \"3\" | JUMP \"4\" | JUMP \"5\" |\n" +
                        "| 1 | MOVE_IN \"1\" | \\ | \\ | MOVE_IN \"2\" | JUMP \"7\" | JUMP \"6\" | \\ |\n" +
                        "| 2 | \\ | REDUCTION \"L → id\" | REDUCTION \"L → id\" | \\ | \\ | \\ | \\ |\n" +
                        "| 3 | \\ | MOVE_IN \"8\" | REDUCTION \"R → L\" | \\ | \\ | \\ | \\ |\n" +
                        "| 4 | \\ | \\ | REDUCTION \"S → R\" | \\ | \\ | \\ | \\ |\n" +
                        "| 5 | \\ | \\ | ACCEPT \"__S__ → S\" | \\ | \\ | \\ | \\ |\n" +
                        "| 6 | \\ | REDUCTION \"L → * R\" | REDUCTION \"L → * R\" | \\ | \\ | \\ | \\ |\n" +
                        "| 7 | \\ | REDUCTION \"R → L\" | REDUCTION \"R → L\" | \\ | \\ | \\ | \\ |\n" +
                        "| 8 | MOVE_IN \"1\" | \\ | \\ | MOVE_IN \"2\" | JUMP \"7\" | JUMP \"12\" | \\ |\n" +
                        "| 12 | \\ | \\ | REDUCTION \"S → L = R\" | \\ | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );

    }

    @Test
    public void testLR1Case1() {
        LRParser parser = LALR1.create(GrammarCase.LR1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LR1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LR1_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LR1_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LALR1.create(GrammarCase.LR1_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.LR1_CASE1.GRAMMAR);

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
        LRParser parser = LALR1.create(GrammarCase.LL1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LALR1.create(GrammarCase.LL1_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.LL1_CASE1.GRAMMAR);

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
        LRParser parser = LALR1.create(GrammarCase.LL1_CASE2.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE2.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE2.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LALR1.create(GrammarCase.LL1_CASE2.NFA_LEXICAL_ANALYZER, GrammarCase.LL1_CASE2.GRAMMAR);

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
        LRParser parser = LALR1.create(GrammarCase.LL1_CASE3.JDK_LEXICAL_ANALYZER, GrammarCase.LL1_CASE3.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LL1_CASE3.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LL1_CASE3.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LALR1.create(GrammarCase.LL1_CASE3.NFA_LEXICAL_ANALYZER, GrammarCase.LL1_CASE3.GRAMMAR);

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
        LRParser parser = LALR1.create(GrammarCase.LR0_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LR0_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LR0_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LR0_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LALR1.create(GrammarCase.LR0_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.LR0_CASE1.GRAMMAR);

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
        LRParser parser = LALR1.create(GrammarCase.SLR_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.SLR_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.SLR_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.SLR_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LALR1.create(GrammarCase.SLR_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.SLR_CASE1.GRAMMAR);

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
        LRParser parser = LALR1.create(GrammarCase.SLR_CASE2.JDK_LEXICAL_ANALYZER, GrammarCase.SLR_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.SLR_CASE2.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.SLR_CASE2.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LALR1.create(GrammarCase.SLR_CASE2.NFA_LEXICAL_ANALYZER, GrammarCase.SLR_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.SLR_CASE2.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.SLR_CASE2.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }

}
