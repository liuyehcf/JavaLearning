package org.liuyehcf.algorithm.compile.grammar.regex.composition;

/**
 * Created by Liuye on 2017/10/25.
 */
public interface Matcher {
    boolean isMatch(String s);

    void print();

    void printAllGroup();
}
