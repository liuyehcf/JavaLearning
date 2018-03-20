package org.liuyehcf.grammar.core.definition;

import org.liuyehcf.grammar.utils.ListUtils;
import org.liuyehcf.grammar.utils.SetUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;

/**
 * 文法定义
 */
public class Grammar {

    // 文法开始符号
    private final Symbol start;

    // 文法包含的所有产生式
    private final List<Production> productions;

    // 终结符集合
    private final Set<Symbol> terminators;

    // 非终结符集合
    private final Set<Symbol> nonTerminators;

    // 文法符号集合
    private final Set<Symbol> symbols;

    private Grammar(Symbol start, List<Production> productions) {
        this.start = start;
        this.productions = Collections.unmodifiableList(productions);

        Set<Symbol> terminators = new HashSet<>();
        Set<Symbol> nonTerminators = new HashSet<>();

        for (Production _P : productions) {
            for (PrimaryProduction _PP : _P.getPrimaryProductions()) {
                assertFalse(_PP.getLeft().isTerminator());
                nonTerminators.add(_PP.getLeft());

                for (Symbol symbol : _PP.getRight().getSymbols()) {
                    if (symbol.isTerminator()) {
                        terminators.add(symbol);
                    } else {
                        nonTerminators.add(symbol);
                    }
                }
            }
        }

        this.terminators = Collections.unmodifiableSet(terminators);
        this.nonTerminators = Collections.unmodifiableSet(nonTerminators);
        this.symbols = Collections.unmodifiableSet(SetUtils.of(this.terminators, this.nonTerminators));
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

    public Set<Symbol> getTerminators() {
        return terminators;
    }

    public Set<Symbol> getNonTerminators() {
        return nonTerminators;
    }

    public Set<Symbol> getSymbols() {
        return symbols;
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
