package org.liuyehcf.grammar.rg;

import org.junit.Test;
import org.liuyehcf.grammar.rg.utils.TestCaseBuilder;
import org.liuyehcf.grammar.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.liuyehcf.grammar.rg.TestRegex.*;

/**
 * Created by Liuye on 2017/10/23.
 */
public class TestNfa {

    private void testRegexGroup(String[] regexGroup,
                                boolean testAllPossibleCases,
                                int randomTimes) {
        for (String regex : regexGroup) {
            testEachRegex(regex,
                    testAllPossibleCases,
                    randomTimes);
        }
    }

    private void testEachRegex(String regex,
                               boolean testAllPossibleCases,
                               int randomTimes) {
        RGParser parser = RGBuilder.compile(regex).buildNfa();

        Pattern pattern = Pattern.compile(regex);
        List<String> unPassedCases = null;

        try {
            if (testAllPossibleCases) {
                Set<String> matchedCases = TestCaseBuilder.createAllOptionalTestCasesWithRegex(regex);
                unPassedCases = testNfaWithMatchedCases(pattern, parser, matchedCases);
                assertTrue(unPassedCases.isEmpty());
            }

            Set<String> matchedCases = TestCaseBuilder.createRandomTestCasesWithRegex(regex, randomTimes);
            unPassedCases = testNfaWithMatchedCases(pattern, parser, matchedCases);
            assertTrue(unPassedCases.isEmpty());
        } catch (AssertionError e) {
            System.err.println("Regex: [" + regex + "], unPassedCases: " + unPassedCases);
            throw e;
        }
    }

    private List<String> testNfaWithMatchedCases(Pattern pattern, RGParser parser, Set<String> matchedCases) {
        List<String> unPassedCases = new ArrayList<>();
        for (String matchedCase : matchedCases) {

            java.util.regex.Matcher jdkMatcher = pattern.matcher(matchedCase);
            Matcher nfaMatcher = parser.matcher(matchedCase);

            assertTrue(jdkMatcher.matches());

            if (!nfaMatcher.matches() || jdkMatcher.groupCount() != nfaMatcher.groupCount()) {
                unPassedCases.add(matchedCase);
                continue;
            }

            for (int group = 0; group <= jdkMatcher.groupCount(); group++) {
                String jdkGroup = jdkMatcher.group(group);
                String nfaGroup = nfaMatcher.group(group);
                if (jdkMatcher.start(group) != nfaMatcher.start(group)
                        || jdkMatcher.end(group) != nfaMatcher.end(group)) {
                    unPassedCases.add(matchedCase);
                    break;
                }
                if (jdkGroup == null) {
                    if (nfaGroup != null) {
                        unPassedCases.add(matchedCase);
                        break;
                    }
                } else {
                    if (!jdkGroup.equals(nfaGroup)) {
                        unPassedCases.add(matchedCase);
                        break;
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
                1000);
    }

    @Test
    public void testGroup2() {
        testRegexGroup(REGEX_GROUP_2,
                true,
                1000);
    }

    @Test
    public void testGroup3() {
        testRegexGroup(REGEX_GROUP_3,
                true,
                1000);
    }

    @Test
    public void testGroup4() {
        testRegexGroup(REGEX_GROUP_4,
                true,
                1000);
    }

    @Test
    public void testGroup5() {
        testRegexGroup(REGEX_GROUP_5,
                true,
                1000);
    }

    @Test
    public void testGroup6() {
        testRegexGroup(REGEX_GROUP_6,
                true,
                1000);
    }

    @Test
    public void testGroup7() {
        testRegexGroup(REGEX_GROUP_7,
                true,
                1000);
    }

    @Test
    public void testGroup8() {
        testRegexGroup(REGEX_GROUP_8,
                false,
                1000);
    }

    @Test
    public void testGroup9() {
        testRegexGroup(REGEX_GROUP_9,
                false,
                1000);
    }

    @Test
    public void testGroupSpecial() {
        testRegexGroup(REGEX_GROUP_SPECIAL,
                false,
                1000);
    }

    @Test
    public void testFindCase1() {
        RGParser parser = RGBuilder.compile("()").buildNfa();

        Matcher matcher = parser.matcher("");

        matcher.matches();

        assertEquals(
                "",
                matcher.group(0)
        );
    }

    @Test
    public void testFindCase2() {
        RGParser parser = RGBuilder.compile(createIdentifierRegex()).buildNfa();

        Matcher matcher = parser.matcher("a11 aa  asdfasdf111 _asdf");

        assertTrue(matcher.find());
        assertEquals(
                "a11",
                matcher.group(0)
        );
        assertTrue(matcher.find());
        assertEquals(
                "aa",
                matcher.group(0)
        );
        assertTrue(matcher.find());
        assertEquals(
                "asdfasdf111",
                matcher.group(0)
        );
        assertTrue(matcher.find());
        assertEquals(
                "_asdf",
                matcher.group(0)
        );
        assertFalse(matcher.find());
    }

    @Test
    public void testFindCase3() {
        RGParser parser = RGBuilder.compile("(a+)*").buildNfa();

        Matcher matcher = parser.matcher("aaaaaaaaa");

        assertTrue(matcher.matches());
        assertEquals(
                "aaaaaaaaa",
                matcher.group(1)
        );
    }

    @Test
    public void testFindCase4() {
        RGParser parser = RGBuilder.compile("(a)|(b)|(ab)").buildNfa();

        Matcher matcher = parser.matcher("ab aaa bbb abb abab");

        List<String> expects = ListUtils.of("a", "b", "a", "a", "a", "b", "b", "b", "a", "b", "b", "a", "b", "a", "b");

        int index = 0;
        while (matcher.find()) {
            assertEquals(
                    expects.get(index++),
                    matcher.group(0)
            );
        }
    }

    @Test
    public void testFindCase5() {
        RGParser parser = RGBuilder.compile("((a)|(b)|(ab))+").buildNfa();

        Matcher matcher = parser.matcher("ab aaa bbb abb abab");

        List<String> expects = ListUtils.of("ab", "aaa", "bbb", "abb", "abab");

        int index = 0;
        while (matcher.find()) {
            assertEquals(
                    expects.get(index++),
                    matcher.group(0)
            );
        }
    }


    @Test
    public void testGreedyMode() {
        RGParser parser = RGBuilder.compile("a((Ba)*)B(a(Ba)*)").buildNfa();

        Matcher matcher = parser.matcher("aBaBaBa");

        assertTrue(matcher.matches());
        assertEquals(
                "BaBa",
                matcher.group(1)
        );
    }


    @Test
    //todo
    public void test() {
        RGParser parser = RGBuilder.compile("(\\()|(a)|(\\))").buildNfa();

        Matcher matcher = parser.matcher("(a)");

        while(matcher.find()){
            System.out.println(matcher.group(0));
        }
    }
}
