package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.JdkLexicalAnalyzer;
import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.core.definition.Grammar;

import static org.junit.Assert.*;

public class TestLR0 {
    @Test
    public void testCase1() {
        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("a")
                .addMorpheme("b")
                .build();

        LRParser parser = LR0.create(analyzer, GrammarCase.GRAMMAR_CASE_10);

        Grammar convertedGrammar = parser.getGrammar();

        assertEquals(
                "{\"productions\":[\"__START__ → · S | S ·\",\"B → · a B | a · B | a B · | · b | b ·\",\"S → · B B | B · B | B B ·\"]}",
                convertedGrammar.toJSONString()
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

        assertTrue(parser.matches("bab"));
        assertTrue(parser.matches("bb"));
        assertTrue(parser.matches("aaaabab"));

        assertFalse(parser.matches("a"));
        assertFalse(parser.matches("b"));
        assertFalse(parser.matches("aba"));
    }

    @Test
    public void testCase2() {
        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("*")
                .addMorpheme("+")
                .addMorpheme("id")
                .build();

        LRParser parser = LR0.create(analyzer, GrammarCase.GRAMMAR_CASE_11);

        assertEquals(
                "| 状态\\文法符号 | ( | ) | * | + | id | __DOLLAR__ | T | E | F |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | MOVE_IN \"4\" | \\ | \\ | \\ | MOVE_IN \"5\" | \\ | JUMP \"2\" | JUMP \"1\" | JUMP \"3\" |\n" +
                        "| 1 | \\ | \\ | \\ | MOVE_IN \"6\" | \\ | ACCEPT \"__START__ → E\" | \\ | \\ | \\ |\n" +
                        "| 2 | REDUCTION \"E → T\" | REDUCTION \"E → T\" | REDUCTION \"E → T\" / MOVE_IN \"7\" | REDUCTION \"E → T\" | REDUCTION \"E → T\" | REDUCTION \"E → T\" | \\ | \\ | \\ |\n" +
                        "| 3 | REDUCTION \"T → F\" | REDUCTION \"T → F\" | REDUCTION \"T → F\" | REDUCTION \"T → F\" | REDUCTION \"T → F\" | REDUCTION \"T → F\" | \\ | \\ | \\ |\n" +
                        "| 4 | MOVE_IN \"4\" | \\ | \\ | \\ | MOVE_IN \"5\" | \\ | JUMP \"2\" | JUMP \"8\" | JUMP \"3\" |\n" +
                        "| 5 | REDUCTION \"F → id\" | REDUCTION \"F → id\" | REDUCTION \"F → id\" | REDUCTION \"F → id\" | REDUCTION \"F → id\" | REDUCTION \"F → id\" | \\ | \\ | \\ |\n" +
                        "| 6 | MOVE_IN \"4\" | \\ | \\ | \\ | MOVE_IN \"5\" | \\ | JUMP \"9\" | \\ | JUMP \"3\" |\n" +
                        "| 7 | MOVE_IN \"4\" | \\ | \\ | \\ | MOVE_IN \"5\" | \\ | \\ | \\ | JUMP \"10\" |\n" +
                        "| 8 | \\ | MOVE_IN \"11\" | \\ | MOVE_IN \"6\" | \\ | \\ | \\ | \\ | \\ |\n" +
                        "| 9 | REDUCTION \"E → E + T\" | REDUCTION \"E → E + T\" | REDUCTION \"E → E + T\" / MOVE_IN \"7\" | REDUCTION \"E → E + T\" | REDUCTION \"E → E + T\" | REDUCTION \"E → E + T\" | \\ | \\ | \\ |\n" +
                        "| 10 | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | REDUCTION \"T → T * F\" | \\ | \\ | \\ |\n" +
                        "| 11 | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | REDUCTION \"F → ( E )\" | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }
}
