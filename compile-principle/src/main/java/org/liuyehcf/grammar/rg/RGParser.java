package org.liuyehcf.grammar.rg;

import org.liuyehcf.grammar.Parser;

public interface RGParser extends Parser {
    /**
     * 返回一个Matcher对象
     */
    Matcher matcher(String input);

    void print();

    void printAllGroup();
}
