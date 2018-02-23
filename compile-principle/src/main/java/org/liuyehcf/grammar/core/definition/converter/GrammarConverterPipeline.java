package org.liuyehcf.grammar.core.definition.converter;

import org.liuyehcf.grammar.core.definition.Grammar;

public interface GrammarConverterPipeline {
    Grammar convert(Grammar grammar);
}
