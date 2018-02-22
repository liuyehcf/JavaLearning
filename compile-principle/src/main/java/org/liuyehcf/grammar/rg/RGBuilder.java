package org.liuyehcf.grammar.rg;

import org.liuyehcf.grammar.definition.Grammar;
import org.liuyehcf.grammar.definition.converter.MergeGrammarConverter;
import org.liuyehcf.grammar.definition.converter.SimplificationGrammarConverter;
import org.liuyehcf.grammar.rg.dfa.Dfa;
import org.liuyehcf.grammar.rg.nfa.Nfa;
import org.liuyehcf.grammar.rg.utils.GrammarUtils;

public class RGBuilder {

    // 正则文法
    private final Grammar grammar;

    // nfa自动机
    private Nfa nfa;

    // dfa自动机
    private Dfa dfa;

    private RGBuilder(Grammar grammar) {
        this.grammar = grammar;
        this.nfa = null;
        this.dfa = null;
    }

    public static RGBuilder compile(String regex) {

        Grammar grammar = new SimplificationGrammarConverter(
                new MergeGrammarConverter(
                        GrammarUtils.createGrammarWithRegex(regex)
                ).getConvertedGrammar()
        ).getConvertedGrammar();

        return new RGBuilder(grammar);
    }

    public RGParser buildNfa() {
        if (nfa == null) {
            nfa = new Nfa(grammar);
        }
        return nfa;
    }

    public RGParser buildDfa() {
        if (dfa == null) {
            if (nfa == null) {
                buildNfa();
            }
            dfa = new Dfa(nfa);
        }
        return dfa;
    }
}
