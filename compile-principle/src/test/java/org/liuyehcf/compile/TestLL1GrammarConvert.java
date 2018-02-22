package org.liuyehcf.compile;

import org.junit.Test;
import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.PrimaryProduction;
import org.liuyehcf.compile.definition.Production;

import static org.junit.Assert.assertEquals;
import static org.liuyehcf.compile.definition.Symbol.createNonTerminator;
import static org.liuyehcf.compile.definition.Symbol.createTerminator;

public class TestLL1GrammarConvert {
    @Test
    public void convertCase1() {
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

        Grammar convertedGrammar = LL1.GrammarConverter.convert(grammar);

        assertEquals(
                "{\"productions\":[\"__START__ → E\",\"E → ( E ) (E)′ | id (E)′\",\"(E)′ → + E (E)′ | * E (E)′ | __EPSILON__\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void convertCase2() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("D"),
                        PrimaryProduction.create(
                                createNonTerminator("E")
                        )
                ),
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

        Grammar convertedGrammar = LL1.GrammarConverter.convert(grammar);

        assertEquals(
                "{\"productions\":[\"__START__ → D\",\"D → ( E ) (E)′ | id (E)′\",\"E → ( E ) (E)′ | id (E)′\",\"(E)′ → + E (E)′ | * E (E)′ | __EPSILON__\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void convertCase3() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("D"),
                        PrimaryProduction.create(
                                createNonTerminator("E"),
                                createTerminator("e")
                        )
                ),
                Production.create(
                        createNonTerminator("D"),
                        PrimaryProduction.create(
                                createTerminator("e"),
                                createNonTerminator("E")
                        )
                ),
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

        Grammar convertedGrammar = LL1.GrammarConverter.convert(grammar);

        assertEquals(
                "{\"productions\":[\"__START__ → D\",\"D → ( E ) (E)′ e | id (E)′ e | e E\",\"E → ( E ) (E)′ | id (E)′\",\"(E)′ → + E (E)′ | * E (E)′ | __EPSILON__\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void convertCase4() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("a"),
                                createTerminator("b")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("a"),
                                createTerminator("b"),
                                createTerminator("c")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("b"),
                                createTerminator("d")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("b"),
                                createTerminator("c")
                        )
                )
        );

        Grammar convertedGrammar = LL1.GrammarConverter.convert(grammar);

        assertEquals(
                "{\"productions\":[\"__START__ → A\",\"A → b (A)′′ | a (A)′\",\"(A)′ → b (A)′′′\",\"(A)′′ → d | c\",\"(A)′′′ → __EPSILON__ | c\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void convertCase5() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("a"),
                                createTerminator("β1")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("a"),
                                createTerminator("β2")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("a"),
                                createTerminator("βn")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("γ1")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("γ2")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("γm")
                        )
                )
        );

        Grammar convertedGrammar = LL1.GrammarConverter.convert(grammar);

        assertEquals(
                "{\"productions\":[\"__START__ → A\",\"A → a (A)′ | γ1 | γ2 | γm\",\"(A)′ → β1 | β2 | βn\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }

    @Test
    public void convertCase6() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("a")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("a"),
                                createTerminator("b")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("a"),
                                createTerminator("b"),
                                createTerminator("c")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("a"),
                                createTerminator("b"),
                                createTerminator("c"),
                                createTerminator("d")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("b")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("b"),
                                createTerminator("c")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("b"),
                                createTerminator("c"),
                                createTerminator("d")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("c")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("c"),
                                createTerminator("d")
                        )
                ),
                Production.create(
                        createNonTerminator("A"),
                        PrimaryProduction.create(
                                createTerminator("d")
                        )
                )
        );

        Grammar convertedGrammar = LL1.GrammarConverter.convert(grammar);

        assertEquals(
                "{\"productions\":[\"(A)′′′′′′ → __EPSILON__ | d\",\"__START__ → A\",\"A → c (A)′′′ | b (A)′′ | a (A)′ | d\",\"(A)′ → b (A)′′′′′ | __EPSILON__\",\"(A)′′ → c (A)′′′′ | __EPSILON__\",\"(A)′′′ → __EPSILON__ | d\",\"(A)′′′′ → __EPSILON__ | d\",\"(A)′′′′′ → c (A)′′′′′′ | __EPSILON__\"]}",
                convertedGrammar.toReadableJSONString()
        );
    }
}
