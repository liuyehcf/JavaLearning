package org.liuyehcf.grammar.core.definition;

import org.liuyehcf.grammar.utils.ListUtils;

import java.util.Collections;
import java.util.List;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;

/**
 * 文法定义
 */
public class Grammar {

    // 文法开始符号
    private final Symbol start;

    // 文法包含的所有产生式
    private final List<Production> productions;

    private Grammar(Symbol start, List<Production> productions) {
        this.start = start;
        this.productions = Collections.unmodifiableList(productions);
    }

    public static Grammar create(Symbol start, Production... productions) {
        return new Grammar(start, ListUtils.of(productions));
    }

    public static Grammar create(Symbol start, List<Production> productions) {
        return new Grammar(start, productions);
    }

    public Symbol getStart() {
        return start;
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

        for (Production _P : productions) {
            sb.append(_P.toReadableJSONString())
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
