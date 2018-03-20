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

public class TestStatusExpandGrammarConverter {

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

        Grammar convertedGrammar = getGrammarConverterPipeline().convert(grammar);

        assertEquals(
                "{\"productions\":[\"S → __DOT__ b B B\",\"S → b __DOT__ B B\",\"S → b B __DOT__ B\",\"S → b B B __DOT__\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void convertCase2() {

        Grammar convertedGrammar = getGrammarConverterPipeline().convert(GrammarCase.GRAMMAR_CASE_1);

        assertEquals(
                "{\"productions\":[\"E → __DOT__ E + E\",\"E → E __DOT__ + E\",\"E → E + __DOT__ E\",\"E → E + E __DOT__\",\"E → __DOT__ E * E\",\"E → E __DOT__ * E\",\"E → E * __DOT__ E\",\"E → E * E __DOT__\",\"E → __DOT__ ( E )\",\"E → ( __DOT__ E )\",\"E → ( E __DOT__ )\",\"E → ( E ) __DOT__\",\"E → __DOT__ id\",\"E → id __DOT__\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    private GrammarConverterPipeline getGrammarConverterPipeline() {
        return GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(MergeGrammarConverter.class)
                .registerGrammarConverter(StatusExpandGrammarConverter.class)
                .build();
    }
}
