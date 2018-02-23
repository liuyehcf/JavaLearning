package org.liuyehcf.grammar.core.definition.converter;

import org.liuyehcf.grammar.core.definition.Grammar;

public abstract class AbstractGrammarConverter implements GrammarConverter {
    // 待转换的文法
    protected final Grammar originalGrammar;

    // 转换后的文法
    protected Grammar convertedGrammar;

    public AbstractGrammarConverter(Grammar originalGrammar) {
        this.originalGrammar = originalGrammar;
    }

    @Override
    public final Grammar getConvertedGrammar() {
        if (convertedGrammar == null) {
            convertedGrammar = doConvert();
        }
        return convertedGrammar;
    }

    protected abstract Grammar doConvert();
}
