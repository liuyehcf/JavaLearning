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
                "{\"productions\":[\"S → b B B\",\"__S__ → S\"]}",
                convertedGrammar.toString()
        );
    }

    @Test
    public void convertCase2() {

        Grammar convertedGrammar = new AugmentedGrammarConverter(GrammarCase.Ambiguity_CASE1.GRAMMAR).getConvertedGrammar();

        assertEquals(
                "{\"productions\":[\"E → ( E )\",\"E → id\",\"E → E * E\",\"E → E + E\",\"__S__ → E\"]}",
                convertedGrammar.toString()
        );
    }

}
