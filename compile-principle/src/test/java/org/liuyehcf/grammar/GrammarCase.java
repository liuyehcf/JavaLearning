package org.liuyehcf.grammar;

import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Production;

import static org.liuyehcf.grammar.core.definition.Symbol.createNonTerminator;
import static org.liuyehcf.grammar.core.definition.Symbol.createTerminator;

public abstract class GrammarCase {
    public static Grammar GRAMMAR_CASE_1 = Grammar.create(
            createNonTerminator("E"),
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
}
