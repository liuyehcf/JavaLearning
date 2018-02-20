package org.liuyehcf.compile.compiler;

import org.junit.Test;
import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.Symbol;

import static org.junit.Assert.assertEquals;
import static org.liuyehcf.compile.utils.DefinitionUtils.*;

public class TestLL1Compiler {
    @Test
    public void testGrammarConvert1() {
        Grammar grammar = createGrammar(
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createNonTerminator("E"),
                                createTerminator("+"),
                                createNonTerminator("E")
                        )
                ),
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createNonTerminator("E"),
                                createTerminator("*"),
                                createNonTerminator("E")
                        )
                ),
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createTerminator("id")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"E → (E)E′|idE′\",\"E′ → +EE′|*EE′|__EPSILON__\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void testGrammarConvert2() {
        Grammar grammar = createGrammar(
                createProduction(
                        createNonTerminator("D"),
                        createSymbolSequence(
                                createNonTerminator("E")
                        )
                ),
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createNonTerminator("E"),
                                createTerminator("+"),
                                createNonTerminator("E")
                        )
                ),
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createNonTerminator("E"),
                                createTerminator("*"),
                                createNonTerminator("E")
                        )
                ),
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createTerminator("id")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"D → (E)E′|idE′\",\"E → (E)E′|idE′\",\"E′ → +EE′|*EE′|__EPSILON__\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }


    @Test
    public void testGrammarConvert3() {
        Grammar grammar = createGrammar(
                createProduction(
                        createNonTerminator("D"),
                        createSymbolSequence(
                                createNonTerminator("E"),
                                createTerminator("e")
                        )
                ),
                createProduction(
                        createNonTerminator("D"),
                        createSymbolSequence(
                                createTerminator("e"),
                                createNonTerminator("E")
                        )
                ),
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createNonTerminator("E"),
                                createTerminator("+"),
                                createNonTerminator("E")
                        )
                ),
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createNonTerminator("E"),
                                createTerminator("*"),
                                createNonTerminator("E")
                        )
                ),
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createTerminator("id")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"D → (E)E′e|idE′e|eE\",\"E → (E)E′|idE′\",\"E′ → +EE′|*EE′|__EPSILON__\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void testCommonPrefixExtract1() {
        Grammar grammar = createGrammar(
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("a"),
                                createTerminator("b")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("a"),
                                createTerminator("b"),
                                createTerminator("c")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("b"),
                                createTerminator("d")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("b"),
                                createTerminator("c")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"A → bA′′|aA′\",\"A′ → bA′′′\",\"A′′ → d|c\",\"A′′′ → __EPSILON__|c\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void testCommonPrefixExtract2() {
        Grammar grammar = createGrammar(
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("a"),
                                createTerminator("β1")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("a"),
                                createTerminator("β2")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("a"),
                                createTerminator("βn")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("γ1")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("γ2")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("γm")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"A → aA′|γ1|γ2|γm\",\"A′ → β1|β2|βn\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void testCommonPrefixExtract3() {
        Grammar grammar = createGrammar(
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("a")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("a"),
                                createTerminator("b")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("a"),
                                createTerminator("b"),
                                createTerminator("c")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("a"),
                                createTerminator("b"),
                                createTerminator("c"),
                                createTerminator("d")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("b")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("b"),
                                createTerminator("c")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("b"),
                                createTerminator("c"),
                                createTerminator("d")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("c")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("c"),
                                createTerminator("d")
                        )
                ),
                createProduction(
                        createNonTerminator("A"),
                        createSymbolSequence(
                                createTerminator("d")
                        )
                )
        );

        Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"A → cA′′′|bA′′|aA′|d\",\"A′ → bA′′′′′|__EPSILON__\",\"A′′ → cA′′′′|__EPSILON__\",\"A′′′ → __EPSILON__|d\",\"A′′′′ → __EPSILON__|d\",\"A′′′′′ → cA′′′′′′|__EPSILON__\",\"A′′′′′′ → __EPSILON__|d\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void testFirst1() {
        Grammar grammar = createGrammar(
                createProduction(
                        createNonTerminator("E"),
                        createSymbolSequence(
                                createNonTerminator("T"),
                                createNonTerminator("E^")
                        )
                ),
                createProduction(
                        createNonTerminator("E^"),
                        createSymbolSequence(
                                createTerminator("+"),
                                createNonTerminator("T"),
                                createNonTerminator("E^")
                        )
                ),
                createProduction(
                        createNonTerminator("E^"),
                        createSymbolSequence(
                                Symbol.EPSILON
                        )
                ),
                createProduction(
                        createNonTerminator("T"),
                        createSymbolSequence(
                                createNonTerminator("F"),
                                createNonTerminator("T^")
                        )
                ),
                createProduction(
                        createNonTerminator("T^"),
                        createSymbolSequence(
                                createTerminator("*"),
                                createNonTerminator("F"),
                                createNonTerminator("T^")
                        )
                ),
                createProduction(
                        createNonTerminator("T^"),
                        createSymbolSequence(
                                Symbol.EPSILON
                        )
                ),
                createProduction(
                        createNonTerminator("F"),
                        createSymbolSequence(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                createProduction(
                        createNonTerminator("F"),
                        createSymbolSequence(
                                createTerminator("id")
                        )
                )
        );

        LL1Compiler compiler = new LL1Compiler(grammar);
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"T → (E)T^|idT^\",\"E → (E)T^E^|idT^E^\",\"F → (E)|id\",\"E^ → +TE^|__EPSILON__\",\"T^ → *FT^|__EPSILON__\"]}",
                convertedGrammar.toReadableJSONString()
        );
        assertEquals(
                "{\"terminator\":{\"__EPSILON__\":\"__EPSILON__\",\"(\":\"(\",\")\":\")\",\"*\":\"*\",\"id\":\"id\",\"+\":\"+\"},\"nonTerminator\":{\"T\":\"(,id\",\"E\":\"(,id\",\"F\":\"(,id\",\"E^\":\"__EPSILON__,+\",\"T^\":\"__EPSILON__,*\"}}",
                compiler.getFirstReadableJSONString()
        );
    }
}
