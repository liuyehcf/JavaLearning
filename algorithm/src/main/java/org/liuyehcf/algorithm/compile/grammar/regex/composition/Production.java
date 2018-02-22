package org.liuyehcf.algorithm.compile.grammar.regex.composition;

import org.liuyehcf.algorithm.compile.grammar.regex.symbol.Symbol;

/**
 * Created by Liuye on 2017/10/21.
 */
public class Production {
    private final Symbol left;
    private final PrimeProduction right;

    public Production(Symbol left,
                      PrimeProduction right) {
        this.left = left;
        this.right = right;
    }

    public Symbol getLeft() {
        return left;
    }

    public PrimeProduction getRight() {
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

