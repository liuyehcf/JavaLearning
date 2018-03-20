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
                "{\"productions\":[\"__START__ → · S | S ·\",\"B → · a B | a · B | a B · | · b | b ·\",\"S → · B B | B · B | B B ·\"]}",
                convertedGrammar.toJSONString()
        );
    }
}
