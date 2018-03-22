package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.JdkLexicalAnalyzer;
import org.liuyehcf.grammar.LexicalAnalyzer;

import static org.junit.Assert.*;

public class TestLR1 {
    @Test
    public void testCase1() {
        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("*")
                .addMorpheme("id")
                .addMorpheme("=")
                .build();

        LRParser parser = LR1.create(analyzer, GrammarCase.GRAMMAR_CASE_1);

        assertFalse(parser.isLegal());

        assertEquals(
                "{\"nonTerminator\":{\"__START__\":\"__DOLLAR__\",\"E\":\"),*,+,__DOLLAR__\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"1\":\"[0, (] → 2\",\"2\":\"[0, id] → 3\",\"3\":\"[0, E] → 1\",\"4\":\"[1, *] → 5\",\"5\":\"[1, +] → 4\",\"6\":\"[2, (] → 7\",\"7\":\"[2, id] → 8\",\"8\":\"[2, E] → 6\",\"9\":\"[4, (] → 2\",\"10\":\"[4, id] → 3\",\"11\":\"[4, E] → 9\",\"12\":\"[5, (] → 2\",\"13\":\"[5, id] → 3\",\"14\":\"[5, E] → 10\",\"15\":\"[6, )] → 11\",\"16\":\"[6, *] → 13\",\"17\":\"[6, +] → 12\",\"18\":\"[7, (] → 7\",\"19\":\"[7, id] → 8\",\"20\":\"[7, E] → 14\",\"21\":\"[9, *] → 5\",\"22\":\"[9, +] → 4\",\"23\":\"[10, *] → 5\",\"24\":\"[10, +] → 4\",\"25\":\"[12, (] → 7\",\"26\":\"[12, id] → 8\",\"27\":\"[12, E] → 15\",\"28\":\"[13, (] → 7\",\"29\":\"[13, id] → 8\",\"30\":\"[13, E] → 16\",\"31\":\"[14, )] → 17\",\"32\":\"[14, *] → 13\",\"33\":\"[14, +] → 12\",\"34\":\"[15, *] → 13\",\"35\":\"[15, +] → 12\",\"36\":\"[16, *] → 13\",\"37\":\"[16, +] → 12\"}",
                parser.getClosureTransferTableJSONString()
        );

        assertEquals(
                "{\"0\":[\"__START__ → · E, [__DOLLAR__]\",\"E → · E + E, [*, +, __DOLLAR__]\",\"E → · E * E, [*, +, __DOLLAR__]\",\"E → · ( E ), [*, +, __DOLLAR__]\",\"E → · id, [*, +, __DOLLAR__]\"],\"1\":[\"__START__ → E ·, [__DOLLAR__]\",\"E → E · + E, [*, +, __DOLLAR__]\",\"E → E · * E, [*, +, __DOLLAR__]\"],\"2\":[\"E → ( · E ), [*, +, __DOLLAR__]\",\"E → · E + E, [), *, +]\",\"E → · E * E, [), *, +]\",\"E → · ( E ), [), *, +]\",\"E → · id, [), *, +]\"],\"3\":[\"E → id ·, [*, +, __DOLLAR__]\"],\"4\":[\"E → E + · E, [*, +, __DOLLAR__]\",\"E → · E + E, [*, +, __DOLLAR__]\",\"E → · E * E, [*, +, __DOLLAR__]\",\"E → · ( E ), [*, +, __DOLLAR__]\",\"E → · id, [*, +, __DOLLAR__]\"],\"5\":[\"E → E * · E, [*, +, __DOLLAR__]\",\"E → · E + E, [*, +, __DOLLAR__]\",\"E → · E * E, [*, +, __DOLLAR__]\",\"E → · ( E ), [*, +, __DOLLAR__]\",\"E → · id, [*, +, __DOLLAR__]\"],\"6\":[\"E → ( E · ), [*, +, __DOLLAR__]\",\"E → E · + E, [), *, +]\",\"E → E · * E, [), *, +]\"],\"7\":[\"E → ( · E ), [), *, +]\",\"E → · E + E, [), *, +]\",\"E → · E * E, [), *, +]\",\"E → · ( E ), [), *, +]\",\"E → · id, [), *, +]\"],\"8\":[\"E → id ·, [), *, +]\"],\"9\":[\"E → E + E ·, [*, +, __DOLLAR__]\",\"E → E · + E, [*, +, __DOLLAR__]\",\"E → E · * E, [*, +, __DOLLAR__]\"],\"10\":[\"E → E * E ·, [*, +, __DOLLAR__]\",\"E → E · + E, [*, +, __DOLLAR__]\",\"E → E · * E, [*, +, __DOLLAR__]\"],\"11\":[\"E → ( E ) ·, [*, +, __DOLLAR__]\"],\"12\":[\"E → E + · E, [), *, +]\",\"E → · E + E, [), *, +]\",\"E → · E * E, [), *, +]\",\"E → · ( E ), [), *, +]\",\"E → · id, [), *, +]\"],\"13\":[\"E → E * · E, [), *, +]\",\"E → · E + E, [), *, +]\",\"E → · E * E, [), *, +]\",\"E → · ( E ), [), *, +]\",\"E → · id, [), *, +]\"],\"14\":[\"E → ( E · ), [), *, +]\",\"E → E · + E, [), *, +]\",\"E → E · * E, [), *, +]\"],\"15\":[\"E → E + E ·, [), *, +]\",\"E → E · + E, [), *, +]\",\"E → E · * E, [), *, +]\"],\"16\":[\"E → E * E ·, [), *, +]\",\"E → E · + E, [), *, +]\",\"E → E · * E, [), *, +]\"],\"17\":[\"E → ( E ) ·, [), *, +]\"]}",
                parser.getClosureJSONString()
        );

        assertEquals(
                "| 状态\\文法符号 | ( | ) | * | + | id | __DOLLAR__ | E |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | MOVE_IN \"2\" | \\ | \\ | \\ | MOVE_IN \"3\" | \\ | JUMP \"1\" |\n" +
                        "| 1 | \\ | \\ | MOVE_IN \"5\" | MOVE_IN \"4\" | \\ | ACCEPT \"__START__ → E\" | \\ |\n" +
                        "| 2 | MOVE_IN \"7\" | \\ | \\ | \\ | MOVE_IN \"8\" | \\ | JUMP \"6\" |\n" +
                        "| 3 | \\ | \\ | REDUCTION \"E → id\" | REDUCTION \"E → id\" | \\ | REDUCTION \"E → id\" | \\ |\n" +
                        "| 4 | MOVE_IN \"2\" | \\ | \\ | \\ | MOVE_IN \"3\" | \\ | JUMP \"9\" |\n" +
                        "| 5 | MOVE_IN \"2\" | \\ | \\ | \\ | MOVE_IN \"3\" | \\ | JUMP \"10\" |\n" +
                        "| 6 | \\ | MOVE_IN \"11\" | MOVE_IN \"13\" | MOVE_IN \"12\" | \\ | \\ | \\ |\n" +
                        "| 7 | MOVE_IN \"7\" | \\ | \\ | \\ | MOVE_IN \"8\" | \\ | JUMP \"14\" |\n" +
                        "| 8 | \\ | REDUCTION \"E → id\" | REDUCTION \"E → id\" | REDUCTION \"E → id\" | \\ | \\ | \\ |\n" +
                        "| 9 | \\ | \\ | REDUCTION \"E → E + E\" / MOVE_IN \"5\" | REDUCTION \"E → E + E\" / MOVE_IN \"4\" | \\ | REDUCTION \"E → E + E\" | \\ |\n" +
                        "| 10 | \\ | \\ | REDUCTION \"E → E * E\" / MOVE_IN \"5\" | REDUCTION \"E → E * E\" / MOVE_IN \"4\" | \\ | REDUCTION \"E → E * E\" | \\ |\n" +
                        "| 11 | \\ | \\ | REDUCTION \"E → ( E )\" | REDUCTION \"E → ( E )\" | \\ | REDUCTION \"E → ( E )\" | \\ |\n" +
                        "| 12 | MOVE_IN \"7\" | \\ | \\ | \\ | MOVE_IN \"8\" | \\ | JUMP \"15\" |\n" +
                        "| 13 | MOVE_IN \"7\" | \\ | \\ | \\ | MOVE_IN \"8\" | \\ | JUMP \"16\" |\n" +
                        "| 14 | \\ | MOVE_IN \"17\" | MOVE_IN \"13\" | MOVE_IN \"12\" | \\ | \\ | \\ |\n" +
                        "| 15 | \\ | REDUCTION \"E → E + E\" | REDUCTION \"E → E + E\" / MOVE_IN \"13\" | REDUCTION \"E → E + E\" / MOVE_IN \"12\" | \\ | \\ | \\ |\n" +
                        "| 16 | \\ | REDUCTION \"E → E * E\" | REDUCTION \"E → E * E\" / MOVE_IN \"13\" | REDUCTION \"E → E * E\" / MOVE_IN \"12\" | \\ | \\ | \\ |\n" +
                        "| 17 | \\ | REDUCTION \"E → ( E )\" | REDUCTION \"E → ( E )\" | REDUCTION \"E → ( E )\" | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );
    }


    @Test
    public void testCase2() {
        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("*")
                .addMorpheme("id")
                .addMorpheme("=")
                .build();

        LRParser parser = LR1.create(analyzer, GrammarCase.GRAMMAR_CASE_13);

        assertTrue(parser.isLegal());

        assertEquals(
                "{\"nonTerminator\":{\"L\":\"__DOLLAR__,=\",\"__START__\":\"__DOLLAR__\",\"R\":\"__DOLLAR__,=\",\"S\":\"__DOLLAR__\"}}",
                parser.getFollowJSONString()
        );

        assertEquals(
                "{\"1\":\"[0, *] → 4\",\"2\":\"[0, id] → 5\",\"3\":\"[0, L] → 2\",\"4\":\"[0, R] → 3\",\"5\":\"[0, S] → 1\",\"6\":\"[2, =] → 6\",\"7\":\"[4, *] → 4\",\"8\":\"[4, id] → 5\",\"9\":\"[4, L] → 8\",\"10\":\"[4, R] → 7\",\"11\":\"[6, *] → 11\",\"12\":\"[6, id] → 12\",\"13\":\"[6, L] → 10\",\"14\":\"[6, R] → 9\",\"15\":\"[11, *] → 11\",\"16\":\"[11, id] → 12\",\"17\":\"[11, L] → 10\",\"18\":\"[11, R] → 13\"}",
                parser.getClosureTransferTableJSONString()
        );

        assertEquals(
                "{\"0\":[\"__START__ → · S, [__DOLLAR__]\",\"S → · L = R, [__DOLLAR__]\",\"S → · R, [__DOLLAR__]\",\"L → · * R, [__DOLLAR__, =]\",\"L → · id, [__DOLLAR__, =]\",\"R → · L, [__DOLLAR__]\"],\"1\":[\"__START__ → S ·, [__DOLLAR__]\"],\"2\":[\"S → L · = R, [__DOLLAR__]\",\"R → L ·, [__DOLLAR__]\"],\"3\":[\"S → R ·, [__DOLLAR__]\"],\"4\":[\"L → * · R, [__DOLLAR__, =]\",\"R → · L, [__DOLLAR__, =]\",\"L → · * R, [__DOLLAR__, =]\",\"L → · id, [__DOLLAR__, =]\"],\"5\":[\"L → id ·, [__DOLLAR__, =]\"],\"6\":[\"S → L = · R, [__DOLLAR__]\",\"R → · L, [__DOLLAR__]\",\"L → · * R, [__DOLLAR__]\",\"L → · id, [__DOLLAR__]\"],\"7\":[\"L → * R ·, [__DOLLAR__, =]\"],\"8\":[\"R → L ·, [__DOLLAR__, =]\"],\"9\":[\"S → L = R ·, [__DOLLAR__]\"],\"10\":[\"R → L ·, [__DOLLAR__]\"],\"11\":[\"L → * · R, [__DOLLAR__]\",\"R → · L, [__DOLLAR__]\",\"L → · * R, [__DOLLAR__]\",\"L → · id, [__DOLLAR__]\"],\"12\":[\"L → id ·, [__DOLLAR__]\"],\"13\":[\"L → * R ·, [__DOLLAR__]\"]}",
                parser.getClosureJSONString()
        );

        assertEquals(
                "| 状态\\文法符号 | * | id | = | __DOLLAR__ | L | R | S |\n" +
                        "|:--|:--|:--|:--|:--|:--|:--|:--|\n" +
                        "| 0 | MOVE_IN \"4\" | MOVE_IN \"5\" | \\ | \\ | JUMP \"2\" | JUMP \"3\" | JUMP \"1\" |\n" +
                        "| 1 | \\ | \\ | \\ | ACCEPT \"__START__ → S\" | \\ | \\ | \\ |\n" +
                        "| 2 | \\ | \\ | MOVE_IN \"6\" | REDUCTION \"R → L\" | \\ | \\ | \\ |\n" +
                        "| 3 | \\ | \\ | \\ | REDUCTION \"S → R\" | \\ | \\ | \\ |\n" +
                        "| 4 | MOVE_IN \"4\" | MOVE_IN \"5\" | \\ | \\ | JUMP \"8\" | JUMP \"7\" | \\ |\n" +
                        "| 5 | \\ | \\ | REDUCTION \"L → id\" | REDUCTION \"L → id\" | \\ | \\ | \\ |\n" +
                        "| 6 | MOVE_IN \"11\" | MOVE_IN \"12\" | \\ | \\ | JUMP \"10\" | JUMP \"9\" | \\ |\n" +
                        "| 7 | \\ | \\ | REDUCTION \"L → * R\" | REDUCTION \"L → * R\" | \\ | \\ | \\ |\n" +
                        "| 8 | \\ | \\ | REDUCTION \"R → L\" | REDUCTION \"R → L\" | \\ | \\ | \\ |\n" +
                        "| 9 | \\ | \\ | \\ | REDUCTION \"S → L = R\" | \\ | \\ | \\ |\n" +
                        "| 10 | \\ | \\ | \\ | REDUCTION \"R → L\" | \\ | \\ | \\ |\n" +
                        "| 11 | MOVE_IN \"11\" | MOVE_IN \"12\" | \\ | \\ | JUMP \"10\" | JUMP \"13\" | \\ |\n" +
                        "| 12 | \\ | \\ | \\ | REDUCTION \"L → id\" | \\ | \\ | \\ |\n" +
                        "| 13 | \\ | \\ | \\ | REDUCTION \"L → * R\" | \\ | \\ | \\ |\n",
                parser.getAnalysisTableMarkdownString()
        );

        assertTrue(parser.matches("id=id"));
        assertTrue(parser.matches("id=*id"));
        assertTrue(parser.matches("id=**id"));

        assertTrue(parser.matches("*id=id"));
        assertTrue(parser.matches("*id=*id"));
        assertTrue(parser.matches("*id=**id"));

        assertTrue(parser.matches("**id=id"));
        assertTrue(parser.matches("**id=*id"));
        assertTrue(parser.matches("**id=**id"));
    }
}
