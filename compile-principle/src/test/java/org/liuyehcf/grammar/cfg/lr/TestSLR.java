package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;

import static org.junit.Assert.*;

public class TestSLR {

    @Test
    public void testSLRStatus1() {
        LRParser parser = SLR.create(GrammarCase.SLR_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.SLR_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"nonTerminator\":{\"E\":\"__$__,),+\",\"F\":\"__$__,),*,+\",\"T\":\"__$__,),*,+\",\"__S__\":\"__$__\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"1\":\"[0, (] → 3\",\"2\":\"[0, id] → 4\",\"3\":\"[0, E] → 1\",\"4\":\"[0, F] → 5\",\"5\":\"[0, T] → 2\",\"6\":\"[1, +] → 6\",\"7\":\"[2, *] → 7\",\"8\":\"[3, (] → 3\",\"9\":\"[3, id] → 4\",\"10\":\"[3, E] → 8\",\"11\":\"[3, F] → 5\",\"12\":\"[3, T] → 2\",\"13\":\"[6, (] → 3\",\"14\":\"[6, id] → 4\",\"15\":\"[6, F] → 5\",\"16\":\"[6, T] → 9\",\"17\":\"[7, (] → 3\",\"18\":\"[7, id] → 4\",\"19\":\"[7, F] → 10\",\"20\":\"[8, )] → 11\",\"21\":\"[8, +] → 6\",\"22\":\"[9, *] → 7\"}",
                parser.getClosureTransferTableJSONString()
        );

        assertEquals(
                "{\"closures:\":[{\"id\":\"0\",\"coreItems\":\"[__S__ → · E]\",\"equalItems\":\"[E → · E + T, E → · T, F → · ( E ), F → · id, T → · F, T → · T * F]\"}, {\"id\":\"1\",\"coreItems\":\"[E → E · + T, __S__ → E ·]\",\"equalItems\":\"[]\"}, {\"id\":\"2\",\"coreItems\":\"[E → T ·, T → T · * F]\",\"equalItems\":\"[]\"}, {\"id\":\"3\",\"coreItems\":\"[F → ( · E )]\",\"equalItems\":\"[E → · E + T, E → · T, F → · ( E ), F → · id, T → · F, T → · T * F]\"}, {\"id\":\"4\",\"coreItems\":\"[F → id ·]\",\"equalItems\":\"[]\"}, {\"id\":\"5\",\"coreItems\":\"[T → F ·]\",\"equalItems\":\"[]\"}, {\"id\":\"6\",\"coreItems\":\"[E → E + · T]\",\"equalItems\":\"[F → · ( E ), F → · id, T → · F, T → · T * F]\"}, {\"id\":\"7\",\"coreItems\":\"[T → T * · F]\",\"equalItems\":\"[F → · ( E ), F → · id]\"}, {\"id\":\"8\",\"coreItems\":\"[E → E · + T, F → ( E · )]\",\"equalItems\":\"[]\"}, {\"id\":\"9\",\"coreItems\":\"[E → E + T ·, T → T · * F]\",\"equalItems\":\"[]\"}, {\"id\":\"10\",\"coreItems\":\"[T → T * F ·]\",\"equalItems\":\"[]\"}, {\"id\":\"11\",\"coreItems\":\"[F → ( E ) ·]\",\"equalItems\":\"[]\"}]}",
                parser.getClosureJSONString()
        );

        assertEquals(
                "| 状态\\文法符号 | ( | ) | * | + | __$__ | id | E | F | T |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | MOVE_IN \"3\" | \\ | \\ | \\ | \\ | MOVE_IN \"4\" | JUMP \"1\" | JUMP \"5\" | JUMP \"2\" |\n" +
                        "| 1 | \\ | \\ | \\ | MOVE_IN \"6\" | ACCEPT \"__S__ → E\" | \\ | \\ | \\ | \\ |\n" +
                        "| 2 | \\ | REDUCTION \"E → T\" | MOVE_IN \"7\" | REDUCTION \"E → T\" | REDUCTION \"E → T\" | \\ | \\ | \\ | \\ |\n" +
                        "| 3 | MOVE_IN \"3\" | \\ | \\ | \\ | \\ | MOVE_IN \"4\" | JUMP \"8\" | JUMP \"5\" | JUMP \"2\" |\n" +
                        "| 4 | \\ | REDUCTION \"F → id\" | REDUCTION \"F → id\" | REDUCTION \"F → id\" | REDUCTION \"F → id\" | \\ | \\ | \\ | \\ |\n" +
                        "| 5 | \\ | REDUCTION \"T → F\" | REDUCTION \"T → F\" | REDUCTION \"T → F\" | REDUCTION \"T → F\" | \\ | \\ | \\ | \\ |\n" +
                        "| 6 | MOVE_IN \"3\" | \\ | \\ | \\ | \\ | MOVE_IN \"4\" | \\ | JUMP \"5\" | JUMP \"9\" |\n" +
                        "| 7 | MOVE_IN \"3\" | \\ | \\ | \\ | \\ | MOVE_IN \"4\" | \\ | JUMP \"10\" | \\ |\n" +
                        "| 8 | \\ | MOVE_IN \"11\" | \\ | MOVE_IN \"6\" | \\ | \\ | \\ | \\ | \\ |\n" +
                        "| 9 | \\ | REDUCTION \"E → E + T\" | MOVE_IN \"7\" | REDUCTION \"E → E + T\" | REDUCTION \"E → E + T\" | \\ | \\ | \\ | \\ |\n" +
                        "| 10 | \\ | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | \\ | \\ | \\ | \\ |\n" +
                        "| 11 | \\ | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | \\ | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }

    @Test
    public void testSLRStatus2() {
        LRParser parser = SLR.create(GrammarCase.SLR_CASE2.JDK_LEXICAL_ANALYZER, GrammarCase.SLR_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"nonTerminator\":{\"B\":\"d\",\"T\":\"b,__$__\",\"__S__\":\"__$__\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"1\":\"[0, a] → 2\",\"2\":\"[0, T] → 1\",\"3\":\"[2, a] → 2\",\"4\":\"[2, B] → 3\",\"5\":\"[2, T] → 4\",\"6\":\"[3, d] → 5\",\"7\":\"[4, b] → 6\"}",
                parser.getClosureTransferTableJSONString()
        );

        assertEquals(
                "{\"closures:\":[{\"id\":\"0\",\"coreItems\":\"[__S__ → · T]\",\"equalItems\":\"[T → __ε__ ·, T → · a B d]\"}, {\"id\":\"1\",\"coreItems\":\"[__S__ → T ·]\",\"equalItems\":\"[]\"}, {\"id\":\"2\",\"coreItems\":\"[T → a · B d]\",\"equalItems\":\"[B → __ε__ ·, B → · T b, T → __ε__ ·, T → · a B d]\"}, {\"id\":\"3\",\"coreItems\":\"[T → a B · d]\",\"equalItems\":\"[]\"}, {\"id\":\"4\",\"coreItems\":\"[B → T · b]\",\"equalItems\":\"[]\"}, {\"id\":\"5\",\"coreItems\":\"[T → a B d ·]\",\"equalItems\":\"[]\"}, {\"id\":\"6\",\"coreItems\":\"[B → T b ·]\",\"equalItems\":\"[]\"}]}",
                parser.getClosureJSONString()
        );

        assertEquals(
                "| 状态\\文法符号 | __$__ | a | b | d | B | T |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | REDUCTION \"T → __ε__\" | MOVE_IN \"2\" | REDUCTION \"T → __ε__\" | \\ | \\ | JUMP \"1\" |\n" +
                        "| 1 | ACCEPT \"__S__ → T\" | \\ | \\ | \\ | \\ | \\ |\n" +
                        "| 2 | REDUCTION \"T → __ε__\" | MOVE_IN \"2\" | REDUCTION \"T → __ε__\" | REDUCTION \"B → __ε__\" | JUMP \"3\" | JUMP \"4\" |\n" +
                        "| 3 | \\ | \\ | \\ | MOVE_IN \"5\" | \\ | \\ |\n" +
                        "| 4 | \\ | \\ | MOVE_IN \"6\" | \\ | \\ | \\ |\n" +
                        "| 5 | REDUCTION \"T → a B d\" | \\ | REDUCTION \"T → a B d\" | \\ | \\ | \\ |\n" +
                        "| 6 | \\ | \\ | \\ | REDUCTION \"B → T b\" | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }

    @Test
    public void testLR1Status1() {
        LRParser parser = SLR.create(GrammarCase.LR1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LR1_CASE1.GRAMMAR);

        assertFalse(parser.isLegal());

        assertEquals(
                "{\"nonTerminator\":{\"L\":\"__$__,=\",\"R\":\"__$__,=\",\"S\":\"__$__\",\"__S__\":\"__$__\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"1\":\"[0, *] → 2\",\"2\":\"[0, id] → 3\",\"3\":\"[0, L] → 4\",\"4\":\"[0, R] → 5\",\"5\":\"[0, S] → 1\",\"6\":\"[2, *] → 2\",\"7\":\"[2, id] → 3\",\"8\":\"[2, L] → 7\",\"9\":\"[2, R] → 6\",\"10\":\"[4, =] → 8\",\"11\":\"[8, *] → 2\",\"12\":\"[8, id] → 3\",\"13\":\"[8, L] → 7\",\"14\":\"[8, R] → 9\"}",
                parser.getClosureTransferTableJSONString()
        );

        assertEquals(
                "{\"closures:\":[{\"id\":\"0\",\"coreItems\":\"[__S__ → · S]\",\"equalItems\":\"[L → · * R, L → · id, R → · L, S → · L = R, S → · R]\"}, {\"id\":\"1\",\"coreItems\":\"[__S__ → S ·]\",\"equalItems\":\"[]\"}, {\"id\":\"2\",\"coreItems\":\"[L → * · R]\",\"equalItems\":\"[L → · * R, L → · id, R → · L]\"}, {\"id\":\"3\",\"coreItems\":\"[L → id ·]\",\"equalItems\":\"[]\"}, {\"id\":\"4\",\"coreItems\":\"[R → L ·, S → L · = R]\",\"equalItems\":\"[]\"}, {\"id\":\"5\",\"coreItems\":\"[S → R ·]\",\"equalItems\":\"[]\"}, {\"id\":\"6\",\"coreItems\":\"[L → * R ·]\",\"equalItems\":\"[]\"}, {\"id\":\"7\",\"coreItems\":\"[R → L ·]\",\"equalItems\":\"[]\"}, {\"id\":\"8\",\"coreItems\":\"[S → L = · R]\",\"equalItems\":\"[L → · * R, L → · id, R → · L]\"}, {\"id\":\"9\",\"coreItems\":\"[S → L = R ·]\",\"equalItems\":\"[]\"}]}",
                parser.getClosureJSONString()
        );

        assertEquals(
                "| 状态\\文法符号 | * | = | __$__ | id | L | R | S |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | MOVE_IN \"2\" | \\ | \\ | MOVE_IN \"3\" | JUMP \"4\" | JUMP \"5\" | JUMP \"1\" |\n" +
                        "| 1 | \\ | \\ | ACCEPT \"__S__ → S\" | \\ | \\ | \\ | \\ |\n" +
                        "| 2 | MOVE_IN \"2\" | \\ | \\ | MOVE_IN \"3\" | JUMP \"7\" | JUMP \"6\" | \\ |\n" +
                        "| 3 | \\ | REDUCTION \"L → id\" | REDUCTION \"L → id\" | \\ | \\ | \\ | \\ |\n" +
                        "| 4 | \\ | REDUCTION \"R → L\" / MOVE_IN \"8\" | REDUCTION \"R → L\" | \\ | \\ | \\ | \\ |\n" +
                        "| 5 | \\ | \\ | REDUCTION \"S → R\" | \\ | \\ | \\ | \\ |\n" +
                        "| 6 | \\ | REDUCTION \"L → * R\" | REDUCTION \"L → * R\" | \\ | \\ | \\ | \\ |\n" +
                        "| 7 | \\ | REDUCTION \"R → L\" | REDUCTION \"R → L\" | \\ | \\ | \\ | \\ |\n" +
                        "| 8 | MOVE_IN \"2\" | \\ | \\ | MOVE_IN \"3\" | JUMP \"7\" | JUMP \"9\" | \\ |\n" +
                        "| 9 | \\ | \\ | REDUCTION \"S → L = R\" | \\ | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }

    @Test
    public void testSLRCase1() {
        LRParser parser = SLR.create(GrammarCase.SLR_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.SLR_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.SLR_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.SLR_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = SLR.create(GrammarCase.SLR_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.SLR_CASE1.GRAMMAR);

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
        LRParser parser = SLR.create(GrammarCase.SLR_CASE2.JDK_LEXICAL_ANALYZER, GrammarCase.SLR_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.SLR_CASE2.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.SLR_CASE2.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = SLR.create(GrammarCase.SLR_CASE2.NFA_LEXICAL_ANALYZER, GrammarCase.SLR_CASE2.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.SLR_CASE2.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.SLR_CASE2.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }
}
