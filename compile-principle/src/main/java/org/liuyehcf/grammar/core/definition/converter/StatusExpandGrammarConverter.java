package org.liuyehcf.grammar.core.definition.converter;

import org.liuyehcf.grammar.core.definition.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 将形如 A → a的文法转换成
 * 1. A → ·a
 * 2. A → a·
 */
public class StatusExpandGrammarConverter extends AbstractGrammarConverter {

    public StatusExpandGrammarConverter(Grammar originalGrammar) {
        super(originalGrammar);
    }

    @Override
    protected Grammar doConvert() {
        List<Production> newProductions = new ArrayList<>();

        // A → B
        //   |
        //   v
        // A → ·B
        // A → B·
        for (Production _P : originalGrammar.getProductions()) {
            Symbol _A = _P.getLeft();

            for (PrimaryProduction _PP : _P.getPrimaryProductions()) {
                int length = _PP.getRight().getSymbols().size();

                // 构造新的  length+1个 PrimaryProduction
                for (int i = 0; i < length + 1; i++) {
                    newProductions.add(
                            Production.create(
                                    PrimaryProduction.create(
                                            _A,
                                            SymbolString.create(
                                                    _PP.getRight().getSymbols(),
                                                    i
                                            )
                                    )

                            )
                    );
                }
            }
        }

        return Grammar.create(
                originalGrammar.getStart(),
                newProductions
        );
    }
}
