package org.liuyehcf.grammar.rg.dfa;

import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.rg.nfa.NfaState;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;

/**
 * Created by Liuye on 2017/10/24.
 */
public class DfaState {
    private static int count = 1;

    private final int id = count++;

    private DfaStateDescription description = new DfaStateDescription("");

    private boolean isMarked = false;

    private Set<NfaState> nfaStates = new HashSet<>();

    private Set<Symbol> inputSymbols = new HashSet<>();

    private Map<Symbol, DfaState> nextDfaStateMap = new HashMap<>();

    private boolean canReceive = false;

    public static DfaState createDfaStateWithNfaStates(List<NfaState> nfaStates) {
        LinkedList<NfaState> stack = new LinkedList<>();
        stack.addAll(nfaStates);

        DfaState dfaState = new DfaState();
        dfaState.addNfaStates(nfaStates);

        while (!stack.isEmpty()) {
            NfaState nfaState = stack.pop();

            for (NfaState nextNfaState : nfaState.getNextNfaStatesWithInputSymbol(Symbol.EPSILON)) {
                if (dfaState.addNfaState(nextNfaState)) {
                    stack.push(nextNfaState);
                }
            }
        }

        dfaState.setDescription();

        return dfaState;
    }

    public DfaStateDescription getDescription() {
        return description;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked() {
        isMarked = true;
    }

    public boolean isCanReceive() {
        return canReceive;
    }

    public boolean addNfaState(NfaState nfaState) {
        boolean flag = nfaStates.add(nfaState);
        if (flag) {
            if (nfaState.canReceive(0)) { //todo
                canReceive = true;
            }
            inputSymbols.addAll(nfaState.getAllInputSymbol());
            inputSymbols.remove(Symbol.EPSILON);
        }
        return flag;
    }

    public void addNfaStates(List<NfaState> nfaStates) {
        for (NfaState nfaState : nfaStates) {
            addNfaState(nfaState);
        }
    }

    public Set<Symbol> getAllInputSymbols() {
        return inputSymbols;
    }

    public List<NfaState> getNextNfaStatesWithInputSymbol(Symbol inputSymbol) {
        List<NfaState> nextNfaStates = new ArrayList<>();
        for (NfaState nfaState : nfaStates) {
            nextNfaStates.addAll(nfaState.getNextNfaStatesWithInputSymbol(inputSymbol));
        }
        return nextNfaStates;
    }

    public void addInputSymbolAndNextDfaState(Symbol symbol, DfaState dfaState) {
        assertFalse(nextDfaStateMap.containsKey(symbol));
        nextDfaStateMap.put(symbol, dfaState);
    }

    public DfaState getNextDfaStateWithSymbol(Symbol symbol) {
        if (!nextDfaStateMap.containsKey(symbol)) {
            return null;
        }
        return nextDfaStateMap.get(symbol);
    }

    @Override
    public String toString() {
        return "DfaState[" + id + "]";
    }

    public void setDescription() {
        List<NfaState> nfaStates = new ArrayList<>(this.nfaStates);
        Collections.sort(nfaStates, new Comparator<NfaState>() {
            @Override
            public int compare(NfaState o1, NfaState o2) {
                return o1.getId() - o2.getId();
            }
        });
        description = new DfaStateDescription(nfaStates.toString());
    }

    public void print() {
        Set<DfaState> visited = new HashSet<>();

        LinkedList<DfaState> stack = new LinkedList<>();

        stack.push(this);

        List<DfaState> endDfaStates = new ArrayList<>();

        while (!stack.isEmpty()) {
            DfaState curDfaState = stack.pop();
            if (curDfaState.isCanReceive()) {
                endDfaStates.add(curDfaState);
            }

            for (Symbol inputSymbol : curDfaState.getAllInputSymbols()) {
                DfaState nextDfaState = curDfaState.getNextDfaStateWithSymbol(inputSymbol);

                System.out.println(curDfaState + " (" + inputSymbol + ")-> " + nextDfaState);

                if (visited.add(nextDfaState)) {
                    stack.push(nextDfaState);
                }
            }
        }

        System.out.print("EndState: ");
        endDfaStates.forEach(dfaState -> {
            System.out.print(dfaState + ", ");
        });
        System.out.println("\n");
    }

    @Override
    public int hashCode() {
        return this.description.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DfaState) {
            DfaState other = (DfaState) obj;
            return other.description.equals(this.description);
        }
        return false;
    }
}
