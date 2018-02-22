package org.liuyehcf.compile;

import org.junit.Test;
import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.PrimaryProduction;
import org.liuyehcf.compile.definition.Production;

import static org.junit.Assert.assertEquals;
import static org.liuyehcf.compile.definition.Symbol.createNonTerminator;
import static org.liuyehcf.compile.definition.Symbol.createTerminator;

public class TestLRParserGrammarConvert {

    @Test
    public void convertCase1() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("S"),
                        PrimaryProduction.create(
                                createTerminator("b"),
                                createNonTerminator("B"),
                                createNonTerminator("B")
                        )
                )
        );

        Parser parser = new LR0(grammar);

        assertEquals(
                "{\"productions\":[\"S → __DOT__ b B B \",\"S → b __DOT__ B B \",\"S → b B __DOT__ B \",\"S → b B B __DOT__ \",\"__START__ → __DOT__ S \",\"__START__ → S __DOT__ \"]}",
                parser.getGrammar().toReadableJSONString()
        );
    }

    @Test
    public void convertCase2() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("E"),
                        PrimaryProduction.create(
                                createNonTerminator("E"),
                                createTerminator("+"),
                                createNonTerminator("E")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        PrimaryProduction.create(
                                createNonTerminator("E"),
                                createTerminator("*"),
                                createNonTerminator("E")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        PrimaryProduction.create(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        PrimaryProduction.create(
                                createTerminator("id")
                        )
                )
        );

        Parser parser = new LR0(grammar);

        assertEquals(
                "{\"productions\":[\"E → __DOT__ E + E \",\"E → E __DOT__ + E \",\"E → E + __DOT__ E \",\"E → E + E __DOT__ \",\"E → __DOT__ E * E \",\"E → E __DOT__ * E \",\"E → E * __DOT__ E \",\"E → E * E __DOT__ \",\"E → __DOT__ ( E ) \",\"E → ( __DOT__ E ) \",\"E → ( E __DOT__ ) \",\"E → ( E ) __DOT__ \",\"E → __DOT__ id \",\"E → id __DOT__ \",\"__START__ → __DOT__ E \",\"__START__ → E __DOT__ \"]}",
                parser.getGrammar().toReadableJSONString()
        );
    }
}
