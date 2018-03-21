package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.JdkLexicalAnalyzer;
import org.liuyehcf.grammar.LexicalAnalyzer;

import static org.junit.Assert.assertEquals;

public class TestLR1 {
    @Test
    public void testCase1() {
        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("*")
                .addMorpheme("id")
                .addMorpheme("=")
                .build();

        LRParser parser = LR1.create(analyzer, GrammarCase.GRAMMAR_CASE_13);

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
    }
}
