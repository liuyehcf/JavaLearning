package org.liuyehcf.grammar;

import org.liuyehcf.grammar.core.parse.Token;

import java.util.Iterator;

public interface LexicalAnalyzer {
    TokenIterator iterator(String input);

    interface TokenIterator extends Iterator<Token> {

        @Override
        boolean hasNext();

        @Override
        Token next();
    }
}
