package org.liuyehcf.grammar.rg.utils;


import org.liuyehcf.grammar.definition.Grammar;
import org.liuyehcf.grammar.rg.RGParser;
import org.liuyehcf.grammar.rg.dfa.Dfa;
import org.liuyehcf.grammar.rg.nfa.Nfa;

/**
 * Created by Liuye on 2017/10/25.
 */
public class RegexAutoMachine {
    private Nfa nfa;
    private Dfa dfa;

    private RegexAutoMachine(Nfa nfa, Dfa dfa) {
        this.nfa = nfa;
        this.dfa = dfa;
    }

    public static RegexAutoMachine compile(String regex) {
        Grammar grammar = GrammarUtils.createGrammarWithRegex(regex);

        Nfa nfa = new Nfa(grammar);
        Dfa dfa = new Dfa(nfa);

        RegexAutoMachine autoMachine = new RegexAutoMachine(nfa, dfa);

        return autoMachine;
    }

    public RGParser getNfaMatcher() {
        return nfa;
    }

    public RGParser getDfaMatcher() {
        return dfa;
    }

}
