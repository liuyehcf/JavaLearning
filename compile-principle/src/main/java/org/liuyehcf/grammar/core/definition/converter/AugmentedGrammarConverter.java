package org.liuyehcf.grammar.core.definition.converter;

import org.liuyehcf.grammar.core.definition.*;
import org.liuyehcf.grammar.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;

/**
 * 增广文法
 */
public class AugmentedGrammarConverter extends AbstractGrammarConverter {

    public AugmentedGrammarConverter(Grammar originalGrammar) {
        super(originalGrammar);
    }

    @Override
    protected Grammar doConvert() {
        List<Production> newProductions = new ArrayList<>();

        assertFalse(originalGrammar.getProductions().isEmpty());

        newProductions.add(
                Production.create(
                        PrimaryProduction.create(
                                Symbol.START,
                                SymbolString.create(
                                        ListUtils.of(originalGrammar.getStart())
                                )
                        )
                )
        );

        newProductions.addAll(originalGrammar.getProductions());

        return Grammar.create(
                Symbol.START,
                newProductions
        );
    }
}
