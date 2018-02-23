package org.liuyehcf.grammar.rg;

import org.junit.Test;
import org.liuyehcf.grammar.rg.utils.TestCaseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public void testGroupPrint() {
        RGParser parser = RGBuilder.compile("(abc(cd)(ef(g)))").buildNfa();
        parser.print();

        Matcher matcher = parser.matcher("abccdefg");


        System.out.println(matcher.matches());

        System.out.println(matcher.group(0));
        System.out.println(matcher.group(1));
        System.out.println(matcher.group(2));
        System.out.println(matcher.group(3));
        System.out.println(matcher.group(4));

    }
}
