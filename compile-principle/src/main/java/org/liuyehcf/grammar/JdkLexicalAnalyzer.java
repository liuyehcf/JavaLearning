package org.liuyehcf.grammar;

import org.liuyehcf.grammar.core.MorphemeType;
import org.liuyehcf.grammar.rg.Matcher;
import org.liuyehcf.grammar.utils.Pair;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * 词法分析器，直接用JDK的正则表达式进行解析
 */
public class JdkLexicalAnalyzer extends AbstractLexicalAnalyzer {

    private final Pattern pattern;

    private JdkLexicalAnalyzer(Map<String, Pair<String, MorphemeType>> morphemes, Map<Integer, String> groups, String regex) {
        super(morphemes, groups, regex);
        try {
            this.pattern = Pattern.compile(regex);
        } catch (Throwable e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public TokenIteratorImpl iterator(String input) {
        return new TokenIteratorImpl(new JdkMatcherProxy(pattern.matcher(input)));
    }

    public static final class Builder extends AbstractLexicalAnalyzer.Builder {
        @Override
        protected LexicalAnalyzer createLexicalAnalyzer() {
            return new JdkLexicalAnalyzer(morphemes, groups, regex);
        }
    }

    private static final class JdkMatcherProxy implements Matcher {
        private final java.util.regex.Matcher target;

        private JdkMatcherProxy(java.util.regex.Matcher target) {
            this.target = target;
        }

        @Override
        public boolean matches() {
            return target.matches();
        }

        @Override
        public boolean find() {
            return target.find();
        }

        @Override
        public String group(int group) {
            return target.group(group);
        }
    }


}
