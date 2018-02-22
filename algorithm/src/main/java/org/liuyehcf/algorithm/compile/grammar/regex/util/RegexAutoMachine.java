package org.liuyehcf.algorithm.compile.grammar.regex.util;

import org.liuyehcf.algorithm.compile.grammar.regex.composition.Grammar;
import org.liuyehcf.algorithm.compile.grammar.regex.composition.Matcher;
import org.liuyehcf.algorithm.compile.grammar.regex.dfa.Dfa;
import org.liuyehcf.algorithm.compile.grammar.regex.nfa.Nfa;

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
        Grammar grammar = Grammar.createGrammarDefinitionOfNormalRegex(regex);

        Nfa nfa = new Nfa(grammar);
        Dfa dfa = new Dfa(nfa);

        RegexAutoMachine autoMachine = new RegexAutoMachine(nfa, dfa);

        return autoMachine;
    }

    public Matcher getNfaMatcher() {
        return nfa;
    }

    public Matcher getDfaMatcher() {
        return dfa;
    }

}
