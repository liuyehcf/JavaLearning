package org.liuyehcf.compile;

import org.junit.Test;
import org.liuyehcf.compile.core.MorphemeType;
import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.Production;
import org.liuyehcf.compile.definition.Symbol;
import org.liuyehcf.compile.definition.PrimaryProduction;

import static org.junit.Assert.*;
import static org.liuyehcf.compile.TestLexicalAnalyzer.getIdRegex;
import static org.liuyehcf.compile.definition.Symbol.createNonTerminator;
import static org.liuyehcf.compile.definition.Symbol.createTerminator;

public class TestLL1Compiler {
    @Test
    public void testFirstFollowSelectCase1() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("E"),
                        PrimaryProduction.create(
                                createNonTerminator("T"),
                                createNonTerminator("E^")
                        )
                ),
                Production.create(
                        createNonTerminator("E^"),
                        PrimaryProduction.create(
                                createTerminator("+"),
                                createNonTerminator("T"),
                                createNonTerminator("E^")
                        )
                ),
                Production.create(
                        createNonTerminator("E^"),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator("T"),
                        PrimaryProduction.create(
                                createNonTerminator("F"),
                                createNonTerminator("T^")
                        )
                ),
                Production.create(
                        createNonTerminator("T^"),
                        PrimaryProduction.create(
                                createTerminator("*"),
                                createNonTerminator("F"),
                                createNonTerminator("T^")
                        )
                ),
                Production.create(
                        createNonTerminator("T^"),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator("F"),
                        PrimaryProduction.create(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                Production.create(
                        createNonTerminator("F"),
                        PrimaryProduction.create(
                                createTerminator("id")
                        )
                )
        );

        LL1Compiler compiler = new LL1Compiler(grammar, getDefaultLexicalAnalyzer());
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"T → (E)T^|idT^\",\"E → (E)T^E^|idT^E^\",\"F → (E)|id\",\"__START__ → E\",\"E^ → +TE^|__EPSILON__\",\"T^ → *FT^|__EPSILON__\"]}",
                convertedGrammar.toReadableJSONString()
        );
        assertEquals(
                "{\"FIRST\":{\"terminator\":{\"__EPSILON__\":\"__EPSILON__\",\"(\":\"(\",\")\":\")\",\"*\":\"*\",\"id\":\"id\",\"+\":\"+\"},\"nonTerminator\":{\"T\":\"(,id\",\"E\":\"(,id\",\"F\":\"(,id\",\"__START__\":\"(,id\",\"E^\":\"__EPSILON__,+\",\"T^\":\"__EPSILON__,*\"}},\"FOLLOW\":{\"nonTerminator\":{\"T\":\"),+,__DOLLAR__\",\"E\":\"),__DOLLAR__\",\"F\":\"),*,+,__DOLLAR__\",\"__START__\":\"__DOLLAR__\",\"E^\":\"),__DOLLAR__\",\"T^\":\"),+,__DOLLAR__\"}},\"SELECT\":{\"T\":{\"T → (E)T^\":\"(\",\"T → idT^\":\"id\"},\"E\":{\"E → (E)T^E^\":\"(\",\"E → idT^E^\":\"id\"},\"F\":{\"F → (E)\":\"(\",\"F → id\":\"id\"},\"__START__\":{\"__START__ → E\":\"(,id\"},\"E^\":{\"E^ → +TE^\":\"+\",\"E^ → __EPSILON__\":\"),__DOLLAR__\"},\"T^\":{\"T^ → *FT^\":\"*\",\"T^ → __EPSILON__\":\"),+,__DOLLAR__\"}}}",
                compiler.toReadableJSONString()
        );
    }

    @Test
    public void testFirstFollowSelectCase2() {
        String PROGRAM = "PROGRAM";
        String DECLIST = "DECLIST";
        String DECLISTN = "DECLISTN";
        String STLIST = "STLIST";
        String STLISTN = "STLISTN";
        String TYPE = "TYPE";


        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator(PROGRAM),
                        PrimaryProduction.create(
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
                        PrimaryProduction.create(
                                createTerminator("id"),
                                createNonTerminator(DECLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(DECLISTN),
                        PrimaryProduction.create(
                                createTerminator(","),
                                createTerminator("id"),
                                createNonTerminator(DECLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(DECLISTN),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator(STLIST),
                        PrimaryProduction.create(
                                createTerminator("s"),
                                createNonTerminator(STLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(STLISTN),
                        PrimaryProduction.create(
                                createTerminator(";"),
                                createTerminator("s"),
                                createNonTerminator(STLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(STLISTN),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator(TYPE),
                        PrimaryProduction.create(
                                createTerminator("real")
                        )
                ),
                Production.create(
                        createNonTerminator(TYPE),
                        PrimaryProduction.create(
                                createTerminator("int")
                        )
                )
        );


        LL1Compiler compiler = new LL1Compiler(grammar, getDefaultLexicalAnalyzer());
        Grammar convertedGrammar = compiler.getGrammar();

        assertEquals(
                "{\"productions\":[\"STLIST → sSTLISTN\",\"PROGRAM → programDECLIST:TYPE;STLISTend\",\"DECLISTN → ,idDECLISTN|__EPSILON__\",\"TYPE → real|int\",\"STLISTN → ;sSTLISTN|__EPSILON__\",\"DECLIST → idDECLISTN\",\"__START__ → PROGRAM\"]}",
                convertedGrammar.toReadableJSONString()
        );
        assertEquals(
                "{\"FIRST\":{\"terminator\":{\"s\":\"s\",\"__EPSILON__\":\"__EPSILON__\",\"real\":\"real\",\":\":\":\",\";\":\";\",\"id\":\"id\",\"end\":\"end\",\",\":\",\",\"program\":\"program\",\"int\":\"int\"},\"nonTerminator\":{\"STLIST\":\"s\",\"PROGRAM\":\"program\",\"DECLISTN\":\"__EPSILON__,,\",\"TYPE\":\"real,int\",\"STLISTN\":\"__EPSILON__,;\",\"DECLIST\":\"id\",\"__START__\":\"program\"}},\"FOLLOW\":{\"nonTerminator\":{\"STLIST\":\"end\",\"PROGRAM\":\"__DOLLAR__\",\"DECLISTN\":\":\",\"TYPE\":\";\",\"STLISTN\":\"end\",\"DECLIST\":\":\",\"__START__\":\"__DOLLAR__\"}},\"SELECT\":{\"STLIST\":{\"STLIST → sSTLISTN\":\"s\"},\"PROGRAM\":{\"PROGRAM → programDECLIST:TYPE;STLISTend\":\"program\"},\"DECLISTN\":{\"DECLISTN → __EPSILON__\":\":\",\"DECLISTN → ,idDECLISTN\":\",\"},\"TYPE\":{\"TYPE → real\":\"real\",\"TYPE → int\":\"int\"},\"STLISTN\":{\"STLISTN → __EPSILON__\":\"end\",\"STLISTN → ;sSTLISTN\":\";\"},\"DECLIST\":{\"DECLIST → idDECLISTN\":\"id\"},\"__START__\":{\"__START__ → PROGRAM\":\"program\"}}}",
                compiler.toReadableJSONString()
        );
    }

    @Test
    public void testParseCase1() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("E"),
                        PrimaryProduction.create(
                                createNonTerminator("T"),
                                createNonTerminator("E^")
                        )
                ),
                Production.create(
                        createNonTerminator("E^"),
                        PrimaryProduction.create(
                                createTerminator("+"),
                                createNonTerminator("T"),
                                createNonTerminator("E^")
                        )
                ),
                Production.create(
                        createNonTerminator("E^"),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator("T"),
                        PrimaryProduction.create(
                                createNonTerminator("F"),
                                createNonTerminator("T^")
                        )
                ),
                Production.create(
                        createNonTerminator("T^"),
                        PrimaryProduction.create(
                                createTerminator("*"),
                                createNonTerminator("F"),
                                createNonTerminator("T^")
                        )
                ),
                Production.create(
                        createNonTerminator("T^"),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator("F"),
                        PrimaryProduction.create(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                Production.create(
                        createNonTerminator("F"),
                        PrimaryProduction.create(
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
                        PrimaryProduction.create(
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
                        PrimaryProduction.create(
                                createTerminator("id"),
                                createNonTerminator(DECLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(DECLISTN),
                        PrimaryProduction.create(
                                createTerminator(","),
                                createTerminator("id"),
                                createNonTerminator(DECLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(DECLISTN),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator(STLIST),
                        PrimaryProduction.create(
                                createTerminator("s"),
                                createNonTerminator(STLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(STLISTN),
                        PrimaryProduction.create(
                                createTerminator(";"),
                                createTerminator("s"),
                                createNonTerminator(STLISTN)
                        )
                ),
                Production.create(
                        createNonTerminator(STLISTN),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator(TYPE),
                        PrimaryProduction.create(
                                createTerminator("real")
                        )
                ),
                Production.create(
                        createNonTerminator(TYPE),
                        PrimaryProduction.create(
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

        assertTrue(compiler.isSentence("program id, id, id: real; s; s end"));
        assertTrue(compiler.isSentence("program id: int; s; s end"));
        assertTrue(compiler.isSentence("program id, id: int; s end"));

        assertFalse(compiler.isSentence(" id, id, id: real; s; s end"));
        assertFalse(compiler.isSentence("program : real; s; s end"));
        assertFalse(compiler.isSentence("program id, id, id: double; s; s end"));
        assertFalse(compiler.isSentence("program id, id, id: real; s; s"));
        assertFalse(compiler.isSentence("program id, id, id: real; s, s end"));

    }

    @Test
    public void testParseCase3() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("E"),
                        PrimaryProduction.create(
                                createNonTerminator("T"),
                                createNonTerminator("E^")
                        )
                ),
                Production.create(
                        createNonTerminator("E^"),
                        PrimaryProduction.create(
                                createTerminator("+"),
                                createNonTerminator("T"),
                                createNonTerminator("E^")
                        )
                ),
                Production.create(
                        createNonTerminator("E^"),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator("T"),
                        PrimaryProduction.create(
                                createNonTerminator("F"),
                                createNonTerminator("T^")
                        )
                ),
                Production.create(
                        createNonTerminator("T^"),
                        PrimaryProduction.create(
                                createTerminator("*"),
                                createNonTerminator("F"),
                                createNonTerminator("T^")
                        )
                ),
                Production.create(
                        createNonTerminator("T^"),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator("F"),
                        PrimaryProduction.create(
                                createTerminator("("),
                                createNonTerminator("E"),
                                createTerminator(")")
                        )
                ),
                Production.create(
                        createNonTerminator("F"),
                        PrimaryProduction.create(
                                createTerminator("id")
                        )
                )
        );

        LexicalAnalyzer analyzer = LexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .build();


        LL1Compiler compiler = new LL1Compiler(grammar, analyzer);

        assertTrue(compiler.isSentence("A12+B*D"));
        assertTrue(compiler.isSentence("(a+b01)*d03"));
        assertTrue(compiler.isSentence("(asdfsdfDASDF323+ASDFC0102D*d23234+(asdf+dd)*(d1d*k9))"));
        assertFalse(compiler.isSentence("000+(id*id)"));
        assertFalse(compiler.isSentence("()"));

        System.out.println(compiler.toMarkDownAnalysisTable());
    }

    private LexicalAnalyzer getDefaultLexicalAnalyzer() {
        return LexicalAnalyzer.builder()
                .addMorpheme("NULL")
                .build();
    }
}
