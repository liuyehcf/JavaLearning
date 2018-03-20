package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.core.definition.Grammar;

import static org.junit.Assert.assertEquals;

public class TestLR0 {
    @Test
    public void testGrammarConvertCase1() {
        Grammar convertedGrammar = new LR0(GrammarCase.GRAMMAR_CASE_2).getGrammar();

        assertEquals(
                "{\"productions\":[\"__START__ → __DOT__ S | S __DOT__\",\"B → __DOT__ a B | a __DOT__ B | a B __DOT__ | __DOT__ b | b __DOT__\",\"S → __DOT__ B B | B __DOT__ B | B B __DOT__\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }
}
