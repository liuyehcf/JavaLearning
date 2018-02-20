package org.liuyehcf.compile.definition;

import org.junit.Test;

import static org.liuyehcf.compile.definition.Grammar.create;
import static org.liuyehcf.compile.definition.Production.create;
import static org.liuyehcf.compile.definition.Symbol.createNonTerminator;
import static org.liuyehcf.compile.definition.Symbol.createTerminator;
import static org.liuyehcf.compile.definition.SymbolSequence.create;

public class TestGrammar {
    @Test
    public void testCreateGrammar() {
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

        System.out.println(grammar);
    }
}
