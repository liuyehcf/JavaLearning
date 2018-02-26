package org.liuyehcf.grammar.rg.dfa;

import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.rg.nfa.NfaState;

import java.util.*;
import java.util.stream.Collectors;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;
import static org.liuyehcf.grammar.utils.AssertUtils.assertNotNull;

/**
 * Created by Liuye on 2017/10/24.
 */
public class DfaState {

    private static int count = 1;

    private final int id = count++;

    // DfaState描述符，与当前DfaState包含的所有NfaState的id集合相关
    private DfaStateDescription description = new DfaStateDescription(new HashSet<>());

    // 当前DfaState是否被标记过
    private boolean isMarked = false;

    // 当前DfaState包含的所有NfaState
    private Set<NfaState> nfaStates = new HashSet<>();

    // 当前DfaState包含的所有NfaState的所有下一跳输入符号，构建时会用到。当构建完成时，与"nextDfaStateMap.keySet()"一致
    private Set<Symbol> inputSymbols = new HashSet<>();

    // 邻接节点映射表
    private Map<Symbol, DfaState> nextDfaStateMap = new HashMap<>();

    // 当前节点作为 group i 的起始节点，那么i位于groupStart中
    private Set<Integer> groupStart = new HashSet<>();

    // 当前节点作为 group i 的接收节点，那么i位于groupReceive中
    private Set<Integer> groupReceive = new HashSet<>();

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

    public boolean canReceive() {
        return canReceive(0);
    }

    public boolean addNfaState(NfaState nfaState) {
        boolean flag = nfaStates.add(nfaState);
        if (flag) {
            groupStart.addAll(nfaState.getGroupStart());
            groupReceive.addAll(nfaState.getGroupReceive());

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
        description = new DfaStateDescription(
                this.nfaStates.stream().map(NfaState::getId).collect(Collectors.toSet())
        );
    }

    public void print() {
        Set<DfaState> visited = new HashSet<>();

        LinkedList<DfaState> stack = new LinkedList<>();

        stack.push(this);

        List<DfaState> endDfaStates = new ArrayList<>();

        while (!stack.isEmpty()) {
            DfaState curDfaState = stack.pop();
            if (curDfaState.canReceive()) {
                endDfaStates.add(curDfaState);
            }

            for (Symbol inputSymbol : curDfaState.getAllInputSymbols()) {
                DfaState nextDfaState = curDfaState.getNextDfaStateWithSymbol(inputSymbol);

                assertNotNull(nextDfaState);

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
