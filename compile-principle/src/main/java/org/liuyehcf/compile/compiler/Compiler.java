package org.liuyehcf.compile.compiler;

import org.liuyehcf.compile.definition.Grammar;

public interface Compiler {
    /**
     * 给定字符串是否为当前文法的句子
     *
     * @param sequence
     * @return
     */
    boolean isSentence(String sequence);

    /**
     * 获取Grammar
     *
     * @return
     */
    Grammar getGrammar();
}
