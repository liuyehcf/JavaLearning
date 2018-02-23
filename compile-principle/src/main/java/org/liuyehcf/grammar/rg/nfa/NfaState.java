package org.liuyehcf.grammar.rg.nfa;


import org.liuyehcf.grammar.core.definition.Symbol;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;

/**
 * Created by Liuye on 2017/10/21.
 */
public class NfaState {
    private static int count = 1;
    private final int id = count++;
    private final List<NfaState> NONE = Collections.unmodifiableList(new ArrayList<>());

    // 当前节点作为 group i 的起始节点，那么i位于groupStart中
    private Set<Integer> groupStart = new HashSet<>();

    // 当前节点作为 group i 的接收节点，那么i位于groupReceive中
    private Set<Integer> groupReceive = new HashSet<>();

    // 输入符号 -> 后继节点集合 的映射表
    private Map<Symbol, List<NfaState>> nextNfaStatesMap = new HashMap<>();

    public int getId() {
        return id;
    }

    public Set<Integer> getGroupStart() {
        return groupStart;
    }

    public Set<Integer> getGroupReceive() {
        return groupReceive;
    }

    public void setStart(int group) {
        assertFalse(groupStart.contains(group));
        groupStart.add(group);
    }

    public boolean isStart(int group) {
        return groupStart.contains(group);
    }

    public void setReceive(int group) {
        assertFalse(groupReceive.contains(group));
        groupReceive.add(group);
    }

    public boolean canReceive(int group) {
        return groupReceive.contains(group);
    }

    public Set<Symbol> getAllInputSymbol() {
        return nextNfaStatesMap.keySet();
    }

    public List<NfaState> getNextNfaStatesWithInputSymbol(Symbol symbol) {
        return nextNfaStatesMap.getOrDefault(symbol, NONE);
    }

    public void addInputSymbolAndNextNfaState(Symbol symbol, NfaState nextNfaState) {
        if (!nextNfaStatesMap.containsKey(symbol)) {
            nextNfaStatesMap.put(symbol, new ArrayList<>());
        }
        nextNfaStatesMap.get(symbol).add(nextNfaState);
    }

    public void cleanConnections() {
        nextNfaStatesMap.clear();
    }

    @Override
    public String toString() {
        return "NfaState[" + id + "]";
    }
}
