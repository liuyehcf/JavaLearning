package org.liuyehcf.compile;

import org.junit.Test;
import org.liuyehcf.compile.core.MorphemeType;

import java.util.Iterator;

public class TestLexicalAnalyzer {
    @Test
    public void testLexicalAnalyze1() {
        LexicalAnalyzer analyzer = LexicalAnalyzer.builder()
                .addMorpheme("int", "int", MorphemeType.KEY)
                .addMorpheme("=")
                .addMorpheme("+")
                .addMorpheme("*")
                .addMorpheme("id", getIdRegex(), MorphemeType.REGEX)
                .addMorpheme("unsignedInteger", getUnsignedIntegerRegex(), MorphemeType.REGEX)
                .build();

        Iterator iterator = analyzer.iterator("id +id*id = 0");

        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }


    private String getIdRegex() {
        String digit = "[0-9]";
        String letter = "[a-zA-Z_]";
        String id = letter + "(" + letter + "|" + digit + ")*";
        return id;
    }

    private String getUnsignedIntegerRegex() {
        return "[0-9]+";
    }

}
