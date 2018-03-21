package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.LexicalAnalyzer;
import org.liuyehcf.grammar.cfg.AbstractCfgParser;
import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.converter.AugmentedGrammarConverter;
import org.liuyehcf.grammar.core.definition.converter.GrammarConverterPipelineImpl;
import org.liuyehcf.grammar.core.definition.converter.MergeGrammarConverter;

public class SLRParser extends AbstractCfgParser implements LRParser {

    private SLRParser(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        super(lexicalAnalyzer, originalGrammar, GrammarConverterPipelineImpl
                .builder()
                .registerGrammarConverter(AugmentedGrammarConverter.class)
                //.registerGrammarConverter(StatusExpandGrammarConverter.class)
                .registerGrammarConverter(MergeGrammarConverter.class)
                .build());
    }

    public static LRParser create(LexicalAnalyzer lexicalAnalyzer, Grammar originalGrammar) {
        LRParser parser = new SLRParser(lexicalAnalyzer, originalGrammar);

        parser.init();

        return parser;
    }

    @Override
    protected void postInit() {

    }

    @Override
    public String getClosureJSONString() {
        return null;
    }

    @Override
    public String getAnalysisTableMarkdownString() {
        return null;
    }

    @Override
    public boolean matches(String input) {
        return false;
    }
}
