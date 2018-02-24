package org.liuyehcf.grammar.rg;


import org.liuyehcf.grammar.core.definition.Grammar;
import org.liuyehcf.grammar.core.definition.Production;
import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.core.definition.converter.SimplificationGrammarConverter;
import org.liuyehcf.grammar.rg.utils.GrammarUtils;

import static org.liuyehcf.grammar.core.definition.Symbol.createNonTerminator;

/**
 * Created by Liuye on 2017/10/24.
 */
public class TestRegex {
    static final String[] REGEX_GROUP_1 = {
            "", "a", "z", "A", "Z", "!", ".", "#", "\0", "\n",
            "azAZ01!#@",
    };
    static final String[] REGEX_GROUP_2 = {
            "\\.", "\\|", "\\*", "\\+", "\\[", "\\]", "\\(", "\\)",
            "\\d", "\\D", "\\w", "\\W"
    };
    static final String[] REGEX_GROUP_3 = {
            "[\\[]", "[\\]]", "[\\d]", "[\\D]", "[\\d]", "[\\D]",
            "[^\\[]", "[^\\]]", "[^\\d]", "[^\\D]", "[^\\d]", "[^\\D]",
    };
    static final String[] REGEX_GROUP_4 = {
            "a?", "z?", "!?", "\0?", "\n?", ".?",
            "\\.?", "\\|?", "\\*?", "\\+?", "\\[?", "\\]?", "\\(?", "\\)?",
            "\\d?", "\\D?", "\\w?", "\\W?", "\\s?", "\\S?",
            "a?c?", "a?c?efg", "012a?c?efg"
    };
    static final String[] REGEX_GROUP_5 = {
            "a*", "z*", "!*", "\0*", "\n*", ".*",
            "\\.*", "\\|*", "\\**", "\\+*", "\\[*", "\\]*", "\\(*", "\\)*",
            "\\d*", "\\D*", "\\w*", "\\W*", "\\s*", "\\S*",
            "a*c*", "a*c*efg", "012a*c*efg"
    };
    static final String[] REGEX_GROUP_6 = {
            "a+", "z+", "!+", "\0+", "\n+", ".+",
            "\\.+", "\\|+", "\\*+", "\\++", "\\[+", "\\]+", "\\(+", "\\)+",
            "\\d+", "\\D+", "\\w+", "\\W+", "\\s", "\\S",
            "a+c+", "a+c+efg", "012a+c+efg"
    };
    static final String[] REGEX_GROUP_7 = {
            "a|b", "aaaaa|b", "a|bbbbbb", "0000|11111",
            "a|b12c|c|d!ef|ge+l#ksjd|a*po",
            "a|[abc\\d]", "[^\\W]|bc",
            ".a|b12c|c.|d!ef|ge+l#ksjd|a*po",
            "a|0|1|b|z", "a|0[sdfsd]f|1|bddd|z",
            "a*|b+|c|.|[12]3|asdjf[\\dhaks]jdhf|ced|ac\\d+edf|\0\\w",
            "111[\\d]ac|cd[^\\W].\\*+|cd09!%@#"
    };
    static final String[] REGEX_GROUP_8 = {
            "()", "()()", "(())", "((()))()()(()((()(()))))()",
            "(abc(cd)(ef(g)()))", "((a)|(b))", "((a)+)", "a*", "((a(b|e))*)",
            "(a*)+", "(a+)*", "(a+)*|(a*)+",
            "a(b(d(e|f)*)|((g(h|i)+)))+",
            "a(b(d(e+|f*))|((g(\\d|i)+)))+",
            "(a)", "(0)", "(\\d)", "(\\w)", "(.)", "(a+)", "(0*)", "(\0)",
            "(a|b)", "([0123])", "(a|bc[\\d])",
            "(a|b+)", "(c[0123]+e)", "(a|bc[\\d])",
            "(ab*c+|1[^\\w]+)|(a(b(e(g|o9))+)).",
            "a|(bc?d|(efg|[\\w]))c\\++|liuye",
            "((a+())~!@#[\\d]ac)(\0)()((098)(2\\.9(3()(c()de)r[^\\S]c)s)ef)a()",
            "(ab|cd*f|es(d)(3|[\\s]|0a|9(3(37(9+)?)99|283(a(())!%#@)(a|(4(d)([1z])))(7()1+)9)))"
    };
    static final String[] REGEX_GROUP_SPECIAL = {
            "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*",//email
            createIdentifierRegex(),
    };

    static Grammar createIdentifierGrammar() {
        Symbol digit = createNonTerminator("digit");
        Symbol letter_ = createNonTerminator("letter_");
        Symbol id = createNonTerminator("id");

        return new SimplificationGrammarConverter(
                Grammar.create(
                        id,
                        Production.create(
                                digit,
                                GrammarUtils.createPrimaryProduction("[0123456789]")
                        ),
                        Production.create(
                                letter_,
                                GrammarUtils.createPrimaryProduction("[abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_]")),
                        Production.create(
                                id,
                                GrammarUtils.createPrimaryProduction(letter_, '(', letter_, '|', digit, ')', '*')
                        )
                )
        ).getConvertedGrammar();
    }

    static String createIdentifierRegex() {
        Grammar grammar = createIdentifierGrammar();

        StringBuilder sb = new StringBuilder();

        for (Symbol symbol : GrammarUtils.extractSymbolsFromGrammar(grammar)) {
            sb.append(symbol.getValue());
        }

        return sb.toString();
    }
}
