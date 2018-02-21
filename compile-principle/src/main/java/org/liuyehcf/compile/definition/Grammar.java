package org.liuyehcf.compile.definition;

import org.liuyehcf.compile.utils.ListUtils;

import java.util.ArrayList;
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

        init();
    }

    private void init() {
        boolean containsStartSymbol = false;

        // 首先，检查是否含有文法开始符号的产生式
        for (Production production : productions) {
            if (production.getLeft().equals(Symbol.START)) {
                containsStartSymbol = true;
                break;
            }
        }

        if (!containsStartSymbol) {
            Symbol symbol = productions.get(0).getLeft();

            // 添加文法开始符号的产生式
            productions.add(
                    Production.create(
                            Symbol.START,
                            PrimaryProduction.create(
                                    symbol
                            )
                    )
            );
        }
    }

    public static Grammar create(Production... productions) {
        return new Grammar(ListUtils.of(productions));
    }

    public static Grammar create(List<Production> productions) {
        return new Grammar(productions);
    }

    public static Production parallelProduction(Production p1, Production p2) {
        assertTrue(p1.getLeft().equals(p2.getLeft()));

        List<PrimaryProduction> right = new ArrayList<>(p1.getRight());
        right.addAll(p2.getRight());

        return Production.create(
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
