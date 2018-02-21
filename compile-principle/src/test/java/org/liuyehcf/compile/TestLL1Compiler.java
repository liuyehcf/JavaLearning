package org.liuyehcf.compile;

import org.junit.Test;
import org.liuyehcf.compile.core.MorphemeType;
import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.PrimaryProduction;
import org.liuyehcf.compile.definition.Production;
import org.liuyehcf.compile.definition.Symbol;

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
                                createNonTerminator("E′")
                        )
                ),
                Production.create(
                        createNonTerminator("E′"),
                        PrimaryProduction.create(
                                createTerminator("+"),
                                createNonTerminator("T"),
                                createNonTerminator("E′")
                        )
                ),
                Production.create(
                        createNonTerminator("E′"),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator("T"),
                        PrimaryProduction.create(
                                createNonTerminator("F"),
                                createNonTerminator("T′")
                        )
                ),
                Production.create(
                        createNonTerminator("T′"),
                        PrimaryProduction.create(
                                createTerminator("*"),
                                createNonTerminator("F"),
                                createNonTerminator("T′")
                        )
                ),
                Production.create(
                        createNonTerminator("T′"),
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
                "{\"productions\":[\"E′ → + T E′ | __EPSILON__ |\",\"T′ → * F T′ | __EPSILON__ |\",\"T → ( E ) T′ | id T′ |\",\"E → ( E ) T′ E′ | id T′ E′ |\",\"F → ( E ) | id |\",\"__START__ → E |\"]}",
                convertedGrammar.toReadableJSONString()
        );
        assertEquals(
                "{\"FIRST\":{\"terminator\":{\"__EPSILON__\":\"__EPSILON__\",\"(\":\"(\",\")\":\")\",\"*\":\"*\",\"+\":\"+\",\"id\":\"id\"},\"nonTerminator\":{\"E′\":\"__EPSILON__,+\",\"T′\":\"__EPSILON__,*\",\"T\":\"(,id\",\"E\":\"(,id\",\"F\":\"(,id\",\"__START__\":\"(,id\"}},\"FOLLOW\":{\"nonTerminator\":{\"E′\":\"),__DOLLAR__\",\"T′\":\"),+,__DOLLAR__\",\"T\":\"),+\",\"E\":\"),__DOLLAR__\",\"F\":\"),*,+\",\"__START__\":\"__DOLLAR__\"}},\"SELECT\":{\"E′\":{\"E′ → __EPSILON__\":\"),__DOLLAR__\",\"E′ → + T E′\":\"+\"},\"T′\":{\"T′ → __EPSILON__\":\"),+,__DOLLAR__\",\"T′ → * F T′\":\"*\"},\"T\":{\"T → ( E ) T′\":\"(\",\"T → id T′\":\"id\"},\"E\":{\"E → ( E ) T′ E′\":\"(\",\"E → id T′ E′\":\"id\"},\"F\":{\"F → ( E )\":\"(\",\"F → id\":\"id\"},\"__START__\":{\"__START__ → E\":\"(,id\"}}}",
                compiler.toReadableJSONString()
        );
        assertEquals(
                "| 非终结符\\终结符 | __EPSILON__ | ( | ) | * | + | id |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| E′ | \\ | \\ | E′ → __EPSILON__ | \\ | E′ → + T E′ | \\ |\n" +
                        "| T′ | \\ | \\ | T′ → __EPSILON__ | T′ → * F T′ | T′ → __EPSILON__ | \\ |\n" +
                        "| T | \\ | T → ( E ) T′ | \\ | \\ | \\ | T → id T′ |\n" +
                        "| E | \\ | E → ( E ) T′ E′ | \\ | \\ | \\ | E → id T′ E′ |\n" +
                        "| F | \\ | F → ( E ) | \\ | \\ | \\ | F → id |\n" +
                        "| __START__ | \\ | __START__ → E | \\ | \\ | \\ | __START__ → E |\n",
                compiler.toMarkDownAnalysisTable()
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
                "{\"productions\":[\"STLIST → s STLISTN |\",\"PROGRAM → program DECLIST : TYPE ; STLIST end |\",\"DECLISTN → , id DECLISTN | __EPSILON__ |\",\"TYPE → real | int |\",\"STLISTN → ; s STLISTN | __EPSILON__ |\",\"DECLIST → id DECLISTN |\",\"__START__ → PROGRAM |\"]}",
                convertedGrammar.toReadableJSONString()
        );
        assertEquals(
                "{\"FIRST\":{\"terminator\":{\"s\":\"s\",\"__EPSILON__\":\"__EPSILON__\",\"real\":\"real\",\":\":\":\",\";\":\";\",\"id\":\"id\",\"end\":\"end\",\",\":\",\",\"program\":\"program\",\"int\":\"int\"},\"nonTerminator\":{\"STLIST\":\"s\",\"PROGRAM\":\"program\",\"DECLISTN\":\"__EPSILON__,,\",\"TYPE\":\"real,int\",\"STLISTN\":\"__EPSILON__,;\",\"DECLIST\":\"id\",\"__START__\":\"program\"}},\"FOLLOW\":{\"nonTerminator\":{\"STLIST\":\"end\",\"PROGRAM\":\"__DOLLAR__\",\"DECLISTN\":\":\",\"TYPE\":\";\",\"STLISTN\":\"end\",\"DECLIST\":\":\",\"__START__\":\"__DOLLAR__\"}},\"SELECT\":{\"STLIST\":{\"STLIST → s STLISTN\":\"s\"},\"PROGRAM\":{\"PROGRAM → program DECLIST : TYPE ; STLIST end\":\"program\"},\"DECLISTN\":{\"DECLISTN → __EPSILON__\":\":\",\"DECLISTN → , id DECLISTN\":\",\"},\"TYPE\":{\"TYPE → real\":\"real\",\"TYPE → int\":\"int\"},\"STLISTN\":{\"STLISTN → __EPSILON__\":\"end\",\"STLISTN → ; s STLISTN\":\";\"},\"DECLIST\":{\"DECLIST → id DECLISTN\":\"id\"},\"__START__\":{\"__START__ → PROGRAM\":\"program\"}}}",
                compiler.toReadableJSONString()
        );
        assertEquals(
                "| 非终结符\\终结符 | s | __EPSILON__ | real | : | ; | id | end | , | program | int |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| STLIST | STLIST → s STLISTN | \\ | \\ | \\ | \\ | \\ | \\ | \\ | \\ | \\ |\n" +
                        "| PROGRAM | \\ | \\ | \\ | \\ | \\ | \\ | \\ | \\ | PROGRAM → program DECLIST : TYPE ; STLIST end | \\ |\n" +
                        "| DECLISTN | \\ | \\ | \\ | DECLISTN → __EPSILON__ | \\ | \\ | \\ | DECLISTN → , id DECLISTN | \\ | \\ |\n" +
                        "| TYPE | \\ | \\ | TYPE → real | \\ | \\ | \\ | \\ | \\ | \\ | TYPE → int |\n" +
                        "| STLISTN | \\ | \\ | \\ | \\ | STLISTN → ; s STLISTN | \\ | STLISTN → __EPSILON__ | \\ | \\ | \\ |\n" +
                        "| DECLIST | \\ | \\ | \\ | \\ | \\ | DECLIST → id DECLISTN | \\ | \\ | \\ | \\ |\n" +
                        "| __START__ | \\ | \\ | \\ | \\ | \\ | \\ | \\ | \\ | __START__ → PROGRAM | \\ |\n",
                compiler.toMarkDownAnalysisTable()
        );
    }

    @Test
    public void testParseCase1() {
        Grammar grammar = Grammar.create(
                Production.create(
                        createNonTerminator("E"),
                        PrimaryProduction.create(
                                createNonTerminator("T"),
                                createNonTerminator("E′")
                        )
                ),
                Production.create(
                        createNonTerminator("E′"),
                        PrimaryProduction.create(
                                createTerminator("+"),
                                createNonTerminator("T"),
                                createNonTerminator("E′")
                        )
                ),
                Production.create(
                        createNonTerminator("E′"),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator("T"),
                        PrimaryProduction.create(
                                createNonTerminator("F"),
                                createNonTerminator("T′")
                        )
                ),
                Production.create(
                        createNonTerminator("T′"),
                        PrimaryProduction.create(
                                createTerminator("*"),
                                createNonTerminator("F"),
                                createNonTerminator("T′")
                        )
                ),
                Production.create(
                        createNonTerminator("T′"),
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
                                createNonTerminator("E′")
                        )
                ),
                Production.create(
                        createNonTerminator("E′"),
                        PrimaryProduction.create(
                                createTerminator("+"),
                                createNonTerminator("T"),
                                createNonTerminator("E′")
                        )
                ),
                Production.create(
                        createNonTerminator("E′"),
                        PrimaryProduction.create(
                                Symbol.EPSILON
                        )
                ),
                Production.create(
                        createNonTerminator("T"),
                        PrimaryProduction.create(
                                createNonTerminator("F"),
                                createNonTerminator("T′")
                        )
                ),
                Production.create(
                        createNonTerminator("T′"),
                        PrimaryProduction.create(
                                createTerminator("*"),
                                createNonTerminator("F"),
                                createNonTerminator("T′")
                        )
                ),
                Production.create(
                        createNonTerminator("T′"),
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
    }

    private LexicalAnalyzer getDefaultLexicalAnalyzer() {
        return LexicalAnalyzer.builder()
                .addMorpheme("NULL")
                .build();
    }
}
