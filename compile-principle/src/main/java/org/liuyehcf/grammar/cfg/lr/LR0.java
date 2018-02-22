package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.definition.Grammar;
import org.liuyehcf.grammar.definition.converter.StatusExpandGrammarConverter;

public class LR0 implements LRParser {

    // 原始文法
    private final Grammar originalGrammar;

    // 转换后的文法
    private Grammar grammar;

    public LR0(Grammar grammar) {
        this.originalGrammar = grammar;

        init();
    }

    private void init() {
        // 文法转换
        convertGrammar();
    }

    private void convertGrammar() {
        this.grammar = new StatusExpandGrammarConverter(originalGrammar).getConvertedGrammar();
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

}
