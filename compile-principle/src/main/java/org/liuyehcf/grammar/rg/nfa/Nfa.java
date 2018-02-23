package org.liuyehcf.grammar.rg.nfa;

import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.rg.RGParser;
import org.liuyehcf.grammar.rg.utils.GrammarUtils;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.liuyehcf.grammar.rg.nfa.NfaBuildIterator.createNfaClosuresMap;


/**
 * Created by Liuye on 2017/10/21.
 */
public class Nfa implements RGParser {
    private final Grammar grammar;

    private List<NfaClosure> groupNfaClosures;

    public Nfa(Grammar grammar) {
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
        groupNfaClosures = createNfaClosuresMap(
                GrammarUtils.extractSymbolsFromGrammar(grammar));
    }

    @Override
    public boolean isMatch(String s) {
        NfaState curNfaState = getWholeNfaClosure().getStartNfaState();

        Set<String> visitedNfaState = new HashSet<>();

        return isMatchDfs(curNfaState, s, 0, visitedNfaState);
    }

    @Override
    public Grammar getGrammar() {
        return null;
    }

    @Override
    public boolean find() {
        return false;
    }

    @Override
    public String group(int group) {
        return null;
    }

    private boolean isMatchDfs(NfaState curNfaState, String s, int index, Set<String> visitedNfaState) {
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
            return curNfaState.isCanReceive();
        }

        List<NfaState> nextStates = curNfaState.getNextNfaStatesWithInputSymbol(
                SymbolUtils.getAlphabetSymbolWithChar(s.charAt(index)));

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
