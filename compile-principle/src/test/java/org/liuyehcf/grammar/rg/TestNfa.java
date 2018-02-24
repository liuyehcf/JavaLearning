package org.liuyehcf.grammar.rg;

import org.junit.Test;
import org.liuyehcf.grammar.rg.utils.TestCaseBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.assertTrue;
import static org.liuyehcf.grammar.rg.TestRegex.*;

/**
 * Created by Liuye on 2017/10/23.
 */
public class TestNfa {

    private void testRegexGroup(String[] regexGroup,
                                boolean testAllPossibleCases,
                                int randomTimes,
                                boolean testGroup) {
        for (String regex : regexGroup) {
            testEachRegex(regex,
                    testAllPossibleCases,
                    randomTimes,
                    testGroup);
        }
    }

    private void testEachRegex(String regex,
                               boolean testAllPossibleCases,
                               int randomTimes,
                               boolean testGroup) {
        RGParser parser = RGBuilder.compile(regex).buildNfa();

        Pattern pattern = Pattern.compile(regex);
        List<String> unPassedCases = null;

        try {
            if (testAllPossibleCases) {
                Set<String> matchedCases = TestCaseBuilder.createAllOptionalTestCasesWithRegex(regex);
                unPassedCases = testNfaWithMatchedCases(pattern, parser, matchedCases, testGroup);
                assertTrue(unPassedCases.isEmpty());
            }

            Set<String> matchedCases = TestCaseBuilder.createRandomTestCasesWithRegex(regex, randomTimes);
            unPassedCases = testNfaWithMatchedCases(pattern, parser, matchedCases, testGroup);
            assertTrue(unPassedCases.isEmpty());
        } catch (AssertionError e) {
            System.err.println("Regex: [" + regex + "], unPassedCases: " + unPassedCases);
            throw e;
        }
    }

    private List<String> testNfaWithMatchedCases(Pattern pattern, RGParser parser, Set<String> matchedCases, boolean testGroup) {
        List<String> unPassedCases = new ArrayList<>();
        for (String matchedCase : matchedCases) {

            java.util.regex.Matcher jdkMatcher = pattern.matcher(matchedCase);
            Matcher nfaMatcher = parser.matcher(matchedCase);

            assertTrue(jdkMatcher.matches());

            if (!nfaMatcher.matches()) {
                unPassedCases.add(matchedCase);
                continue;
            }

            if (testGroup) {
                for (int group = 0; group < jdkMatcher.groupCount(); group++) {
                    String jdkGroup = jdkMatcher.group(group);
                    String nfaGroup = nfaMatcher.group(group);
                    if (jdkGroup == null) {
                        if (nfaGroup != null) {
                            unPassedCases.add(matchedCase);
                        }
                    } else {
                        if (!jdkGroup.equals(nfaGroup)) {
                            unPassedCases.add(matchedCase);
                        }
                    }
                }
            }
        }

        return unPassedCases;
    }

    @Test
    public void testGroup1() {
        testRegexGroup(REGEX_GROUP_1,
                true,
                1000,
                true);
    }

    @Test
    public void testGroup2() {
        testRegexGroup(REGEX_GROUP_2,
                true,
                1000,
                true);
    }

    @Test
    public void testGroup3() {
        testRegexGroup(REGEX_GROUP_3,
                true,
                1000,
                true);
    }

    @Test
    public void testGroup4() {
        testRegexGroup(REGEX_GROUP_4,
                true,
                1000,
                true);
    }

    @Test
    public void testGroup5() {
        testRegexGroup(REGEX_GROUP_5,
                true,
                1000,
                true);
    }

    @Test
    public void testGroup6() {
        testRegexGroup(REGEX_GROUP_6,
                true,
                1000,
                true);
    }

    @Test
    public void testGroup7() {
        testRegexGroup(REGEX_GROUP_7,
                false,
                100,
                true);
    }

    @Test
    public void testGroup8() {
        testRegexGroup(REGEX_GROUP_8,
                false,
                100,
                true);
    }

    @Test
    public void testGroupSpecial() {
        // todo ，这里的正则表达式涉及到贪婪模式，因此不测 group
        testRegexGroup(REGEX_GROUP_SPECIAL,
                false,
                100,
                false);
    }

    @Test
    public void test(){
        RGParser parser=RGBuilder.compile("()").buildNfa();

        Matcher matcher=parser.matcher("");

        while(matcher.find()){
            System.out.println(matcher.group(0));
        }
    }
}
