package org.liuyehcf.grammar.rg.nfa;

import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.rg.Matcher;
import org.liuyehcf.grammar.rg.RGParser;
import org.liuyehcf.grammar.rg.utils.GrammarUtils;

import java.util.List;

import static org.liuyehcf.grammar.rg.nfa.NfaBuildIterator.createNfaClosuresMap;
import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;
import static org.liuyehcf.grammar.utils.AssertUtils.assertNotNull;


/**
 * Created by Liuye on 2017/10/21.
 */
public class Nfa implements RGParser {

    // 正则文法
    private final Grammar grammar;

    // 每个group对应的NfaClosure
    private List<NfaClosure> groupNfaClosures;

    public Nfa(Grammar grammar) {
        this.grammar = grammar;
        init();
    }

    public List<NfaClosure> getGroupNfaClosures() {
        return groupNfaClosures;
    }

    NfaClosure getWholeNfaClosure() {
        assertFalse(groupNfaClosures.isEmpty());
        return groupNfaClosures.get(0);
    }

    private void init() {
        groupNfaClosures = createNfaClosuresMap(
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

    @Override
    public void print() {
        assertNotNull(getWholeNfaClosure());
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
