package org.liuyehcf.compile.compiler;

import org.junit.Test;
import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.Production;
import org.liuyehcf.compile.definition.Symbol;
import org.liuyehcf.compile.definition.SymbolSequence;

import static org.junit.Assert.assertEquals;
import static org.liuyehcf.compile.definition.Symbol.createNonTerminator;
import static org.liuyehcf.compile.definition.Symbol.createTerminator;

public class TestLL1Compiler {
    @Test
    public void testGrammarConvert1() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createNonTerminator("E"),
                                createTerminator("+"),
                                createNonTerminator("E")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createNonTerminator("E"),
                                createTerminator("*"),
                                createNonTerminator("E")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createTerminator("id")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"E → (E)E′|idE′\",\"E′ → +EE′|*EE′|__EPSILON__\",\"__START__ → (E)E′|idE′\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void testGrammarConvert2() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("D"),
                        SymbolSequence.create(
                                createNonTerminator("E")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createNonTerminator("E"),
                                createTerminator("+"),
                                createNonTerminator("E")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createNonTerminator("E"),
                                createTerminator("*"),
                                createNonTerminator("E")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createTerminator("id")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"D → (E)E′|idE′\",\"E → (E)E′|idE′\",\"E′ → +EE′|*EE′|__EPSILON__\",\"__START__ → (E)E′|idE′\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void testGrammarConvert3() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("D"),
                        SymbolSequence.create(
                                createNonTerminator("E"),
                                createTerminator("e")
                        )
                ),
                Production.create(
                        createNonTerminator("D"),
                        SymbolSequence.create(
                                createTerminator("e"),
                                createNonTerminator("E")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createNonTerminator("E"),
                                createTerminator("+"),
                                createNonTerminator("E")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createNonTerminator("E"),
                                createTerminator("*"),
                                createNonTerminator("E")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createTerminator("id")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"D → (E)E′e|idE′e|eE\",\"E → (E)E′|idE′\",\"E′ → +EE′|*EE′|__EPSILON__\",\"__START__ → (E)E′e|idE′e|eE\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void testCommonPrefixExtract1() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("a"),
                                createTerminator("b")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("a"),
                                createTerminator("b"),
                                createTerminator("c")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("b"),
                                createTerminator("d")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("b"),
                                createTerminator("c")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"A → bA′′|aA′\",\"A′ → bA′′′\",\"A′′ → d|c\",\"A′′′ → __EPSILON__|c\",\"__START__ → bA′′|aA′\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void testCommonPrefixExtract2() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("a"),
                                createTerminator("β1")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("a"),
                                createTerminator("β2")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("a"),
                                createTerminator("βn")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("γ1")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("γ2")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("γm")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"A → aA′|γ1|γ2|γm\",\"A′ → β1|β2|βn\",\"__START__ → aA′|γ1|γ2|γm\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void testCommonPrefixExtract3() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("a")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("a"),
                                createTerminator("b")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("a"),
                                createTerminator("b"),
                                createTerminator("c")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("a"),
                                createTerminator("b"),
                                createTerminator("c"),
                                createTerminator("d")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("b")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("b"),
                                createTerminator("c")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("b"),
                                createTerminator("c"),
                                createTerminator("d")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("c")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("c"),
                                createTerminator("d")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        SymbolSequence.create(
                                createTerminator("d")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"A → cA′′′|bA′′|aA′|d\",\"A′ → bA′′′′′|__EPSILON__\",\"A′′ → cA′′′′|__EPSILON__\",\"A′′′ → __EPSILON__|d\",\"A′′′′ → __EPSILON__|d\",\"A′′′′′ → cA′′′′′′|__EPSILON__\",\"__START__ → cA′′′|bA′′|aA′|d\",\"A′′′′′′ → __EPSILON__|d\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void testFirstFollow1() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("E"),
                        SymbolSequence.create(
                                createNonTerminator("T"),
                                createNonTerminator("E^")
                        )
                ),
                Production.create(
                        createNonTerminator("E^"),
                        SymbolSequence.create(
                                createTerminator("+"),
                                createNonTerminator("T"),
                                createNonTerminator("E^")
                        )
                ),
                Production.create(
                        createNonTerminator("E^"),
                        SymbolSequence.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator("T"),
                        SymbolSequence.create(
                                createNonTerminator("F"),
                                createNonTerminator("T^")
                        )
                ),
                Production.create(
                        createNonTerminator("T^"),
                        SymbolSequence.create(
                                createTerminator("*"),
                                createNonTerminator("F"),
                                createNonTerminator("T^")
                        )
                ),
                Production.create(
                        createNonTerminator("T^"),
                        SymbolSequence.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator("F"),
                        SymbolSequence.create(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                Production.create(
                        createNonTerminator("F"),
                        SymbolSequence.create(
                                createTerminator("id")
                        )
                )
        );

        LL1Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"T → (E)T^|idT^\",\"E → (E)T^E^|idT^E^\",\"F → (E)|id\",\"__START__ → (E)T^E^|idT^E^\",\"E^ → +TE^|__EPSILON__\",\"T^ → *FT^|__EPSILON__\"]}",
                convertedGrammar.toReadableJSONString()
        );
        assertEquals(
                "{\"terminator\":{\"__EPSILON__\":\"__EPSILON__\",\"(\":\"(\",\")\":\")\",\"*\":\"*\",\"id\":\"id\",\"+\":\"+\"},\"nonTerminator\":{\"T\":\"(,id\",\"E\":\"(,id\",\"F\":\"(,id\",\"__START__\":\"(,id\",\"E^\":\"__EPSILON__,+\",\"T^\":\"__EPSILON__,*\"}}",
                compiler.getFirstReadableJSONString()
        );

        System.out.println(compiler.getFollowReadableJSONString());
    }
}
