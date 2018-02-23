package org.liuyehcf.grammar.core.definition.converter;

import org.liuyehcf.grammar.core.definition.Grammar;

public interface GrammarConverter {
    /**
     * 返回转换后的文法
     */
    Grammar getConvertedGrammar();
}
