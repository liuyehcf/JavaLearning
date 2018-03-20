package org.liuyehcf.grammar.core.definition.converter;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Production;
import org.liuyehcf.grammar.core.definition.SymbolString;

import static org.junit.Assert.assertEquals;
import static org.liuyehcf.grammar.core.definition.Symbol.createNonTerminator;
import static org.liuyehcf.grammar.core.definition.Symbol.createTerminator;

public class TestAugmentedGrammarConverter {
    @Test
    public void convertCase1() {
        Grammar grammar = Grammar.create(
                createNonTerminator("S"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("S"),
                                SymbolString.create(
                                        createTerminator("b"),
                                        createNonTerminator("B"),
                                        createNonTerminator("B")
                                )
                        )
                )
        );

        Grammar convertedGrammar = new AugmentedGrammarConverter(grammar).getConvertedGrammar();

        assertEquals(
                "{\"productions\":[\"__START__ → S\",\"S → b B B\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void convertCase2() {

        Grammar convertedGrammar = new AugmentedGrammarConverter(GrammarCase.GRAMMAR_CASE_1).getConvertedGrammar();

        assertEquals(
                "{\"productions\":[\"__START__ → E\",\"E → E + E\",\"E → E * E\",\"E → ( E )\",\"E → id\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

}
