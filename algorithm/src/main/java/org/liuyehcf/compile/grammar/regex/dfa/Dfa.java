package org.liuyehcf.compile.grammar.regex.dfa;

import org.liuyehcf.compile.grammar.regex.composition.Matcher;
import org.liuyehcf.compile.grammar.regex.nfa.Nfa;
import org.liuyehcf.compile.grammar.regex.nfa.NfaClosure;
import org.liuyehcf.compile.grammar.regex.nfa.NfaState;
import org.liuyehcf.compile.grammar.regex.symbol.Symbol;

import java.util.*;

/**
 * Created by Liuye on 2017/10/21.
 */
public class Dfa implements Matcher {

    private final Nfa nfa;
    List<DfaState> startDfaStates = new ArrayList<>();

    public Dfa(Nfa nfa) {
        this.nfa = nfa;
        init();
    }

    private void init() {
        for (NfaClosure nfaClosure : nfa.getGroupNfaClosures()) {
            startDfaStates.add(Transfer.getStartDfaStateFromNfaClosure(nfaClosure));
        }
    }

    @Override
    public boolean isMatch(String s) {
        assert !startDfaStates.isEmpty();
        DfaState curDfaState = startDfaStates.get(0);
        for (int i = 0; i < s.length(); i++) {
            DfaState nextDfaState = curDfaState.getNextDfaStateWithSymbol(
                    Symbol.getAlphabetSymbolWithChar(s.charAt(i))
            );
            if (nextDfaState == null) return false;
            curDfaState = nextDfaState;
        }
        return curDfaState.isCanReceive();
    }

    @Override
    public void print() {
        assert !startDfaStates.isEmpty();
        startDfaStates.get(0).print();
    }

    @Override
    public void printAllGroup() {
        for (int group = 0; group < startDfaStates.size(); group++) {
            System.out.println("Group [" + group + "]");
            startDfaStates.get(group).print();

            System.out.println("\n--------------\n");
        }
    }

    private static class Transfer {
        private final NfaClosure nfaClosure;
        private DfaState startDfaState;

        private Map<DfaStateDescription, DfaState> dfaStatesMap = new HashMap<>();
        private Set<DfaState> markedDfaStates = new HashSet<>();
        private Set<DfaState> unMarkedDfaStates = new HashSet<>();

        public Transfer(NfaClosure nfaClosure) {
            this.nfaClosure = nfaClosure;
            init();
        }

        public static DfaState getStartDfaStateFromNfaClosure(NfaClosure nfaClosure) {
            return new Transfer(nfaClosure).getStartDfaState();
        }

        public DfaState getStartDfaState() {
            return startDfaState;
        }

        private void init() {
            addFirstUnMarkedDfaState(
                    DfaState.createDfaStateWithNfaStates(
                            Arrays.asList(nfaClosure.getStartNfaState())));

            DfaState curDfaState;
            while ((curDfaState = getUnMarkedDfaState()) != null) {

                markDfaState(curDfaState);

                for (Symbol inputSymbol : curDfaState.getAllInputSymbols()) {
                    List<NfaState> nextNfaStates = curDfaState.getNextNfaStatesWithInputSymbol(inputSymbol);

                    DfaState nextDfaState = DfaState.createDfaStateWithNfaStates(nextNfaStates);

                    DfaStateDescription nextDfaStateDescription = nextDfaState.getDescription();
                    if (!dfaStatesMap.containsKey(nextDfaStateDescription)) {
                        addUnMarkedDfaState(nextDfaState);
                    } else {
                        nextDfaState = dfaStatesMap.get(nextDfaStateDescription);
                    }

                    curDfaState.addInputSymbolAndNextDfaState(inputSymbol, nextDfaState);
                }
            }
        }

        private void addFirstUnMarkedDfaState(DfaState dfaState) {
            startDfaState = dfaState;
            addUnMarkedDfaState(dfaState);
        }

        private void addUnMarkedDfaState(DfaState dfaState) {
            assert !dfaState.isMarked();
            assert !dfaStatesMap.containsKey(dfaState.getDescription());
            dfaStatesMap.put(dfaState.getDescription(), dfaState);
            assert unMarkedDfaStates.add(dfaState);
        }

        private DfaState getUnMarkedDfaState() {
            Iterator<DfaState> it = unMarkedDfaStates.iterator();

            DfaState dfaState = null;
            if (it.hasNext()) {
                dfaState = it.next();
                assert !dfaState.isMarked();
            }
            return dfaState;
        }

        private void markDfaState(DfaState dfaState) {
            assert !dfaState.isMarked();
            dfaState.setMarked();
            assert unMarkedDfaStates.remove(dfaState);
            assert markedDfaStates.add(dfaState);
        }
    }
}
