package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestLALR {
    @Test
    public void testLR1Status1() {

        LRParser parser = LALR.create(GrammarCase.LR1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LR1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"nonTerminator\":{\"L\":\"__$__,=\",\"R\":\"__$__,=\",\"S\":\"__$__\",\"__S__\":\"__$__\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"1\":\"[0, *] → 2\",\"2\":\"[0, id] → 3\",\"3\":\"[0, L] → 4\",\"4\":\"[0, R] → 5\",\"5\":\"[0, S] → 1\",\"6\":\"[2, *] → 2\",\"7\":\"[2, id] → 3\",\"8\":\"[2, L] → 7\",\"9\":\"[2, R] → 6\",\"10\":\"[4, =] → 8\",\"11\":\"[8, *] → 2\",\"12\":\"[8, id] → 3\",\"13\":\"[8, L] → 7\",\"14\":\"[8, R] → 9\",\"15\":\"[10, *] → 2\",\"16\":\"[10, id] → 3\",\"17\":\"[10, L] → 7\",\"18\":\"[10, R] → 6\"}",
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
                        "| 8 | MOVE_IN \"2\" | \\ | \\ | MOVE_IN \"3\" | JUMP \"7\" | JUMP \"9\" | \\ |\n" +
                        "| 9 | \\ | \\ | REDUCTION \"S → L = R\" | \\ | \\ | \\ | \\ |\n" +
                        "| 10 | MOVE_IN \"2\" | \\ | \\ | MOVE_IN \"3\" | JUMP \"7\" | JUMP \"6\" | \\ |\n" +
                        "| 11 | \\ | \\ | REDUCTION \"L → id\" | \\ | \\ | \\ | \\ |\n" +
                        "| 12 | \\ | \\ | REDUCTION \"R → L\" | \\ | \\ | \\ | \\ |\n" +
                        "| 13 | \\ | \\ | REDUCTION \"L → * R\" | \\ | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );

    }

    @Test
    public void testLR1Case1() {
        LRParser parser = LALR.create(GrammarCase.LR1_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LR1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LR1_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LR1_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }

        parser = LALR.create(GrammarCase.LR1_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.LR1_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LR1_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LR1_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }

}
