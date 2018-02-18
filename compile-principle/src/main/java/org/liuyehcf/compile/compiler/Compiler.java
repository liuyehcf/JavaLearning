package org.liuyehcf.compile.compiler;

public interface Compiler {
    /**
     * 给定字符串是否为当前文法的句子
     *
     * @param sequence
     * @return
     */
    boolean isSentence(String sequence);
}
