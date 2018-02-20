package org.liuyehcf.compile.definition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.liuyehcf.compile.utils.AssertUtils.assertFalse;
import static org.liuyehcf.compile.utils.AssertUtils.assertTrue;

/**
 * 文法定义
 */
public class Grammar {

    // 文法包含的所有产生式
    final private List<Production> productions;

    private Grammar(List<Production> productions) {
        this.productions = productions;
    }

    public static Grammar createGrammar(Production... productions) {
        return new Grammar(Arrays.asList(productions));
    }

    public static Grammar createGrammar(List<Production> productions) {
        return new Grammar(productions);
    }

    public static Production parallelProduction(Production p1, Production p2) {
        assertTrue(p1.getLeft().equals(p2.getLeft()));

        List<SymbolSequence> right = new ArrayList<>(p1.getRight());
        right.addAll(p2.getRight());

        return Production.createProduction(
                p1.getLeft(),
                right);
    }

    public List<Production> getProductions() {
        return productions;
    }

    public String toJSONString() {
        return '{' +
                "\"productions\":" + productions +
                '}';
    }

    public String toReadableJSONString() {
        StringBuilder sb = new StringBuilder();

        sb.append('{')
                .append("\"productions\":")
                .append('[');

        for (Production production : productions) {
            sb.append(production.toReadableJSONString())
                    .append(",");
        }

        assertFalse(productions.isEmpty());
        sb.setLength(sb.length() - 1);

        sb.append(']')
                .append('}');

        return sb.toString();
    }

    @Override
    public String toString() {
        return toReadableJSONString();
    }
}
