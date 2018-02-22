package org.liuyehcf.grammar.definition.converter;

import org.liuyehcf.grammar.definition.Grammar;
import org.liuyehcf.grammar.definition.PrimaryProduction;
import org.liuyehcf.grammar.definition.Production;
import org.liuyehcf.grammar.definition.Symbol;
import org.liuyehcf.grammar.utils.AssertUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 合并具有相同左部的产生式
 */
public class MergeGrammarConverter extends AbstractGrammarConverter {
    public MergeGrammarConverter(Grammar originalGrammar) {
        super(originalGrammar);
    }

    private static Production parallelProduction(Production p1, Production p2) {
        AssertUtils.assertTrue(p1.getLeft().equals(p2.getLeft()));

        List<PrimaryProduction> right = new ArrayList<>(p1.getRight());
        right.addAll(p2.getRight());

        return Production.create(
                p1.getLeft(),
                right);
    }

    @Override
    protected Grammar doConvert() {
        Map<Symbol, Production> productionMap = new HashMap<>();

        for (Production p : originalGrammar.getProductions()) {
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

        return Grammar.create(
                originalGrammar.getStart(),
                productionMap.entrySet()
                        .stream()
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList())
        );
    }
}
