package org.liuyehcf.grammar.rg;

import org.junit.Test;
import org.liuyehcf.grammar.rg.utils.TestCaseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.liuyehcf.grammar.rg.TestRegex.*;


/**
 * Created by Liuye on 2017/10/24.
 */
public class TestTestCaseBuilder {

    private void testRegexGroup(String[] regexGroup,
                                boolean testAllPossibleCases,
                                boolean testRandomCases,
                                int randomTimes) {
        for (String regex : regexGroup) {
            testEachRegex(regex,
                    testAllPossibleCases,
                    testRandomCases,
                    randomTimes);
        }
    }

    private void testEachRegex(String regex,
                               boolean testAllPossibleCases,
                               boolean testRandomCases,
                               int randomTimes) {
        if (testAllPossibleCases) {
            Set<String> testCases = TestCaseBuilder.createAllOptionalTestCasesWithRegex(regex);
            verifyTestCasesWithRegex(testCases, regex);
        }

        if (testRandomCases) {
            Set<String> testCases = TestCaseBuilder.createRandomTestCasesWithRegex(regex, randomTimes);
            verifyTestCasesWithRegex(testCases, regex);
        }
    }

    private void verifyTestCasesWithRegex(Set<String> testCases, String regex) {
        Pattern p = Pattern.compile(regex);
        List<String> wrongCases = new ArrayList<>();
        for (String testCase : testCases) {
            System.out.println(testCase);
            Matcher m = p.matcher(testCase);
            if (!m.matches()) {
                wrongCases.add(testCase);
            }
        }

        if (!wrongCases.isEmpty()) {
            System.out.println(wrongCases);
            throw new RuntimeException();
        }
    }

    @Test
    public void testRegexGroup1() {
        testRegexGroup(REGEX_GROUP_1,
                true,
                true,
                10);
    }

    @Test
    public void testRegexGroup2() {
        testRegexGroup(REGEX_GROUP_2,
                true,
                true,
                10);
    }

    @Test
    public void testRegexGroup3() {
        testRegexGroup(REGEX_GROUP_3,
                true,
                true,
                10);
    }

    @Test
    public void testRegexGroup4() {
        testRegexGroup(REGEX_GROUP_4,
                true,
                true,
                10);
    }

    @Test
    public void testRegexGroup5() {
        testRegexGroup(REGEX_GROUP_5,
                true,
                true,
                10);
    }

    @Test
    public void testRegexGroup6() {
        testRegexGroup(REGEX_GROUP_6,
                false,
                true,
                1000);
    }

    @Test
    public void testRegexGroup7() {
        testRegexGroup(REGEX_GROUP_7,
                false,
                true,
                1000);
    }

    @Test
    public void testRegexGroupSpecial() {
        testRegexGroup(REGEX_GROUP_SPECIAL,
                false,
                true,
                100);
    }
}
