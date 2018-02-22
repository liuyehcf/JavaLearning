package org.liuyehcf.algorithm.compile.grammar.regex.composition;

import org.liuyehcf.algorithm.compile.grammar.regex.symbol.Symbol;

/**
 * Created by Liuye on 2017/10/21.
 */
public class Production {
    private final Symbol left;
    private final PrimaryProduction right;

    public Production(Symbol left,
                      PrimaryProduction right) {
        this.left = left;
        this.right = right;
    }

    public Symbol getLeft() {
        return left;
    }

    public PrimaryProduction getRight() {
        return right;
    }

    @Override
    public String toString() {
        String s = "";
        s += left.toString();
        s += " --> ";
        s += right.toString();
        s += "\n";
        return s;
    }
}

