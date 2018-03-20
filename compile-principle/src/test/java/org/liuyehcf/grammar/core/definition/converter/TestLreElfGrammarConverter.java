package org.liuyehcf.grammar.core.definition.converter;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Production;
import org.liuyehcf.grammar.core.definition.SymbolString;

import static org.junit.Assert.assertEquals;
import static org.liuyehcf.grammar.core.definition.Symbol.createNonTerminator;
import static org.liuyehcf.grammar.core.definition.Symbol.createTerminator;

public class TestLreElfGrammarConverter {
    @Test
    public void convertCase1() {

        Grammar convertedGrammar = getGrammarConverterPipeline().convert(GrammarCase.GRAMMAR_CASE_1);

        assertEquals(
                "{\"productions\":[\"E → ( E ) (E)′ | id (E)′\",\"(E)′ → + E (E)′ | * E (E)′ | __EPSILON__\"]}",
                convertedGrammar.toJSONString()
        );
    }

    @Test
    public void convertCase2() {
        Grammar grammar = Grammar.create(
                createNonTerminator("D"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("D"),
                                SymbolString.create(
                                        createNonTerminator("E")
                                )
                        )
                ),
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

        Grammar convertedGrammar = getGrammarConverterPipeline().convert(grammar);

        assertEquals(
                "{\"productions\":[\"D → ( E ) (E)′ | id (E)′\",\"E → ( E ) (E)′ | id (E)′\",\"(E)′ → + E (E)′ | * E (E)′ | __EPSILON__\"]}",
                convertedGrammar.toJSONString()
        );
    }

    @Test
    public void convertCase3() {
        Grammar grammar = Grammar.create(
                createNonTerminator("D"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("D"),
                                SymbolString.create(
                                        createNonTerminator("E"),
                                        createTerminator("e")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("D"),
                                SymbolString.create(
                                        createTerminator("e"),
                                        createNonTerminator("E")
                                )
                        )
                ),
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

        Grammar convertedGrammar = getGrammarConverterPipeline().convert(grammar);

        assertEquals(
                "{\"productions\":[\"D → ( E ) (E)′ e | id (E)′ e | e E\",\"E → ( E ) (E)′ | id (E)′\",\"(E)′ → + E (E)′ | * E (E)′ | __EPSILON__\"]}",
                convertedGrammar.toJSONString()
        );
    }

    @Test
    public void convertCase4() {
        Grammar grammar = Grammar.create(
                createNonTerminator("A"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createTerminator("b")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createTerminator("b"),
                                        createTerminator("c")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("b"),
                                        createTerminator("d")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("b"),
                                        createTerminator("c")
                                )
                        )
                )
        );

        Grammar convertedGrammar = getGrammarConverterPipeline().convert(grammar);

        assertEquals(
                "{\"productions\":[\"A → b (A)′′ | a (A)′\",\"(A)′ → b (A)′′′\",\"(A)′′ → d | c\",\"(A)′′′ → __EPSILON__ | c\"]}",
                convertedGrammar.toJSONString()
        );
    }

    @Test
    public void convertCase5() {
        Grammar grammar = Grammar.create(
                createNonTerminator("A"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createTerminator("β1")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createTerminator("β2")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createTerminator("βn")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("γ1")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("γ2")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("γm")
                                )
                        )
                )
        );

        Grammar convertedGrammar = getGrammarConverterPipeline().convert(grammar);

        assertEquals(
                "{\"productions\":[\"A → a (A)′ | γ1 | γ2 | γm\",\"(A)′ → β1 | β2 | βn\"]}",
                convertedGrammar.toJSONString()
        );
    }

    @Test
    public void convertCase6() {
        Grammar grammar = Grammar.create(
                createNonTerminator("A"),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createTerminator("b")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createTerminator("b"),
                                        createTerminator("c")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("a"),
                                        createTerminator("b"),
                                        createTerminator("c"),
                                        createTerminator("d")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("b")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("b"),
                                        createTerminator("c")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("b"),
                                        createTerminator("c"),
                                        createTerminator("d")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("c")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("c"),
                                        createTerminator("d")
                                )
                        )
                ),
                Production.create(
                        PrimaryProduction.create(
                                createNonTerminator("A"),
                                SymbolString.create(
                                        createTerminator("d")
                                )
                        )
                )
        );

        Grammar convertedGrammar = getGrammarConverterPipeline().convert(grammar);

        assertEquals(
                "{\"productions\":[\"A → c (A)′′′ | b (A)′′ | a (A)′ | d\",\"(A)′ → b (A)′′′′′ | __EPSILON__\",\"(A)′′ → c (A)′′′′ | __EPSILON__\",\"(A)′′′ → __EPSILON__ | d\",\"(A)′′′′ → __EPSILON__ | d\",\"(A)′′′′′ → c (A)′′′′′′ | __EPSILON__\",\"(A)′′′′′′ → __EPSILON__ | d\"]}",
                convertedGrammar.toJSONString()
        );
    }

    private GrammarConverterPipeline getGrammarConverterPipeline() {
        return GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(MergeGrammarConverter.class)
                .registerGrammarConverter(LreElfGrammarConverter.class)
                .build();
    }
}
