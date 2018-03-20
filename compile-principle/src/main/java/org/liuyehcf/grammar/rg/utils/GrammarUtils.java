package org.liuyehcf.grammar.rg.utils;

import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.PrimaryProduction;
import org.liuyehcf.grammar.core.definition.Production;
import org.liuyehcf.grammar.core.definition.Symbol;

import java.util.ArrayList;
import java.util.List;

import static org.liuyehcf.grammar.utils.AssertUtils.assertFalse;
import static org.liuyehcf.grammar.utils.AssertUtils.assertTrue;

public abstract class GrammarUtils {
    public static Grammar createGrammarWithRegex(String regex) {
        return Grammar.create(
                Symbol.START,
                Production.create(
                        Symbol.START,
                        createPrimaryProduction(regex)
                )
        );
    }

    public static PrimaryProduction createPrimaryProduction(Object... objects) {
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
        return PrimaryProduction.create(
                symbols
        );
    }

    public static List<Symbol> extractSymbolsFromGrammar(Grammar grammar) {
        assertTrue(grammar.getProductions().size() == 1);

        Production _P = grammar.getProductions().get(0);

        assertTrue(_P.getRight().size() == 1);

        return _P.getRight().get(0).getSymbols();
    }
}
