package org.liuyehcf.grammar.cfg.lr;

import org.junit.Test;
import org.liuyehcf.grammar.GrammarCase;
import org.liuyehcf.grammar.JdkLexicalAnalyzer;
import org.liuyehcf.grammar.LexicalAnalyzer;

public class TestSLR {
    @Test
    public void testGrammarConvertCase1() {
        LexicalAnalyzer analyzer = JdkLexicalAnalyzer.builder()
                .addMorpheme("(")
                .addMorpheme(")")
                .addMorpheme("*")
                .addMorpheme("+")
                .addMorpheme("id")
                .build();

        LRParser parser = SLRParser.create(analyzer, GrammarCase.GRAMMAR_CASE_11);

    }
}
