package org.liuyehcf.grammar.rg.utils;

import org.liuyehcf.grammar.definition.Grammar;
import org.liuyehcf.grammar.definition.PrimaryProduction;
import org.liuyehcf.grammar.definition.Production;
import org.liuyehcf.grammar.definition.Symbol;

import java.util.ArrayList;
import java.util.List;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;

public abstract class GrammarUtils {
    private static Symbol DEFAULT_NON_TERMINATOR = Symbol.createNonTerminator("DEFAULT_NON_TERMINATOR");

    public static Grammar createGrammarWithRegex(String regex) {
        Grammar grammarDefinition = Grammar.create(
                DEFAULT_NON_TERMINATOR,
                Production.create(
                        DEFAULT_NON_TERMINATOR,
                        createPrimaryProduction(regex)
                )
        );

        return grammarDefinition;
    }

    public static PrimaryProduction createPrimaryProduction(Object... objects) {
        List<Symbol> symbols = new ArrayList<>();
        for (Object obj : objects) {
            if (obj instanceof Character) {
                symbols.add(SymbolUtils.getAlphabetSymbolWithChar(((char) obj)));
            } else if (obj instanceof String) {
                for (char c : ((String) obj).toCharArray()) {
                    symbols.add(SymbolUtils.getAlphabetSymbolWithChar(c));
                }
            } else if (obj instanceof Symbol) {
                Symbol symbol = (Symbol) obj;
                assertFalse(symbol.isTerminator());
                symbols.add(symbol);
            }
        }
        return PrimaryProduction.create(
                symbols
        );
    }
}
