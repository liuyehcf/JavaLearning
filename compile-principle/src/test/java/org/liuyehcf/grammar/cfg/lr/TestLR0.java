package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.JdkLexicalAnalyzer;
import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.core.definition.Grammar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestLR0 {
    @Test
    public void testGrammarConvertCase1() {
        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("a")
                .addMorpheme("b")
                .build();

        LRParser parser = new LR0(GrammarCase.GRAMMAR_CASE_10, analyzer);

        Grammar convertedGrammar = parser.getGrammar();

        assertEquals(
                "{\"productions\":[\"__START__ → · S | S ·\",\"B → · a B | a · B | a B · | · b | b ·\",\"S → · B B | B · B | B B ·\"]}",
                convertedGrammar.toJSONString()
        );

        assertEquals(
                "{\"0\":[\"__START__ → · S\",\"S → · B B\",\"B → · a B\",\"B → · b\"],\"1\":[\"__START__ → S ·\"],\"2\":[\"S → B · B\",\"B → · a B\",\"B → · b\"],\"3\":[\"B → a · B\",\"B → · a B\",\"B → · b\"],\"4\":[\"B → b ·\"],\"5\":[\"S → B B ·\"],\"6\":[\"B → a B ·\"]}",
                parser.getClosureStatus()
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
                parser.getForecastAnalysisTable()
        );

        assertTrue(parser.matches("bab"));
        assertTrue(parser.matches("bb"));
        assertTrue(parser.matches("aaaabab"));

        assertFalse(parser.matches("a"));
        assertFalse(parser.matches("b"));
        assertFalse(parser.matches("aba"));
    }
}
