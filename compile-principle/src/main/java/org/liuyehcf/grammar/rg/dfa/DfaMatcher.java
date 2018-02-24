package org.liuyehcf.grammar.rg.dfa;

import org.liuyehcf.grammar.rg.Matcher;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.liuyehcf.grammar.utils.AssertUtils.assertNotNull;

public class DfaMatcher implements Matcher{

    // Dfa自动机
    private final Dfa dfa;

    // 待匹配的输入符号
    private final String input;

    // group i --> 起始索引 的映射表，闭集
    private Map<Integer, Integer> groupStartIndexes = new HashMap<>();

    // group i --> 接收索引 的映射表，闭集
    private Map<Integer, Integer> groupEndIndexes = new HashMap<>();

    DfaMatcher(Dfa dfa, String input) {
        if (dfa == null || input == null) {
            throw new NullPointerException();
        }
        this.dfa = dfa;
        this.input = input;
    }

    @Override
    public boolean matches() {
        DfaState curDfaState = dfa.getStartDfaState();
        assertNotNull(curDfaState);
        for (int i = 0; i < input.length(); i++) {
            for (int group : curDfaState.getGroupStart()) {
                groupStartIndexes.put(group, i);
            }

            for (int group : curDfaState.getGroupReceive()) {
                groupEndIndexes.put(group, i);
            }

            DfaState nextDfaState = curDfaState.getNextDfaStateWithSymbol(
                    SymbolUtils.getAlphabetSymbolWithChar(input.charAt(i))
            );
            if (nextDfaState == null) return false;
            curDfaState = nextDfaState;
        }

        for (int group : curDfaState.getGroupStart()) {
            groupStartIndexes.put(group, input.length());
        }

        for (int group : curDfaState.getGroupReceive()) {
            groupEndIndexes.put(group, input.length());
        }

        Set<Integer> keySets = groupStartIndexes.keySet();
        for (int group : keySets.toArray(new Integer[0])) {
            if (!groupEndIndexes.containsKey(group)) {
                groupStartIndexes.remove(group);
            }
        }
        return curDfaState.canReceive();
    }

    @Override
    public boolean find() {
        return false;
    }

    @Override
    public String group(int group) {
        if (!groupStartIndexes.containsKey(group)
                || !groupEndIndexes.containsKey(group)) {
            return null;
        }
        return input.substring(
                groupStartIndexes.get(group),
                groupEndIndexes.get(group)
        );
    }
}
