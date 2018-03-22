package org.liuyehcf.grammar;

import org.liuyehcf.grammar.core.ParserException;
import org.liuyehcf.grammar.core.parse.Token;

import java.util.Iterator;

public interface LexicalAnalyzer {
    TokenIterator iterator(String input) throws ParserException;

    interface TokenIterator extends Iterator<Token> {

        @Override
        boolean hasNext();

        @Override
        Token next();
    }
}
