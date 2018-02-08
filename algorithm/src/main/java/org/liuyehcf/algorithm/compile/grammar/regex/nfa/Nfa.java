package org.liuyehcf.algorithm.compile.grammar.regex.nfa;

import org.liuyehcf.algorithm.compile.grammar.regex.composition.GrammarDefinition;
import org.liuyehcf.algorithm.compile.grammar.regex.composition.Matcher;
import org.liuyehcf.algorithm.compile.grammar.regex.composition.SymbolString;
import org.liuyehcf.algorithm.compile.grammar.regex.symbol.Symbol;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.liuyehcf.algorithm.compile.grammar.regex.nfa.NfaBuildIterator.createNfaClosuresMap;

/**
 * Created by Liuye on 2017/10/21.
 */
public class Nfa implements Matcher {
    private final GrammarDefinition grammar;

    private List<NfaClosure> groupNfaClosures;

    public Nfa(GrammarDefinition grammar) {
        this.grammar = grammar;
        init();
    }

    public List<NfaClosure> getGroupNfaClosures() {
        return groupNfaClosures;
    }

    private NfaClosure getWholeNfaClosure() {
        assert !groupNfaClosures.isEmpty();
        return groupNfaClosures.get(0);
    }

    private void init() {
        SymbolString finalSymbolString = grammar.getFinalSymbolString();
        List<Symbol> symbols = finalSymbolString.getSymbols();

        groupNfaClosures = createNfaClosuresMap(symbols);
    }

    @Override
    public boolean isMatch(String s) {
        NfaState curNfaState = getWholeNfaClosure().getStartNfaState();

        Set<String> visitedNfaState = new HashSet<>();

        return isMatchDfs(curNfaState, s, 0, visitedNfaState);
    }

    private boolean isMatchDfs(NfaState curNfaState, String s, int index, Set<String> visitedNfaState) {
        List<NfaState> epsilonNextStates = curNfaState.getNextNfaStatesWithInputSymbol(
                Symbol._Epsilon
        );
        for (NfaState nextState : epsilonNextStates) {
            String curStateString = nextState.toString() + index + Symbol._Epsilon;
            if (visitedNfaState.add(curStateString)) {
                if (isMatchDfs(nextState, s, index, visitedNfaState))
                    return true;
                visitedNfaState.remove(curStateString);
            }
        }

        if (index == s.length()) {
            return curNfaState.isCanReceive();
        }

        List<NfaState> nextStates = curNfaState.getNextNfaStatesWithInputSymbol(
                Symbol.getAlphabetSymbolWithChar(s.charAt(index)));

        for (NfaState nextState : nextStates) {

            if (isMatchDfs(nextState, s, index + 1, visitedNfaState))
                return true;
        }

        return false;
    }

    @Override
    public void print() {
        assert getWholeNfaClosure() != null;
        getWholeNfaClosure().print();
    }

    @Override
    public void printAllGroup() {
        for (int group = 0; group < groupNfaClosures.size(); group++) {
            System.out.println("Group [" + group + "]");
            groupNfaClosures.get(group).print();

            System.out.println("\n--------------\n");
        }
    }
}
