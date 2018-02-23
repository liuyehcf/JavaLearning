package org.liuyehcf.grammar.definition.converter;

import org.liuyehcf.grammar.definition.Grammar;

public interface GrammarConverterPipeline {
    Grammar convert(Grammar grammar);
}
