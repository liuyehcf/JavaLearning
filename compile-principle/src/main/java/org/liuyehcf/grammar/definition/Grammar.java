package org.liuyehcf.grammar.definition;

import org.liuyehcf.grammar.utils.AssertUtils;
import org.liuyehcf.grammar.utils.ListUtils;

import java.util.*;
import java.util.stream.Collectors;

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

        this.productions = combineProductionWithSameLeft(productions);
    }

    public static Grammar create(Symbol start, Production... productions) {
        return new Grammar(start, ListUtils.of(productions));
    }

    public static Grammar create(Symbol start, List<Production> productions) {
        return new Grammar(start, productions);
    }

    public static Production parallelProduction(Production p1, Production p2) {
        AssertUtils.assertTrue(p1.getLeft().equals(p2.getLeft()));

        List<PrimaryProduction> right = new ArrayList<>(p1.getRight());
        right.addAll(p2.getRight());

        return Production.create(
                p1.getLeft(),
                right);
    }

    /**
     * 合并具有相同左部的产生式
     */
    private List<Production> combineProductionWithSameLeft(List<Production> productions) {
        Map<Symbol, Production> productionMap = new HashMap<>();

        for (Production p : productions) {
            Symbol nonTerminator = p.getLeft();
            AssertUtils.assertFalse(nonTerminator.isTerminator());

            // 合并相同左部的产生式
            if (productionMap.containsKey(nonTerminator)) {
                productionMap.put(
                        nonTerminator,
                        parallelProduction(
                                productionMap.get(nonTerminator),
                                p
                        )
                );
            } else {
                productionMap.put(nonTerminator, p);
            }
        }

        return Collections.unmodifiableList(productionMap.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()));
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

        for (Production p : productions) {
            sb.append(p.toReadableJSONString())
                    .append(",");
        }

        AssertUtils.assertFalse(productions.isEmpty());
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
