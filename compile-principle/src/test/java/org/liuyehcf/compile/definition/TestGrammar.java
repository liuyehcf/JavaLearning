package org.liuyehcf.compile.definition;

import org.junit.Test;

import static org.liuyehcf.compile.definition.Grammar.createGrammar;
import static org.liuyehcf.compile.definition.Production.createProduction;
import static org.liuyehcf.compile.definition.Symbol.createNonTerminator;
import static org.liuyehcf.compile.definition.Symbol.createTerminator;
import static org.liuyehcf.compile.definition.SymbolSequence.createSymbolSequence;

public class TestGrammar {
    @Test
    public void testCreateGrammar() {
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

        System.out.println(grammar);
    }
}
