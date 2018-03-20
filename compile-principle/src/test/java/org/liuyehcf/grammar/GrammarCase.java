package org.liuyehcf.grammar;

import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Production;
import org.liuyehcf.grammar.core.definition.SymbolString;

import static org.liuyehcf.grammar.core.definition.Symbol.createNonTerminator;
import static org.liuyehcf.grammar.core.definition.Symbol.createTerminator;

public abstract class GrammarCase {
    public static Grammar GRAMMAR_CASE_1 = Grammar.create(
            createNonTerminator("E"),
            Production.create(
                    PrimaryProduction.create(
                            createNonTerminator("E"),
                            SymbolString.create(
                                    createNonTerminator("E"),
                                    createTerminator("+"),
                                    createNonTerminator("E")
                            )
                    )
            ),
            Production.create(
                    PrimaryProduction.create(
                            createNonTerminator("E"),
                            SymbolString.create(
                                    createNonTerminator("E"),
                                    createTerminator("*"),
                                    createNonTerminator("E")
                            )
                    )
            ),
            Production.create(
                    PrimaryProduction.create(
                            createNonTerminator("E"),
                            SymbolString.create(
                                    createTerminator("("),
                                    createNonTerminator("E"),
                                    createTerminator(")")
                            )
                    )
            ),
            Production.create(
                    PrimaryProduction.create(
                            createNonTerminator("E"),
                            SymbolString.create(
                                    createTerminator("id")
                            )
                    )
            )
    );


    public static Grammar GRAMMAR_CASE_2 = Grammar.create(
            createNonTerminator("S"),
            Production.create(
                    PrimaryProduction.create(
                            createNonTerminator("S"),
                            SymbolString.create(
                                    createNonTerminator("B"),
                                    createNonTerminator("B")
                            )
                    )
            ),
            Production.create(
                    PrimaryProduction.create(
                            createNonTerminator("B"),
                            SymbolString.create(
                                    createTerminator("a"),
                                    createNonTerminator("B")
                            )
                    )
            ),
            Production.create(
                    PrimaryProduction.create(
                            createNonTerminator("B"),
                            SymbolString.create(
                                    createTerminator("b")
                            )
                    )
            )
    );
}
