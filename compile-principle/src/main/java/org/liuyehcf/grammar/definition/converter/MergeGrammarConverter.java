package org.liuyehcf.grammar.definition.converter;

import org.liuyehcf.grammar.definition.Grammar;

/**
 * 合并具有相同左部的产生式
 */
public class MergeGrammarConverter extends AbstractGrammarConverter {
    public MergeGrammarConverter(Grammar originalGrammar) {
        super(originalGrammar);
    }

    @Override
    protected Grammar doConvert() {
        return null;
    }
}
