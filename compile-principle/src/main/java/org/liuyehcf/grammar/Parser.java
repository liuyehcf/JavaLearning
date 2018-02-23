package org.liuyehcf.grammar;

public interface Parser extends GrammarHolder {
    /**
     * 给定字符串是否为当前文法的句子
     */
    boolean matches(String input);
}
