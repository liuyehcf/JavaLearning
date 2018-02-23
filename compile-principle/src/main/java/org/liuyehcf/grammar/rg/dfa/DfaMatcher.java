package org.liuyehcf.grammar.rg.dfa;

import org.liuyehcf.grammar.rg.Matcher;

public class DfaMatcher implements Matcher{

    private final Dfa dfa;

    private final String input;

    DfaMatcher(Dfa dfa, String input) {
        this.dfa = dfa;
        this.input = input;
    }

    @Override
    public boolean matches() {
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
