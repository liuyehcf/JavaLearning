package org.liuyehcf.compile.grammar.regex.nfa;

import org.liuyehcf.compile.grammar.regex.symbol.Symbol;

import java.util.*;

/**
 * Created by Liuye on 2017/10/23.
 */
public class NfaClosure {
    private static int count = 1;
    private final int id = count++;
    private final NfaState startNfaState;
    private List<NfaState> endNfaStates;
    private int group;
    private NfaClosure clonedNfaClosure;

    public NfaState getStartNfaState() {
        return startNfaState;
    }

    public List<NfaState> getEndNfaStates() {
        return endNfaStates;
    }

    public void setEndNfaStates(List<NfaState> endNfaStates) {
        this.endNfaStates = endNfaStates;
    }

    public NfaClosure(NfaState startNfaState, List<NfaState> endNfaStates, int initialGroup) {
        if (startNfaState == null) throw new RuntimeException();
        this.startNfaState = startNfaState;
        this.endNfaStates = endNfaStates;
        this.group = initialGroup;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
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

    public NfaClosure clone() {
        if (clonedNfaClosure != null) {
            return clonedNfaClosure;
        }

        Map<NfaState, NfaState> oldAndNewNfaStateMap = new HashMap<>();

        dfsSearch(getStartNfaState(), oldAndNewNfaStateMap);
        copy(oldAndNewNfaStateMap);

        return clonedNfaClosure;
    }

    private void dfsSearch(NfaState curNfaState, Map<NfaState, NfaState> oldAndNewNfaStateMap) {
        if (oldAndNewNfaStateMap.containsKey(curNfaState)) return;
        oldAndNewNfaStateMap.put(curNfaState, new NfaState());

        for (Symbol inputSymbol : curNfaState.getAllInputSymbol()) {
            for (NfaState nextNfaState : curNfaState.getNextNfaStatesWithInputSymbol(inputSymbol)) {
                dfsSearch(nextNfaState, oldAndNewNfaStateMap);
            }
        }
    }

    private void copy(Map<NfaState, NfaState> oldAndNewNfaStateMap) {
        for (Map.Entry<NfaState, NfaState> entry : oldAndNewNfaStateMap.entrySet()) {
            NfaState curNfaState = entry.getKey();

            NfaState clonedCurNfaState = entry.getValue();
            for (Symbol inputSymbol : curNfaState.getAllInputSymbol()) {
                for (NfaState nextNfaState : curNfaState.getNextNfaStatesWithInputSymbol(inputSymbol)) {
                    NfaState clonedNextNfaState = oldAndNewNfaStateMap.get(nextNfaState);
                    assert clonedCurNfaState != null;
                    clonedCurNfaState.addInputSymbolAndNextNfaState(inputSymbol, clonedNextNfaState);
                }
            }
        }


        NfaState clonedStartNfaState = oldAndNewNfaStateMap.get(startNfaState);
        List<NfaState> clonedEndNfaStates = new ArrayList<>();

        for (NfaState endNfaState : endNfaStates) {
            NfaState copiedEndNfaState = oldAndNewNfaStateMap.get(endNfaState);
            clonedEndNfaStates.add(copiedEndNfaState);
            copiedEndNfaState.setCanReceive();
        }

        clonedNfaClosure = new NfaClosure(clonedStartNfaState, clonedEndNfaStates, group);
    }

    static NfaClosure getEmptyClosureForGroup(int group) {
        NfaState startNfaState = new NfaState();
        List<NfaState> endNfaStates = new ArrayList<>();
        startNfaState.addInputSymbolAndNextNfaState(
                Symbol._Epsilon, startNfaState
        );
        endNfaStates.add(startNfaState);
        NfaClosure nfaClosure = new NfaClosure(
                startNfaState,
                endNfaStates,
                group);
        return nfaClosure;
    }

    @Override
    public String toString() {
        return "NfaClosure[" + id + "]";
    }
}
