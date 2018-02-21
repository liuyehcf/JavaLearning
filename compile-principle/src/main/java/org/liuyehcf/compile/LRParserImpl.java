package org.liuyehcf.compile;

import org.liuyehcf.compile.definition.Grammar;
import org.liuyehcf.compile.definition.PrimaryProduction;
import org.liuyehcf.compile.definition.Production;
import org.liuyehcf.compile.definition.Symbol;

import java.util.ArrayList;
import java.util.List;

public class LRParserImpl implements LRParser {

    // 原始文法
    private final Grammar originGrammar;

    // 转换后的文法
    private Grammar grammar;

    public LRParserImpl(Grammar grammar) {
        this.originGrammar = grammar;

        init();
    }

    private void init() {
        // 文法转换
        convertGrammar();
    }

    private void convertGrammar() {
        this.grammar = GrammarConverter.convert(originGrammar);
    }

    @Override
    public boolean isMatch(String expression) {
        return false;
    }

    private void moveIn() {

    }

    private void reduction() {

    }

    private void accept() {

    }

    private void error() {

    }

    @Override
    public Grammar getGrammar() {
        return grammar;
    }

    public static final class GrammarConverter {
        // 待转换的文法
        private final Grammar grammar;

        private GrammarConverter(Grammar grammar) {
            this.grammar = grammar;
        }

        public static Grammar convert(Grammar grammar) {
            return new GrammarConverter(grammar)
                    .convert();
        }

        private Grammar convert() {
            List<Production> newProductions = new ArrayList<>();

            // A → B
            //   |
            //   v
            // A → ·B
            // A → B·
            for (Production p : grammar.getProductions()) {
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

            return Grammar.create(newProductions);
        }

    }

}
