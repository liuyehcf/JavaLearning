package org.liuyehcf.grammar.rg;

import org.liuyehcf.grammar.GrammarHolder;
import org.liuyehcf.grammar.definition.Grammar;
import org.liuyehcf.grammar.definition.converter.GrammarConverterPipeline;
import org.liuyehcf.grammar.definition.converter.GrammarConverterPipelineImpl;
import org.liuyehcf.grammar.definition.converter.MergeGrammarConverter;
import org.liuyehcf.grammar.definition.converter.SimplificationGrammarConverter;
import org.liuyehcf.grammar.rg.dfa.Dfa;
import org.liuyehcf.grammar.rg.nfa.Nfa;
import org.liuyehcf.grammar.rg.utils.GrammarUtils;

public class RGBuilder implements GrammarHolder {

    // 文法转换流水线
    private static final GrammarConverterPipeline grammarConverterPipeline;

    static {
        grammarConverterPipeline = GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(MergeGrammarConverter.class)
                .registerGrammarConverter(SimplificationGrammarConverter.class)
                .build();
    }

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

    public static RGBuilder compile(Grammar grammar) {

        Grammar convertedGrammar = grammarConverterPipeline.convert(grammar);

        return new RGBuilder(convertedGrammar);
    }

    public static RGBuilder compile(String regex) {

        Grammar grammar = grammarConverterPipeline.convert(
                GrammarUtils.createGrammarWithRegex(regex)
        );

        return new RGBuilder(grammar);
    }

    @Override
    public Grammar getGrammar() {
        return grammar;
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
