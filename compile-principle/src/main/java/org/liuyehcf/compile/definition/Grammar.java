package org.liuyehcf.compile.definition;

import java.util.Arrays;
import java.util.List;

/**
 * 文法定义
 */
public class Grammar {

    // 文法包含的所有产生式
    final private List<Production> productions;

    public Grammar(Production... productions) {
        this.productions = Arrays.asList(productions);
    }

    public Grammar(List<Production> productions) {
        this.productions = productions;
    }

    public List<Production> getProductions() {
        return productions;
    }

    @Override
    public String toString() {
        return "{" +
                "\"productions\":" + productions +
                '}';
    }
}
