package org.liuyehcf.compile;

import org.junit.Test;
import org.liuyehcf.compile.core.MorphemeType;

import static org.junit.Assert.assertEquals;

public class TestLexicalAnalyzer {
    static String getIdRegex() {
        String digit = "[0-9]";
        String letter = "[a-zA-Z_]";
        String id = letter + "(" + letter + "|" + digit + ")*";
        return id;
    }

    static String getUnsignedIntegerRegex() {
        return "[0-9]+";
    }

    @Test
    public void testLexicalAnalyze1() {
        LexicalAnalyzer analyzer = LexicalAnalyzer.builder()
                .addMorpheme("int", "int", MorphemeType.NORMAL)
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .addMorpheme("=")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("unsignedInteger", getUnsignedIntegerRegex(), MorphemeType.REGEX)
                .build();

        LexicalAnalyzer.TokenIterator iterator = analyzer.iterator("1+2*3=7");


        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            sb.append(iterator.next().getValue());
        }

        assertEquals(
                "1+2*3=7__DOLLAR__",
                sb.toString()
        );
    }

}
