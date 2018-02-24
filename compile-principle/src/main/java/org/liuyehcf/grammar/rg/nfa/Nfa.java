package org.liuyehcf.grammar.rg.nfa;

import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.rg.Matcher;
import org.liuyehcf.grammar.rg.RGParser;
import org.liuyehcf.grammar.rg.utils.GrammarUtils;

import static org.liuyehcf.grammar.rg.nfa.NfaBuildIterator.createNfaClosure;
import static org.liuyehcf.grammar.utils.AssertUtils.assertNotNull;


/**
 * Created by Liuye on 2017/10/21.
 */
public class Nfa implements RGParser {

    // 正则文法
    private final Grammar grammar;

    // NfaClosure
    private NfaClosure nfaClosure;

    public Nfa(Grammar grammar) {
        this.grammar = grammar;
        init();
    }

    public NfaClosure getNfaClosure() {
        return nfaClosure;
    }

    private void init() {
        nfaClosure = createNfaClosure(
                GrammarUtils.extractSymbolsFromGrammar(grammar));
    }

    @Override
    public boolean matches(String input) {
        return matcher(input).matches();
    }

    @Override
    public Grammar getGrammar() {
        return grammar;
    }

    @Override
    public Matcher matcher(String input) {
        return new NfaMatcher(this, input);
    }

    public void print() {
        assertNotNull(nfaClosure);
        nfaClosure.print();
    }
}
