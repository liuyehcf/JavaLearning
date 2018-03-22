package org.liuyehcf.grammar;

import org.liuyehcf.grammar.core.ParserException;
import org.liuyehcf.grammar.core.parse.Token;

import java.util.Iterator;

// todo 现在的词法分析器不允许有多余空格
public interface LexicalAnalyzer {
    TokenIterator iterator(String input) throws ParserException;

    interface TokenIterator extends Iterator<Token> {

        @Override
        boolean hasNext();

        @Override
        Token next();
    }
}
