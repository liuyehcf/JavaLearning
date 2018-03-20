package org.liuyehcf.grammar.rg.utils;

import org.liuyehcf.grammar.core.definition.*;

import java.util.ArrayList;
import java.util.List;

import static org.liuyehcf.grammar.core.definition.Symbol.createNonTerminator;
import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;
import static org.liuyehcf.grammar.utils.AssertUtils.assertTrue;

public abstract class GrammarUtils {
    private static Symbol DEFAULT_NON_TERMINATOR = createNonTerminator("DEFAULT_NON_TERMINATOR");


    public static Grammar createGrammarWithRegex(String regex) {
        return Grammar.create(
                DEFAULT_NON_TERMINATOR,
                Production.create(
                        PrimaryProduction.create(
                                DEFAULT_NON_TERMINATOR,
                                createPrimaryProduction(regex)
                        )
                )
        );
    }

    public static SymbolString createPrimaryProduction(Object... objects) {
        List<Symbol> symbols = new ArrayList<>();
        for (Object obj : objects) {
            if (obj instanceof Character) {
                char c = (char) obj;
                symbols.add(SymbolUtils.getAlphabetSymbolWithChar(c));
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
        return SymbolString.create(
                symbols
        );
    }

    public static List<Symbol> extractSymbolsFromGrammar(Grammar grammar) {
        assertTrue(grammar.getProductions().size() == 1);

        Production _P = grammar.getProductions().get(0);

        assertTrue(_P.getPrimaryProductions().size() == 1);

        return _P.getPrimaryProductions().get(0).getRight().getSymbols();
    }
}
