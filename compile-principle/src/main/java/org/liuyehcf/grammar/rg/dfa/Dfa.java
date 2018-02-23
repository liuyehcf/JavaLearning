package org.liuyehcf.grammar.rg.dfa;


import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.rg.Matcher;
import org.liuyehcf.grammar.rg.RGParser;
import org.liuyehcf.grammar.rg.nfa.Nfa;
import org.liuyehcf.grammar.rg.nfa.NfaClosure;
import org.liuyehcf.grammar.rg.nfa.NfaState;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;

import java.util.*;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;
import static org.liuyehcf.grammar.utils.AssertUtils.assertTrue;

/**
 * Created by Liuye on 2017/10/21.
 */
public class Dfa implements RGParser {

    private final Nfa nfa;
    private List<DfaState> startDfaStates = new ArrayList<>();

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
    public boolean matches(String input) {
        assertFalse(startDfaStates.isEmpty());
        DfaState curDfaState = startDfaStates.get(0);
        for (int i = 0; i < input.length(); i++) {
            DfaState nextDfaState = curDfaState.getNextDfaStateWithSymbol(
                    SymbolUtils.getAlphabetSymbolWithChar(input.charAt(i))
            );
            if (nextDfaState == null) return false;
            curDfaState = nextDfaState;
        }
        return curDfaState.isCanReceive();
    }

    @Override
    public Grammar getGrammar() {
        return nfa.getGrammar();
    }

    @Override
    public Matcher matcher(String input) {
        return new DfaMatcher(this, input);
    }

    @Override
    public void print() {
        assertFalse(startDfaStates.isEmpty());
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

        private Transfer(NfaClosure nfaClosure) {
            this.nfaClosure = nfaClosure;
            init();
        }

        private static DfaState getStartDfaStateFromNfaClosure(NfaClosure nfaClosure) {
            return new Transfer(nfaClosure).getStartDfaState();
        }

        private DfaState getStartDfaState() {
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
            assertFalse(dfaState.isMarked());
            assertFalse(dfaStatesMap.containsKey(dfaState.getDescription()));
            dfaStatesMap.put(dfaState.getDescription(), dfaState);
            assertFalse(unMarkedDfaStates.contains(dfaState));
            unMarkedDfaStates.add(dfaState);
        }

        private DfaState getUnMarkedDfaState() {
            Iterator<DfaState> it = unMarkedDfaStates.iterator();

            DfaState dfaState = null;
            if (it.hasNext()) {
                dfaState = it.next();
                assertFalse(dfaState.isMarked());
            }
            return dfaState;
        }

        private void markDfaState(DfaState dfaState) {
            assertFalse(dfaState.isMarked());
            dfaState.setMarked();
            assertTrue(unMarkedDfaStates.contains(dfaState));
            unMarkedDfaStates.remove(dfaState);
            assertFalse(markedDfaStates.contains(dfaState));
            markedDfaStates.add(dfaState);
        }
    }
}
