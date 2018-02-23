package org.liuyehcf.grammar.core.definition.converter;

import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Production;
import org.liuyehcf.grammar.core.definition.Symbol;

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
        for (Production p : originalGrammar.getProductions()) {
            Symbol _A = p.getLeft();

            for (PrimaryProduction pp : p.getRight()) {
                List<Symbol> symbols = pp.getSymbols();
                int length = symbols.size();

                // 构造新的  length+1个 PrimaryProduction
                for (int i = 0; i < length + 1; i++) {
                    List<Symbol> newSymbols = new ArrayList<>(symbols);
                    newSymbols.add(i, Symbol.DOT);
                    newProductions.add(
                            Production.create(
                                    _A,
                                    PrimaryProduction.create(
                                            newSymbols
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
