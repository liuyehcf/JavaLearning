package org.liuyehcf.compile.grammar.regex;

import org.junit.Test;
import org.liuyehcf.compile.grammar.regex.composition.Matcher;
import org.liuyehcf.compile.grammar.regex.util.RegexAutoMachine;
import org.liuyehcf.compile.grammar.regex.util.TestCaseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.liuyehcf.compile.grammar.regex.TestRegex.*;

/**
 * Created by Liuye on 2017/10/24.
 */
public class TestDfa {

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
        Matcher matcher = RegexAutoMachine.compile(regex).getDfaMatcher();

        if (testAllPossibleCases) {
            Set<String> matchedCases = TestCaseBuilder.createAllOptionalTestCasesWithRegex(regex);
            if (printAllPossibleCases) {
                System.out.println(matchedCases);
                System.out.println("\n============================\n");
            }
            testDfaWithMatchedCases(matcher, matchedCases);
        }

        if (testRandomCases) {
            Set<String> matchedCases = TestCaseBuilder.createRandomTestCasesWithRegex(regex, randomTimes);
            if (printRandomCases) {
                System.out.println(matchedCases);
                System.out.println("\n============================\n");
            }
            testDfaWithMatchedCases(matcher, matchedCases);
        }
    }


    private void testDfaWithMatchedCases(Matcher matcher, Set<String> matchedCases) {
        List<String> unPassedCases = new ArrayList<>();
        for (String matchedCase : matchedCases) {
            if (!matcher.isMatch(matchedCase)) {
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
                1000);
    }

    @Test
    public void testGroup7() {
        testRegexGroup(REGEX_GROUP_7,
                false,
                false,
                true,
                false,
                1000);
    }

    @Test
    public void testGroupSpecial() {
        testRegexGroup(REGEX_GROUP_SPECIAL,
                false,
                false,
                true,
                false,
                1000);
    }

}