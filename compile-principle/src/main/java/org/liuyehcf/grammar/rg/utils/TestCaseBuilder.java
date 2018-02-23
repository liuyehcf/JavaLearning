package org.liuyehcf.grammar.rg.utils;


import org.liuyehcf.grammar.core.definition.Symbol;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.assertTrue;

/**
 * Created by Liuye on 2017/10/24.
 */
public abstract class TestCaseBuilder {

    private static char[] alphabetCharacters = new char[256];

    static {
        for (char c = 0; c < 256; c++) {
            alphabetCharacters[c] = c;
        }
    }

    protected final String regex;
    int index = 0;
    LinkedList<String> contentStack = new LinkedList<>();
    String curContent;
    Set<String> testCases = new HashSet<>();
    LinkedList<LinkedList<String>> revokeHelper = new LinkedList<>();

    TestCaseBuilder(String regex) {
        this.regex = regex;
    }

    public static Set<String> createAllOptionalTestCasesWithRegex(String regex) {
        return EachCaseBuilder.getEachTestCasesWithRegex(regex);
    }

    public static Set<String> createRandomTestCasesWithRegex(String regex, int times) {
        Set<String> randomTestCases = new HashSet<>();
        for (int time = 0; time < times; time++) {
            randomTestCases.addAll(RandomCaseBuilder.getRandomTestCasesWithRegex(regex));
        }
        return randomTestCases;
    }

    private static Set<Character> getOppositeChars(Set<Character> excludedChars) {
        Set<Character> oppositeChars = new HashSet<>();
        for (char c : alphabetCharacters) {
            oppositeChars.add(c);
        }
        oppositeChars.removeAll(excludedChars);
        return oppositeChars;
    }

    void build() {
        backtracking();
    }

    void backtracking() {
        if (!hasNext()) {
            addTestCase();
            return;
        }
        switch (getCurChar()) {
            case '.':
                processWhenEncounteredAny();
                break;
            case '|':
                processWhenEncounteredOr();
                break;
            case '*':
                processWhenEncounteredStar();
                break;
            case '+':
                processWhenEncounteredAdd();
                break;
            case '\\':
                processWhenEncounteredEscaped();
                break;
            case '[':
                processWhenEncounteredLeftMiddleParenthesis();
                break;
            case '(':
                processWhenEncounteredLeftSmallParenthesis();
                break;
            default:
                processWhenEncounteredNormal();
        }
    }

    boolean hasNext() {
        return index < regex.length();
    }

    void addTestCase() {
        pushCurStackUnion();
        testCases.add(getStackString());
        popToCurStackUnion();
    }

    String getStackString() {
        Iterator<String> it = contentStack.iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            sb.insert(0, it.next());
        }
        return sb.toString();
    }

    char getCurChar() {
        return regex.charAt(index);
    }

    protected abstract void processWhenEncounteredAny();

    protected abstract void processWhenEncounteredOr();

    protected abstract void processWhenEncounteredStar();

    protected abstract void processWhenEncounteredAdd();

    protected abstract void processWhenEncounteredEscaped();

    protected abstract void processWhenEncounteredLeftMiddleParenthesis();

    protected abstract void processWhenEncounteredLeftSmallParenthesis();

    protected abstract void processWhenEncounteredNormal();

    protected abstract Set<String> getTestCasesWithRegex(String regex);

    void pushCurStackUnion() {
        if (curContent != null) {
            contentStack.push(curContent);
            curContent = null;
        }
    }

    void popToCurStackUnion() {
        if (!contentStack.isEmpty())
            curContent = contentStack.pop();
        else
            curContent = null;
    }

    String getCombinedStringOfCurGroup() {
        LinkedList<String> curRevokeStack = new LinkedList<>();

        StringBuilder sb = new StringBuilder();

        while (!contentStack.isEmpty()) {
            String peekContent = contentStack.pop();
            sb.insert(0, peekContent);
            curRevokeStack.push(peekContent);
        }

        revokeHelper.push(curRevokeStack);

        return sb.toString();
    }

    List<List<String>> getTestCasesOfAllParts(String combinedStringOfCurGroup) {

        List<List<String>> testCasesOfAllParts = new ArrayList<>();
        testCasesOfAllParts.add(Arrays.asList(combinedStringOfCurGroup));

        addAllAdjacentOrParts(testCasesOfAllParts);

        return testCasesOfAllParts;
    }

    private void addAllAdjacentOrParts(List<List<String>> testCasesOfAllParts) {
        do {
            testCasesOfAllParts.add(getTestCasesOfNextPart());
        } while (hasNext() && getCurChar() == '|');
    }

    void revokeCombinedStringOfCurGroup() {
        LinkedList<String> curRevokeStack = revokeHelper.pop();

        while (!curRevokeStack.isEmpty()) {
            contentStack.push(curRevokeStack.pop());
        }
    }

    String copy(String origin, int times) {
        String s = "";
        while (times-- > 0) {
            s += origin;
        }
        return s;
    }

    List<Character> getAllOptionalChars() {
        index++;
        boolean isNot = (getCurChar() == '^');

        Set<Character> optionalChars = new HashSet<>();
        if (isNot) index++;

        do {
            if (getCurChar() == '\\') {
                index++;
                for (Symbol symbol : EscapedUtil.getSymbolsOfEscapedCharInMiddleParenthesis(getCurChar())) {
                    optionalChars.add(symbol.getValue().charAt(0));
                }
            } else {
                optionalChars.add(getCurChar());
            }
            index++;
        } while (getCurChar() != ']');

        index++;

        if (isNot) {
            optionalChars = getOppositeChars(optionalChars);
        }

        return new ArrayList<>(optionalChars);
    }

    List<String> getTestCasesOfNextPart() {
        int count = 1;
        index++;
        int startIndex = index, endIndex;
        while (hasNext() && count > 0) {
            if (getCurChar() == '(') {
                count++;
            } else if (getCurChar() == ')') {
                count--;
            }
            index++;
        }

        if (count == 0) {
            endIndex = index - 1;
        } else {
            endIndex = index;
        }

        return new ArrayList<>(getTestCasesWithRegex(regex.substring(startIndex, endIndex)));
    }

    private static final class EachCaseBuilder extends TestCaseBuilder {

        public EachCaseBuilder(String regex) {
            super(regex);
        }

        static Set<String> getEachTestCasesWithRegex(String regex) {
            EachCaseBuilder testCaseBuilder = new EachCaseBuilder(regex);
            testCaseBuilder.build();
            return testCaseBuilder.testCases;
        }

        @Override
        protected void processWhenEncounteredAny() {
            pushCurStackUnion();
            for (char c = 0; c < 256; c++) {
                if (!SymbolUtils.isLegalCharMatchesAny(c)) continue;
                curContent = "" + c;
                index++;
                backtracking();
                index--;
            }
            popToCurStackUnion();
        }

        @Override
        protected void processWhenEncounteredOr() {
            int tempIndex = index;
            pushCurStackUnion();
            String combinedStringOfCurGroup = getCombinedStringOfCurGroup();

            List<List<String>> testCasesOfAllParts = getTestCasesOfAllParts(combinedStringOfCurGroup);

            for (List<String> testCases : testCasesOfAllParts) {
                for (String testCase : testCases) {
                    curContent = testCase;
                    backtracking();
                }
            }

            revokeCombinedStringOfCurGroup();
            popToCurStackUnion();
            index = tempIndex;
        }

        @Override
        protected void processWhenEncounteredStar() {
            makeDifferentRepeatCases(0, 1, 2, 4, 8);
        }

        private void makeDifferentRepeatCases(int... repeatTimes) {
            String tempCurStackUnion = curContent;
            for (int repeatTime : repeatTimes) {
                makeSpecificRepeatCase(repeatTime, tempCurStackUnion);
            }
        }

        private void makeSpecificRepeatCase(int repeatTime, String tempCurStackUnion) {
            index++;

            curContent = copy(tempCurStackUnion, repeatTime);
            backtracking();

            index--;
        }

        @Override
        protected void processWhenEncounteredAdd() {
            makeDifferentRepeatCases(1, 2, 4, 8);
        }

        @Override
        protected void processWhenEncounteredEscaped() {
            int tempIndex = index;
            pushCurStackUnion();

            index++;

            List<Symbol> symbols = EscapedUtil.getSymbolsOfEscapedChar(getCurChar());

            index++;
            for (Symbol symbol : symbols) {
                curContent = symbol.getValue();
                backtracking();
            }

            popToCurStackUnion();
            index = tempIndex;
        }

        @Override
        protected void processWhenEncounteredLeftMiddleParenthesis() {
            int tempIndex = index;
            pushCurStackUnion();

            List<Character> optionalChars = getAllOptionalChars();

            for (char optionChar : optionalChars) {
                curContent = "" + optionChar;
                backtracking();
            }

            popToCurStackUnion();
            index = tempIndex;
        }

        @Override
        protected void processWhenEncounteredLeftSmallParenthesis() {
            int tempIndex = index;
            pushCurStackUnion();

            List<String> nextPartTestCases = getTestCasesOfNextPart();

            chooseEachCases(nextPartTestCases);

            popToCurStackUnion();
            index = tempIndex;
        }

        private void chooseEachCases(List<String> nextPartTestCases) {
            for (String testCase : nextPartTestCases) {
                curContent = testCase;
                backtracking();
            }
        }

        @Override
        protected void processWhenEncounteredNormal() {
            int tempIndex = index;
            pushCurStackUnion();

            curContent = "" + getCurChar();
            index++;
            backtracking();

            popToCurStackUnion();
            index = tempIndex;
        }

        @Override
        protected Set<String> getTestCasesWithRegex(String regex) {
            return getEachTestCasesWithRegex(regex);
        }
    }

    private static final class RandomCaseBuilder extends TestCaseBuilder {
        private static final Random random = new Random();

        public RandomCaseBuilder(String regex) {
            super(regex);
        }

        static Set<String> getRandomTestCasesWithRegex(String regex) {
            RandomCaseBuilder testCaseBuilder = new RandomCaseBuilder(regex);
            testCaseBuilder.backtracking();
            return testCaseBuilder.testCases;
        }

        @Override
        protected void processWhenEncounteredAny() {
            char c = (char) random.nextInt(256);
            while (!SymbolUtils.isLegalCharMatchesAny(c)) {
                c = (char) random.nextInt(256);
            }

            pushCurStackUnion();
            curContent = "" + c;
            index++;

            backtracking();
        }

        @Override
        protected void processWhenEncounteredOr() {
            pushCurStackUnion();
            String combinedStringOfCurGroup = getCombinedStringOfCurGroup();

            List<List<String>> testCasesOfAllParts = getTestCasesOfAllParts(combinedStringOfCurGroup);

            int textCaseIndex = random.nextInt(testCasesOfAllParts.size());
            
            assertTrue(testCasesOfAllParts.get(textCaseIndex).size() == 1);
            String testCase = testCasesOfAllParts.get(textCaseIndex).iterator().next();

            pushCurStackUnion();
            curContent = testCase;

            backtracking();
        }

        @Override
        protected void processWhenEncounteredStar() {
            index++;

            curContent = copy(curContent, random.nextInt(8));

            backtracking();
        }

        @Override
        protected void processWhenEncounteredAdd() {
            index++;

            curContent = copy(curContent, random.nextInt(8) + 1);

            backtracking();
        }

        @Override
        protected void processWhenEncounteredEscaped() {
            pushCurStackUnion();

            index++;

            List<Symbol> symbols = EscapedUtil.getSymbolsOfEscapedChar(getCurChar());

            index++;
            int charIndex = random.nextInt(symbols.size());
            curContent = symbols.get(charIndex).getValue();

            backtracking();
        }

        @Override
        protected void processWhenEncounteredLeftMiddleParenthesis() {
            pushCurStackUnion();

            List<Character> optionalChars = getAllOptionalChars();
            int charIndex = random.nextInt(optionalChars.size());

            curContent = "" + optionalChars.get(charIndex);

            backtracking();
        }

        @Override
        protected void processWhenEncounteredLeftSmallParenthesis() {
            pushCurStackUnion();

            List<String> nextPartTestCases = new ArrayList<>(getTestCasesOfNextPart());

            if (nextPartTestCases.size() != 0) {
                int testCaseIndex = random.nextInt(nextPartTestCases.size());

                curContent = nextPartTestCases.get(testCaseIndex);
            }

            backtracking();
        }

        @Override
        protected void processWhenEncounteredNormal() {
            pushCurStackUnion();

            curContent = "" + getCurChar();
            index++;

            backtracking();
        }

        @Override
        protected Set<String> getTestCasesWithRegex(String regex) {
            return getRandomTestCasesWithRegex(regex);
        }
    }
}

