package org.liuyehcf.compile.compiler;

import org.junit.Test;
import org.liuyehcf.compile.definition.Grammar;

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

        Grammar convertedGrammar = new LL1Compiler(grammar).getGrammar();

        assertEquals(
                convertedGrammar.toString(),
                "{\"productions\":[{\"left\":{\"isTerminator\":\"false\", \"value\":\"E\"}, \"right\":[{\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"(\"}, {\"isTerminator\":\"false\", \"value\":\"E\"}, {\"isTerminator\":\"true\", \"value\":\")\"}, {\"isTerminator\":\"false\", \"value\":\"E′\"}]}, {\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"id\"}, {\"isTerminator\":\"false\", \"value\":\"E′\"}]}]}, {\"left\":{\"isTerminator\":\"false\", \"value\":\"E′\"}, \"right\":[{\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"+\"}, {\"isTerminator\":\"false\", \"value\":\"E\"}, {\"isTerminator\":\"false\", \"value\":\"E′\"}]}, {\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"*\"}, {\"isTerminator\":\"false\", \"value\":\"E\"}, {\"isTerminator\":\"false\", \"value\":\"E′\"}]}, {\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"__EPSILON__\"}]}]}]}"
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

        Grammar convertedGrammar = new LL1Compiler(grammar).getGrammar();

        assertEquals(
                convertedGrammar.toString(),
                "{\"productions\":[{\"left\":{\"isTerminator\":\"false\", \"value\":\"D\"}, \"right\":[{\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"(\"}, {\"isTerminator\":\"false\", \"value\":\"E\"}, {\"isTerminator\":\"true\", \"value\":\")\"}, {\"isTerminator\":\"false\", \"value\":\"E′\"}]}, {\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"id\"}, {\"isTerminator\":\"false\", \"value\":\"E′\"}]}]}, {\"left\":{\"isTerminator\":\"false\", \"value\":\"E\"}, \"right\":[{\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"(\"}, {\"isTerminator\":\"false\", \"value\":\"E\"}, {\"isTerminator\":\"true\", \"value\":\")\"}, {\"isTerminator\":\"false\", \"value\":\"E′\"}]}, {\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"id\"}, {\"isTerminator\":\"false\", \"value\":\"E′\"}]}]}, {\"left\":{\"isTerminator\":\"false\", \"value\":\"E′\"}, \"right\":[{\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"+\"}, {\"isTerminator\":\"false\", \"value\":\"E\"}, {\"isTerminator\":\"false\", \"value\":\"E′\"}]}, {\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"*\"}, {\"isTerminator\":\"false\", \"value\":\"E\"}, {\"isTerminator\":\"false\", \"value\":\"E′\"}]}, {\"symbols\":[{\"isTerminator\":\"true\", \"value\":\"__EPSILON__\"}]}]}]}"
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

        System.out.println(new LL1Compiler(grammar).getGrammar());
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

        System.out.println(new LL1Compiler(grammar).getGrammar());
    }
}
