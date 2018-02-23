package org.liuyehcf.grammar.rg;

import org.liuyehcf.grammar.Parser;

public interface RGParser extends Parser {
    boolean find();

    String group(int group);

    void print();

    void printAllGroup();
}
