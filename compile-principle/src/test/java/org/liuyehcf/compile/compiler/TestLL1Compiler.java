package org.liuyehcf.compile.compiler;

import org.junit.Test;
import org.liuyehcf.compile.definition.Grammar;

import static org.liuyehcf.compile.utils.DefinitionUtils.*;

public class TestLL1Compiler {
    @Test
    public void testGrammarConvert() {
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

        new LL1Compiler(grammar);
    }
}
