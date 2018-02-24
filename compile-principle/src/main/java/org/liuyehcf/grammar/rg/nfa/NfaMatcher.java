package org.liuyehcf.grammar.rg.nfa;

import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.rg.Matcher;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;
import org.liuyehcf.grammar.utils.Pair;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.assertNull;

public class NfaMatcher implements Matcher {

    // Nfa自动机
    private final Nfa nfa;

    // 待匹配的输入字符串
    private final String input;
    // 匹配的区间
    List<Pair<Integer, Integer>> matchIntervals;
    // group i --> 起始索引 的映射表，闭集
    private Map<Integer, Integer> groupStartIndexes = null;
    // group i --> 接收索引 的映射表，闭集
    private Map<Integer, Integer> groupEndIndexes = null;
    // 目前进行匹配操作的子串
    private String subInput;
    // 匹配子串索引
    private int indexOfMatchIntervals;

    NfaMatcher(Nfa nfa, String input) {
        if (nfa == null || input == null) {
            throw new NullPointerException();
        }
        this.nfa = nfa;
        this.input = input;
    }

    @Override
    public boolean matches() {
        return doMatch(input);
    }

    private boolean doMatch(String curInput) {
        this.subInput = curInput;

        groupStartIndexes = new HashMap<>();
        groupEndIndexes = new HashMap<>();

        NfaState curNfaState = nfa.getNfaClosure().getStartNfaState();

        Set<String> visitedNfaState = new HashSet<>();

        boolean result = isMatchDfsProxy(curNfaState, 0, visitedNfaState);

        Set<Integer> keySets = groupStartIndexes.keySet();
        for (int group : keySets.toArray(new Integer[0])) {
            if (!groupEndIndexes.containsKey(group)) {
                groupStartIndexes.remove(group);
            }
        }

        return result;
    }

    private boolean isMatchDfsProxy(NfaState curNfaState, int index, Set<String> visitedNfaState) {
        Pair<Map<Integer, Integer>, Map<Integer, Integer>> pair = setGroupIndex(curNfaState, index);

        boolean result = isMatchDfs(curNfaState, index, visitedNfaState);

        if (!result) {
            groupBackTrack(pair);
        }

        return result;
    }

    private Pair<Map<Integer, Integer>, Map<Integer, Integer>> setGroupIndex(NfaState curNfaState, int index) {
        // 由于需要回溯，因此保留一下原始状态
        Pair<Map<Integer, Integer>, Map<Integer, Integer>> pair = new Pair<>(
                new HashMap<>(groupStartIndexes),
                new HashMap<>(groupEndIndexes)
        );

        if (!curNfaState.getGroupStart().isEmpty()) {
            for (int group : curNfaState.getGroupStart()) {
                groupStartIndexes.put(group, index);
            }
        }

        if (!curNfaState.getGroupReceive().isEmpty()) {
            for (int group : curNfaState.getGroupReceive()) {
                groupEndIndexes.put(group, index);
            }
        }

        return pair;
    }

    private void groupBackTrack(Pair<Map<Integer, Integer>, Map<Integer, Integer>> pair) {
        groupStartIndexes = pair.getFirst();
        groupEndIndexes = pair.getSecond();
    }

    private boolean isMatchDfs(NfaState curNfaState, int index, Set<String> visitedNfaState) {

        // 首先走非ε边，贪婪模式（todo 并非完全贪婪）
        if (index != subInput.length()) {
            // 从当前节点出发，经过非ε边的next节点集合
            Set<NfaState> nextStates = curNfaState.getNextNfaStatesWithInputSymbol(
                    SymbolUtils.getAlphabetSymbolWithChar(subInput.charAt(index)));

            for (NfaState nextState : nextStates) {

                if (isMatchDfsProxy(nextState, index + 1, visitedNfaState))
                    return true;
            }
        }

        if (index == subInput.length() && curNfaState.canReceive()) {
            return true;
        }

        // 从当前节点出发，经过ε边的后继节点集合
        Set<NfaState> epsilonNextStates = curNfaState.getNextNfaStatesWithInputSymbol(
                Symbol.EPSILON
        );
        for (NfaState nextState : epsilonNextStates) {
            // 为了避免重复经过相同的 ε边，每次访问ε边，给一个标记
            // 在匹配目标字符串的不同位置时，允许经过相同的ε边
            String curStateString = nextState.toString() + index;

            if (visitedNfaState.add(curStateString)) {
                if (isMatchDfsProxy(nextState, index, visitedNfaState))
                    return true;
                visitedNfaState.remove(curStateString);
            }
        }

        return false;
    }

    @Override
    public boolean find() {
        //todo 不支持 "(a)|(b)|(ab)"，如果字符串是ab，正确地应该是a和b，而这里直接是ab。

        if (matchIntervals == null) {
            initMatchIntervals();
        }

        if (indexOfMatchIntervals < matchIntervals.size()) {

            Pair<Integer, Integer> interval = matchIntervals.get(indexOfMatchIntervals);

            doMatch(input.substring(
                    interval.getFirst(),
                    interval.getSecond()
            ));

            indexOfMatchIntervals++;

            return true;
        }
        return false;
    }

    private void initMatchIntervals() {
        matchIntervals = new ArrayList<>();

        if (input.length() == 0) {
            if (matches()) {
                matchIntervals.add(new Pair<>(0, 0));
            }
        }

        for (int startIndex = 0; startIndex < input.length(); startIndex++) {
            for (int endIndex = startIndex + 1; endIndex <= input.length(); endIndex++) {
                if (doMatch(input.substring(startIndex, endIndex))) {
                    matchIntervals.add(new Pair<>(startIndex, endIndex));
                }
            }
        }

        if (matchIntervals.isEmpty()) {
            return;
        }

        // 目前仅支持以贪婪模式查询匹配的子串
        // 首先排序
        matchIntervals.sort(new Comparator<Pair<Integer, Integer>>() {
            @Override
            public int compare(Pair<Integer, Integer> o1, Pair<Integer, Integer> o2) {
                if (o1.getFirst() < o2.getFirst()) {
                    return -1;
                } else if (o1.getFirst() > o2.getFirst()) {
                    return 1;
                } else {
                    if (o1.getSecond() > o2.getSecond()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            }
        });

        // 然后合并包含的区间，例如[1,6)包含着[1,2] [3,5)，那么删去[1,2] [3,5)两个区间
        List<Pair<Integer, Integer>> filteredMatchIntervals = new ArrayList<>();

        // 第一个匹配的区间必定被选中
        Pair<Integer, Integer> preInterval = matchIntervals.get(0);

        for (int i = 1; i < matchIntervals.size(); i++) {
            Pair<Integer, Integer> nextInterval = matchIntervals.get(i);

            // 若区间完全分离
            if (nextInterval.getFirst() >= preInterval.getSecond()) {
                filteredMatchIntervals.add(preInterval);
                preInterval = nextInterval;
            }

        }

        filteredMatchIntervals.add(preInterval);

        matchIntervals = filteredMatchIntervals;
    }

    @Override
    public String group(int group) {
        if (groupStartIndexes == null) {
            assertNull(groupEndIndexes);
            throw new IllegalStateException("No match found");
        }

        if (!groupStartIndexes.containsKey(group)
                || !groupEndIndexes.containsKey(group)) {
            return null;
        }
        return subInput.substring(
                groupStartIndexes.get(group),
                groupEndIndexes.get(group)
        );
    }
}
