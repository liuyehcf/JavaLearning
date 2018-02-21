package org.liuyehcf.compile;

import org.junit.Test;
import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.Production;
import org.liuyehcf.compile.definition.Symbol;
import org.liuyehcf.compile.definition.SymbolSequence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.liuyehcf.compile.definition.Symbol.createNonTerminator;
import static org.liuyehcf.compile.definition.Symbol.createTerminator;

public class TestLL1Compiler {
    @Test
    public void testFirstFollowSelectCase1() {
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

        LL1Compiler compiler = new LL1Compiler(grammar, getDefaultLexicalAnalyzer());
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"T → (E)T^|idT^\",\"E → (E)T^E^|idT^E^\",\"F → (E)|id\",\"__START__ → (E)T^E^|idT^E^\",\"E^ → +TE^|__EPSILON__\",\"T^ → *FT^|__EPSILON__\"]}",
                convertedGrammar.toReadableJSONString()
        );
        assertEquals(
                "{\"FIRST\":{\"terminator\":{\"__EPSILON__\":\"__EPSILON__\",\"(\":\"(\",\")\":\")\",\"*\":\"*\",\"id\":\"id\",\"+\":\"+\"},\"nonTerminator\":{\"T\":\"(,id\",\"E\":\"(,id\",\"F\":\"(,id\",\"__START__\":\"(,id\",\"E^\":\"__EPSILON__,+\",\"T^\":\"__EPSILON__,*\"}},\"FOLLOW\":{\"nonTerminator\":{\"T\":\"),+,__DOLLAR__\",\"E\":\")\",\"F\":\"),*,+,__DOLLAR__\",\"__START__\":\"__DOLLAR__\",\"E^\":\"),__DOLLAR__\",\"T^\":\"),+,__DOLLAR__\"}},\"SELECT\":{\"T\":{\"T → (E)T^\":\"(\",\"T → idT^\":\"id\"},\"E\":{\"E → (E)T^E^\":\"(\",\"E → idT^E^\":\"id\"},\"F\":{\"F → (E)\":\"(\",\"F → id\":\"id\"},\"__START__\":{\"__START__ → (E)T^E^\":\"(\",\"__START__ → idT^E^\":\"id\"},\"E^\":{\"E^ → +TE^\":\"+\",\"E^ → __EPSILON__\":\"),__DOLLAR__\"},\"T^\":{\"T^ → *FT^\":\"*\",\"T^ → __EPSILON__\":\"),+,__DOLLAR__\"}}}",
                compiler.toReadableJSONString()
        );
    }

    @Test
    public void testParseCase1() {
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

        LexicalAnalyzer analyzer = LexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id")
                .build();


        Compiler compiler = new LL1Compiler(grammar, analyzer);

        assertTrue(compiler.isSentence("id+id*id"));
        assertTrue(compiler.isSentence("(id+id)*id"));
        assertTrue(compiler.isSentence("id+(id*id)"));
        assertTrue(compiler.isSentence("(id)+(id*id)"));
    }


    @Test
    public void testParseCase2() {
        String PROGRAM = "PROGRAM";
        String DECLIST = "DECLIST";
        String DECLISTN = "DECLISTN";
        String STLIST = "STLIST";
        String STLISTN = "STLISTN";
        String TYPE = "TYPE";


        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator(PROGRAM),
                        SymbolSequence.create(
                                createTerminator("program"),
                                createNonTerminator(DECLIST),
                                createTerminator(":"),
                                createNonTerminator(TYPE),
                                createTerminator(";"),
                                createNonTerminator(STLIST),
                                createTerminator("end")
                        )
                ),
                Production.create(
                        createNonTerminator(DECLIST),
                        SymbolSequence.create(
                                createTerminator("id"),
                                createNonTerminator(DECLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(DECLISTN),
                        SymbolSequence.create(
                                createTerminator(","),
                                createTerminator("id"),
                                createNonTerminator(DECLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(DECLISTN),
                        SymbolSequence.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator(STLIST),
                        SymbolSequence.create(
                                createTerminator("s"),
                                createNonTerminator(STLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(STLISTN),
                        SymbolSequence.create(
                                createTerminator(";"),
                                createTerminator("s"),
                                createNonTerminator(STLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(STLISTN),
                        SymbolSequence.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator(TYPE),
                        SymbolSequence.create(
                                createTerminator("real")
                        )
                ),
                Production.create(
                        createNonTerminator(TYPE),
                        SymbolSequence.create(
                                createTerminator("int")
                        )
                )
        );


        LexicalAnalyzer analyzer = LexicalAnalyzer.builder()
                .addMorpheme("program")
                .addMorpheme(":")
                .addMorpheme(";")
                .addMorpheme("end")
                .addMorpheme("id")
                .addMorpheme(",")
                .addMorpheme("s")
                .addMorpheme("real")
                .addMorpheme("int")
                .build();
        LL1Compiler compiler = new LL1Compiler(grammar, analyzer);

        //System.out.println(compiler.toReadableJSONString());

    }

    private LexicalAnalyzer getDefaultLexicalAnalyzer() {
        return LexicalAnalyzer.builder().build();
    }
}
