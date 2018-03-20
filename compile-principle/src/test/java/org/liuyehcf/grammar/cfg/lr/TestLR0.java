package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.core.definition.Grammar;

import static org.junit.Assert.assertEquals;

public class TestLR0 {
    @Test
    public void testGrammarConvertCase1() {
        Grammar convertedGrammar = new LR0(GrammarCase.GRAMMAR_CASE_1).getGrammar();

        assertEquals(
                "{\"productions\":[\"__START__ → __DOT__ E | E __DOT__\",\"E → __DOT__ E + E | E __DOT__ + E | E + __DOT__ E | E + E __DOT__ | __DOT__ E * E | E __DOT__ * E | E * __DOT__ E | E * E __DOT__ | __DOT__ ( E ) | ( __DOT__ E ) | ( E __DOT__ ) | ( E ) __DOT__ | __DOT__ id | id __DOT__\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }
}
