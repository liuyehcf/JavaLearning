package org.liuyehcf.grammar.rg.dfa;

import org.liuyehcf.grammar.rg.Matcher;
import org.liuyehcf.grammar.rg.utils.SymbolUtils;

import static org.liuyehcf.grammar.utils.AssertUtils.assertNotNull;

public class DfaMatcher implements Matcher{

    private final Dfa dfa;

    private final String input;

    DfaMatcher(Dfa dfa, String input) {
        this.dfa = dfa;
        this.input = input;
    }

    @Override
    public boolean matches() {
        DfaState curDfaState = dfa.getStartDfaState();
        assertNotNull(curDfaState);
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
    public boolean find() {
        return false;
    }

    @Override
    public String group(int group) {
        return null;
    }
}
