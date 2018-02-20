package org.liuyehcf.compile.definition;

import java.util.Arrays;
import java.util.List;

import static org.liuyehcf.compile.utils.AssertUtils.assertFalse;

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
