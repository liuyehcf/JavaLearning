package org.liuyehcf.grammar.rg.nfa;

import org.liuyehcf.grammar.core.definition.Symbol;

import java.util.*;

/**
 * Created by Liuye on 2017/10/23.
 */
public class NfaClosure {
    private static int count = 1;
    private final int id = count++;

    // 起始节点
    private final NfaState startNfaState;

    // 结束节点集合
    private List<NfaState> endNfaStates;

    // 所在的group
    private int group;

    public NfaClosure(NfaState startNfaState, List<NfaState> endNfaStates, int initialGroup) {
        if (startNfaState == null) throw new RuntimeException();
        this.startNfaState = startNfaState;
        this.endNfaStates = endNfaStates;
        this.group = initialGroup;
    }

    static NfaClosure getEmptyClosureForGroup(int group) {
        NfaState startNfaState = new NfaState();
        List<NfaState> endNfaStates = new ArrayList<>();
        startNfaState.addInputSymbolAndNextNfaState(
                Symbol.EPSILON, startNfaState
        );
        endNfaStates.add(startNfaState);
        NfaClosure nfaClosure = new NfaClosure(
                startNfaState,
                endNfaStates,
                group);
        return nfaClosure;
    }

    public NfaState getStartNfaState() {
        return startNfaState;
    }

    public List<NfaState> getEndNfaStates() {
        return endNfaStates;
    }

    public void setEndNfaStates(List<NfaState> endNfaStates) {
        this.endNfaStates = endNfaStates;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public void setStartAndReceive(int group) {
        // 设置起始节点
        startNfaState.setStart(group);

        for(NfaState endNfaState:endNfaStates){
            endNfaState.setReceive(group);
        }
    }

    public void print() {
        Set<NfaState> visited = new HashSet<>();

        LinkedList<NfaState> stack = new LinkedList<>();

        if (getStartNfaState() != null)
            stack.push(getStartNfaState());

        while (!stack.isEmpty()) {
            NfaState curNfaState = stack.pop();

            for (Symbol inputSymbol : curNfaState.getAllInputSymbol()) {
                for (NfaState nextNfaState : curNfaState.getNextNfaStatesWithInputSymbol(inputSymbol)) {
                    System.out.println(curNfaState + " (" + inputSymbol + ")-> " + nextNfaState);

                    if (visited.add(nextNfaState)) {
                        stack.push(nextNfaState);
                    }
                }
            }
        }

        System.out.print("EndStates: ");
        getEndNfaStates().forEach(nfaState -> {
            System.out.print(nfaState + ", ");
        });
        System.out.println("\n");
    }

    @Override
    public String toString() {
        return "NfaClosure[" + id + "]";
    }
}
