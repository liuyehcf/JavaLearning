package org.liuyehcf.grammar.rg.nfa;

import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.rg.Matcher;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;
import org.liuyehcf.grammar.utils.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.liuyehcf.grammar.utils.AssertUtils.assertNull;

public class NfaMatcher implements Matcher {

    // Nfa自动机
    private final Nfa nfa;

    // 待匹配的输入字符串
    private final String input;

    // group i --> 起始索引 的映射表，闭集
    private Map<Integer, Integer> groupStartIndexes = null;

    // group i --> 接收索引 的映射表，闭集
    private Map<Integer, Integer> groupEndIndexes = null;

    // 剩余未匹配的起始索引
    private int firstOfRemain = 0;

    // 目前进行匹配操作的子串
    private String subInput;

    NfaMatcher(Nfa nfa, String input) {
        if (nfa == null || input == null) {
            throw new NullPointerException();
        }
        this.nfa = nfa;
        this.input = input;
    }

    public static void main(String[] args) {
        System.out.println("".substring(0, 0));
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

        // 首先走非ε边，贪婪模式
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
        // todo 非贪婪模式
        int index = firstOfRemain;
        try {
            while (index < input.length()) {
                for (int i = firstOfRemain; i <= index; i++) {
                    if (doMatch(input.substring(i, index + 1))) {
                        return true;
                    }
                }
                index++;
            }

            if (index == input.length() &&
                    "".equals(input)) {
                return matches();
            }

            return false;
        } finally {
            firstOfRemain = index + 1;
        }
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
