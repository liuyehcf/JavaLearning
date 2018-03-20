package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.core.definition.Grammar;

import static org.junit.Assert.assertEquals;

public class TestLR0 {
    @Test
    public void testGrammarConvertCase1() {
        LRParser parser = new LR0(GrammarCase.GRAMMAR_CASE_2);

        Grammar convertedGrammar = parser.getGrammar();

        assertEquals(
                "{\"productions\":[\"__START__ → · S | S ·\",\"B → · a B | a · B | a B · | · b | b ·\",\"S → · B B | B · B | B B ·\"]}",
                convertedGrammar.toJSONString()
        );

        assertEquals(
                "{\"0\":[\"B → · a B\",\"S → · B B\",\"__START__ → · S\",\"B → · b\"],\"1\":[\"B → · a B\",\"B → a · B\",\"B → · b\"],\"2\":[\"B → · a B\",\"S → B · B\",\"B → · b\"],\"3\":[\"__START__ → S ·\"],\"4\":[\"B → b ·\"],\"5\":[\"B → a B ·\"],\"6\":[\"S → B B ·\"]}",
                parser.getClosureStatus()
        );

        assertEquals(
                "| 状态\\文法符号 | a | b | __DOLLAR__ | B | S |\n" +
                        "|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | MOVE_IN -- B → · a B | MOVE_IN -- B → · b | \\ | JUMP -- 2 | JUMP -- 3 |\n" +
                        "| 1 | MOVE_IN -- B → · a B | MOVE_IN -- B → · b | \\ | JUMP -- 5 | \\ |\n" +
                        "| 2 | MOVE_IN -- B → · a B | MOVE_IN -- B → · b | \\ | JUMP -- 6 | \\ |\n" +
                        "| 3 | \\ | \\ | ACCEPT -- __START__ → S · | \\ | \\ |\n" +
                        "| 4 | REDUCTION -- B → b · | REDUCTION -- B → b · | REDUCTION -- B → b · | \\ | \\ |\n" +
                        "| 5 | REDUCTION -- B → a B · | REDUCTION -- B → a B · | REDUCTION -- B → a B · | \\ | \\ |\n" +
                        "| 6 | REDUCTION -- S → B B · | REDUCTION -- S → B B · | REDUCTION -- S → B B · | \\ | \\ |\n",
                parser.getForecastAnalysisTable()
        );
    }
}
