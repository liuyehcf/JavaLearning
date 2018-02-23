package org.liuyehcf.grammar.rg.nfa;

import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.rg.Matcher;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NfaMatcher implements Matcher {

    private final Nfa nfa;

    private final String input;


    NfaMatcher(Nfa nfa, String input) {
        this.nfa = nfa;
        this.input = input;

        init();
    }

    private void init() {

    }

    @Override
    public boolean matches() {
        NfaState curNfaState = nfa.getNfaClosure().getStartNfaState();

        Set<String> visitedNfaState = new HashSet<>();

        return isMatchDfs(curNfaState, input, 0, visitedNfaState);
    }

    private boolean isMatchDfs(NfaState curNfaState, String s, int index, Set<String> visitedNfaState) {
        // 从当前节点出发，经过ε边的后继节点集合
        List<NfaState> epsilonNextStates = curNfaState.getNextNfaStatesWithInputSymbol(
                Symbol.EPSILON
        );
        for (NfaState nextState : epsilonNextStates) {
            String curStateString = nextState.toString() + index + Symbol.EPSILON;
            if (visitedNfaState.add(curStateString)) {
                if (isMatchDfs(nextState, s, index, visitedNfaState))
                    return true;
                visitedNfaState.remove(curStateString);
            }
        }

        if (index == s.length()) {
            return curNfaState.canReceive(0);
        }

        // 从当前节点出发，经过非ε边的next节点集合
        List<NfaState> nextStates = curNfaState.getNextNfaStatesWithInputSymbol(
                SymbolUtils.getAlphabetSymbolWithChar(s.charAt(index)));

        for (NfaState nextState : nextStates) {

            if (isMatchDfs(nextState, s, index + 1, visitedNfaState))
                return true;
        }

        return false;
    }


    @Override
    public boolean find() {
        return false;
    }

    @Override
    public String group(int group) {
        return null;
    }
}
