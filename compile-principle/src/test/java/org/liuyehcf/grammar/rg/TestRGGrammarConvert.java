package org.liuyehcf.grammar.rg;

import org.junit.Test;
import org.liuyehcf.grammar.definition.Grammar;
import org.liuyehcf.grammar.definition.PrimaryProduction;
import org.liuyehcf.grammar.definition.Production;
import org.liuyehcf.grammar.definition.Symbol;

import static org.junit.Assert.assertEquals;
import static org.liuyehcf.grammar.definition.Symbol.createNonTerminator;
import static org.liuyehcf.grammar.definition.Symbol.createTerminator;

public class TestRGGrammarConvert {
    @Test
    public void convertCase1() {
        Symbol digit = createNonTerminator("digit");
        Symbol letter_ = createNonTerminator("letter_");
        Symbol id = createNonTerminator("id");

        Grammar grammar = Grammar.create(
                id,
                Production.create(
                        digit,
                        PrimaryProduction.create(
                                createTerminator("["),
                                createTerminator("0"),
                                createTerminator("1"),
                                createTerminator("]")
                        )
                ),
                Production.create(
                        letter_,
                        PrimaryProduction.create(
                                createTerminator("["),
                                createTerminator("a"),
                                createTerminator("b"),
                                createTerminator("]")
                        )
                ),
                Production.create(
                        id,
                        PrimaryProduction.create(
                                letter_,
                                createTerminator("("),
                                letter_,
                                createTerminator("|"),
                                digit,
                                createTerminator(")"),
                                createTerminator("*")
                        )
                )
        );

        System.out.println(grammar);

        Grammar convertedGrammar = RG.GrammarConverter.convert(grammar);

        assertEquals(
                "{\"productions\":[\"id → ( [ a b ] ) ( ( [ a b ] ) | ( [ 0 1 ] ) ) *\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }
}
