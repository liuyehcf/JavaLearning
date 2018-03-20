package org.liuyehcf.grammar.core.definition.converter;

import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Production;
import org.liuyehcf.grammar.core.definition.Symbol;
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

    private static Production parallelProduction(Production _P1, Production _P2) {
        AssertUtils.assertTrue(_P1.getLeft().equals(_P2.getLeft()));

        List<PrimaryProduction> primaryProductions = new ArrayList<>(_P1.getPrimaryProductions());
        primaryProductions.addAll(_P2.getPrimaryProductions());

        return Production.create(
                primaryProductions
        );
    }

    @Override
    protected Grammar doConvert() {
        Map<Symbol, Production> productionMap = new HashMap<>();

        for (Production _P : originalGrammar.getProductions()) {
            Symbol nonTerminator = _P.getLeft();
            AssertUtils.assertFalse(nonTerminator.isTerminator());

            // 合并相同左部的产生式
            if (productionMap.containsKey(nonTerminator)) {
                productionMap.put(
                        nonTerminator,
                        parallelProduction(
                                productionMap.get(nonTerminator),
                                _P
                        )
                );
            } else {
                productionMap.put(nonTerminator, _P);
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
