package org.liuyehcf.grammar.rg;

import org.junit.Test;
import org.liuyehcf.grammar.rg.utils.TestCaseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.liuyehcf.grammar.rg.TestRegex.*;

/**
 * Created by Liuye on 2017/10/23.
 */
public class TestNfa {

    private void testRegexGroup(String[] regexGroup,
                                boolean testAllPossibleCases,
                                boolean printAllPossibleCases,
                                boolean testRandomCases,
                                boolean printRandomCases,
                                int randomTimes) {
        for (String regex : regexGroup) {
            testEachRegex(regex,
                    testAllPossibleCases,
                    printAllPossibleCases,
                    testRandomCases,
                    printRandomCases,
                    randomTimes);
        }
    }

    private void testEachRegex(String regex,
                               boolean testAllPossibleCases,
                               boolean printAllPossibleCases,
                               boolean testRandomCases,
                               boolean printRandomCases,
                               int randomTimes) {
        RGParser parser = RGBuilder.compile(regex).buildNfa();

        if (testAllPossibleCases) {
            Set<String> matchedCases = TestCaseBuilder.createAllOptionalTestCasesWithRegex(regex);
            if (printAllPossibleCases) {
                System.out.println(matchedCases);
                System.out.println("\n============================\n");
            }
            testNfaWithMatchedCases(parser, matchedCases);
        }

        if (testRandomCases) {
            Set<String> matchedCases = TestCaseBuilder.createRandomTestCasesWithRegex(regex, randomTimes);
            if (printRandomCases) {
                System.out.println(matchedCases);
                System.out.println("\n============================\n");
            }
            testNfaWithMatchedCases(parser, matchedCases);
        }
    }

    private void testNfaWithMatchedCases(RGParser parser, Set<String> matchedCases) {
        List<String> unPassedCases = new ArrayList<>();
        for (String matchedCase : matchedCases) {
            if (!parser.matches(matchedCase)) {
                unPassedCases.add(matchedCase);
            }
        }

        if (!unPassedCases.isEmpty()) {
            System.err.println(unPassedCases);
            throw new RuntimeException();
        }
    }

    @Test
    public void testGroup1() {
        testRegexGroup(REGEX_GROUP_1,
                true,
                false,
                true,
                false,
                1000);
    }

    @Test
    public void testGroup2() {
        testRegexGroup(REGEX_GROUP_2,
                true,
                false,
                true,
                false,
                1000);
    }

    @Test
    public void testGroup3() {
        testRegexGroup(REGEX_GROUP_3,
                true,
                false,
                true,
                false,
                1000);
    }

    @Test
    public void testGroup4() {
        testRegexGroup(REGEX_GROUP_4,
                true,
                false,
                true,
                false,
                1000);
    }

    @Test
    public void testGroup5() {
        testRegexGroup(REGEX_GROUP_5,
                true,
                false,
                true,
                false,
                1000);
    }

    @Test
    public void testGroup6() {
        testRegexGroup(REGEX_GROUP_6,
                false,
                false,
                true,
                false,
                100);
    }

    @Test
    public void testGroup7() {
        testRegexGroup(REGEX_GROUP_7,
                false,
                false,
                true,
                false,
                100);
    }

    @Test
    public void testGroupSpecial() {
        testRegexGroup(REGEX_GROUP_SPECIAL,
                false,
                false,
                false,
                false,
                1);
    }

    @Test
    public void testGroupMatcher1() {
        RGParser parser = RGBuilder.compile("(abc(cd)(ef(g)()))").buildNfa();
        parser.print();

        Matcher matcher = parser.matcher("abccdefg");

        assertTrue(matcher.matches());
        assertEquals(
                "abccdefg",
                matcher.group(0)
        );
        assertEquals(
                "abccdefg",
                matcher.group(1)
        );
        assertEquals(
                "cd",
                matcher.group(2)
        );
        assertEquals(
                "efg",
                matcher.group(3)
        );
        assertEquals(
                "g",
                matcher.group(4)
        );
        assertEquals(
                "",
                matcher.group(5)
        );
    }

    @Test
    public void testGroupMatcher2() {
        RGParser parser = RGBuilder.compile("((a)|(b))").buildNfa();

        parser.print();

        Matcher matcher = parser.matcher("a");

        assertTrue(matcher.matches());
        assertEquals(
                "a",
                matcher.group(0)
        );
        assertEquals(
                "a",
                matcher.group(1)
        );
        assertEquals(
                "a",
                matcher.group(2)
        );
        assertEquals(
                null,
                matcher.group(3)
        );
    }

    @Test
    public void testGroupMatcher3() {
        RGParser parser = RGBuilder.compile("((a(b|e))+)").buildNfa();

        parser.print();

        Matcher matcher = parser.matcher("abababae");

        assertTrue(matcher.matches());
        assertEquals(
                "abababae",
                matcher.group(0)
        );
        assertEquals(
                "abababae",
                matcher.group(1)
        );
        assertEquals(
                "ab",
                matcher.group(2)
        );
    }

    @Test
    public void testGroupMatcher4() {
        RGParser parser = RGBuilder.compile("a*").buildNfa();

        parser.print();

        Matcher matcher = parser.matcher("");

        assertTrue(matcher.matches());
    }

    @Test
    public void testJDKRegex(){
        Pattern p=Pattern.compile("((a(b|e))+)");
        java.util.regex.Matcher matcher = p.matcher("abaeabaeab");

        matcher.matches();

        System.out.println(matcher.group(3));
    }

}
