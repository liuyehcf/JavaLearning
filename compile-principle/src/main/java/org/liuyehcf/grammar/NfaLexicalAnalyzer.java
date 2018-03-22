package org.liuyehcf.grammar;

import org.liuyehcf.grammar.core.MorphemeType;
import org.liuyehcf.grammar.core.ParserException;
import org.liuyehcf.grammar.rg.RGBuilder;
import org.liuyehcf.grammar.rg.RGParser;
import org.liuyehcf.grammar.utils.Pair;

import java.util.Map;

public class NfaLexicalAnalyzer extends AbstractLexicalAnalyzer {

    private final RGParser parser;

    private NfaLexicalAnalyzer(Map<String, Pair<String, MorphemeType>> morphemes, Map<Integer, String> groups, String regex) {
        super(morphemes, groups, regex);
        this.parser = RGBuilder.compile(regex).buildNfa();
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public TokenIterator iterator(String input) throws ParserException {
        return new TokenIteratorImpl(parser.matcher(input));
    }

    public static final class Builder extends AbstractLexicalAnalyzer.Builder {
        @Override
        protected LexicalAnalyzer createLexicalAnalyzer() {
            return new NfaLexicalAnalyzer(morphemes, groups, regex);
        }
    }
}
