package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.JdkLexicalAnalyzer;
import org.liuyehcf.grammar.LexicalAnalyzer;

import static org.junit.Assert.*;

public class TestLR0 {
    @Test
    public void testLR0Status1() {
        LRParser parser = LR0.create(GrammarCase.LR0_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LR0_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"productions\":[\"__START__ → · S | S ·\",\"B → · a B | a · B | a B · | · b | b ·\",\"S → · B B | B · B | B B ·\"]}",
                parser.getGrammar().toString()
        );

        assertEquals(
                "{\"0\":[\"__START__ → · S\",\"S → · B B\",\"B → · a B\",\"B → · b\"],\"1\":[\"__START__ → S ·\"],\"2\":[\"S → B · B\",\"B → · a B\",\"B → · b\"],\"3\":[\"B → a · B\",\"B → · a B\",\"B → · b\"],\"4\":[\"B → b ·\"],\"5\":[\"S → B B ·\"],\"6\":[\"B → a B ·\"]}",
                parser.getClosureJSONString()
        );

        assertEquals(
                "| 状态\\文法符号 | a | b | __DOLLAR__ | B | S |\n" +
                        "|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | MOVE_IN \"3\" | MOVE_IN \"4\" | \\ | JUMP \"2\" | JUMP \"1\" |\n" +
                        "| 1 | \\ | \\ | ACCEPT \"__START__ → S\" | \\ | \\ |\n" +
                        "| 2 | MOVE_IN \"3\" | MOVE_IN \"4\" | \\ | JUMP \"5\" | \\ |\n" +
                        "| 3 | MOVE_IN \"3\" | MOVE_IN \"4\" | \\ | JUMP \"6\" | \\ |\n" +
                        "| 4 | REDUCTION \"B → b\" | REDUCTION \"B → b\" | REDUCTION \"B → b\" | \\ | \\ |\n" +
                        "| 5 | REDUCTION \"S → B B\" | REDUCTION \"S → B B\" | REDUCTION \"S → B B\" | \\ | \\ |\n" +
                        "| 6 | REDUCTION \"B → a B\" | REDUCTION \"B → a B\" | REDUCTION \"B → a B\" | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }

    @Test
    public void testSLRStatus1() {
        LRParser parser = LR0.create(GrammarCase.SLR_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.SLR_CASE1.GRAMMAR);

        assertFalse(parser.isLegal());

        assertEquals(
                "| 状态\\文法符号 | id | ( | ) | * | + | __DOLLAR__ | T | E | F |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | MOVE_IN \"5\" | MOVE_IN \"4\" | \\ | \\ | \\ | \\ | JUMP \"2\" | JUMP \"1\" | JUMP \"3\" |\n" +
                        "| 1 | \\ | \\ | \\ | \\ | MOVE_IN \"6\" | ACCEPT \"__START__ → E\" | \\ | \\ | \\ |\n" +
                        "| 2 | REDUCTION \"E → T\" | REDUCTION \"E → T\" | REDUCTION \"E → T\" | REDUCTION \"E → T\" / MOVE_IN \"7\" | REDUCTION \"E → T\" | REDUCTION \"E → T\" | \\ | \\ | \\ |\n" +
                        "| 3 | REDUCTION \"T → F\" | REDUCTION \"T → F\" | REDUCTION \"T → F\" | REDUCTION \"T → F\" | REDUCTION \"T → F\" | REDUCTION \"T → F\" | \\ | \\ | \\ |\n" +
                        "| 4 | MOVE_IN \"5\" | MOVE_IN \"4\" | \\ | \\ | \\ | \\ | JUMP \"2\" | JUMP \"8\" | JUMP \"3\" |\n" +
                        "| 5 | REDUCTION \"F → id\" | REDUCTION \"F → id\" | REDUCTION \"F → id\" | REDUCTION \"F → id\" | REDUCTION \"F → id\" | REDUCTION \"F → id\" | \\ | \\ | \\ |\n" +
                        "| 6 | MOVE_IN \"5\" | MOVE_IN \"4\" | \\ | \\ | \\ | \\ | JUMP \"9\" | \\ | JUMP \"3\" |\n" +
                        "| 7 | MOVE_IN \"5\" | MOVE_IN \"4\" | \\ | \\ | \\ | \\ | \\ | \\ | JUMP \"10\" |\n" +
                        "| 8 | \\ | \\ | MOVE_IN \"11\" | \\ | MOVE_IN \"6\" | \\ | \\ | \\ | \\ |\n" +
                        "| 9 | REDUCTION \"E → E + T\" | REDUCTION \"E → E + T\" | REDUCTION \"E → E + T\" | REDUCTION \"E → E + T\" / MOVE_IN \"7\" | REDUCTION \"E → E + T\" | REDUCTION \"E → E + T\" | \\ | \\ | \\ |\n" +
                        "| 10 | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | \\ | \\ | \\ |\n" +
                        "| 11 | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }

    @Test
    public void testLR0Case1() {

        LRParser parser = LR0.create(GrammarCase.LR0_CASE1.JDK_LEXICAL_ANALYZER, GrammarCase.LR0_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LR0_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LR0_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }


        parser = LR0.create(GrammarCase.LR0_CASE1.NFA_LEXICAL_ANALYZER, GrammarCase.LR0_CASE1.GRAMMAR);

        assertTrue(parser.isLegal());

        for (String input : GrammarCase.LR0_CASE1.TRUE_CASES) {
            assertTrue(parser.matches(input));
        }

        for (String input : GrammarCase.LR0_CASE1.FALSE_CASES) {
            assertFalse(parser.matches(input));
        }
    }


}
