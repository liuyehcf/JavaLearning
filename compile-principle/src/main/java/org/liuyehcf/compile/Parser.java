package org.liuyehcf.compile;

import org.liuyehcf.compile.definition.Grammar;

public interface Parser {
    /**
     * 给定字符串是否为当前文法的句子
     */
    boolean isMatch(String expression);

    /**
     * 获取Grammar
     **/
    Grammar getGrammar();
}
