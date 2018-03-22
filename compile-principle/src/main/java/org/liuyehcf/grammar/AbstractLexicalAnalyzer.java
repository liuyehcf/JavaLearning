package org.liuyehcf.grammar;

import org.liuyehcf.grammar.core.MorphemeType;
import org.liuyehcf.grammar.core.ParserException;
import org.liuyehcf.grammar.core.definition.Symbol;
import org.liuyehcf.grammar.core.parse.Token;
import org.liuyehcf.grammar.rg.Matcher;
import org.liuyehcf.grammar.utils.AssertUtils;
import org.liuyehcf.grammar.utils.Pair;

import java.util.*;

public abstract class AbstractLexicalAnalyzer implements LexicalAnalyzer {
    protected final Map<String, Pair<String, MorphemeType>> morphemes;

    protected final Map<Integer, String> groups;

    protected final String regex;

    protected AbstractLexicalAnalyzer(Map<String, Pair<String, MorphemeType>> morphemes, Map<Integer, String> groups, String regex) {
        this.morphemes = Collections.unmodifiableMap(morphemes);
        this.groups = Collections.unmodifiableMap(groups);
        this.regex = regex;
    }

    public abstract static class Builder {

        // 词素id -> (词素,类型) 的映射表
        protected Map<String, Pair<String, MorphemeType>> morphemes = new HashMap<>();

        // 正则表达式group -> 词素id 的映射表
        protected Map<Integer, String> groups = new HashMap<>();

        // 词素类型 -> (词素id,词素) 的映射表（为了在构造正则表达式的时候按类型排序）
        protected Map<MorphemeType, List<Pair<String, String>>> types = new HashMap<>();

        protected String regex;

        protected Builder() {
            for (MorphemeType type : MorphemeType.values()) {
                types.put(type, new ArrayList<>());
            }
        }

        public Builder addMorpheme(String morpheme) {
            return addMorpheme(morpheme, morpheme, MorphemeType.NORMAL);
        }

        public Builder addMorpheme(String id, String morpheme, MorphemeType type) {

            // 由于morpheme会添加到正则表达式中，因此一些正则表达式相关的符号需要进行转义
            if (type != MorphemeType.REGEX) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < morpheme.length(); i++) {
                    char c = morpheme.charAt(i);
                    if (c == '+'
                            || c == '*'
                            || c == '?'
                            || c == '('
                            || c == ')'
                            || c == '{'
                            || c == '}'
                            || c == '.'
                            && (i == 0 || morpheme.charAt(i - 1) != '\\')) {
                        sb.append('\\').append(c);
                    } else {
                        sb.append(c);
                    }
                }

                morpheme = sb.toString();
            }


            if (morphemes.containsKey(id)) {
                throw new IllegalArgumentException("id repeated");
            }
            morphemes.put(id, new Pair<>(morpheme, type));

            types.get(type).add(new Pair<>(id, morpheme));

            return this;
        }

        public final LexicalAnalyzer build() {
            StringBuilder regexBuilder = new StringBuilder();

            // 正则表达式中的组号
            int groupId = 1;

            for (MorphemeType type : MorphemeType.values()) {
                for (Pair<String, String> pair : types.get(type)) {
                    String id = pair.getFirst();
                    String morpheme = pair.getSecond();

                    regexBuilder.append('(').append(morpheme)
                            .append(')').append("|");

                    groups.put(groupId++, id);

                    // 跳过正则表达式中的括号
                    for (int i = 0; i < morpheme.length(); i++) {
                        char c = morpheme.charAt(i);

                        if (c == '('
                                && (i == 0 || morpheme.charAt(i - 1) != '\\')) {
                            groupId++;
                        }
                    }
                }
            }

            AssertUtils.assertFalse(morphemes.isEmpty());
            regexBuilder.setLength(regexBuilder.length() - 1);

            regex = regexBuilder.toString();

            return createLexicalAnalyzer();
        }

        protected abstract LexicalAnalyzer createLexicalAnalyzer();
    }

    protected final class TokenIteratorImpl implements LexicalAnalyzer.TokenIterator {

        private final Matcher matcher;

        List<Token> tokens = new ArrayList<>();

        private int index = 0;

        TokenIteratorImpl(Matcher matcher) throws ParserException {
            this.matcher = matcher;
            init();
        }

        private void init() throws ParserException {
            int lastEndIndex = 0;

            while (matcher.find()) {
                int groupIndex = findGroup();

                int startIndex = matcher.start(groupIndex);

                if (startIndex != lastEndIndex) {
                    throw new ParserException();
                }

                lastEndIndex = matcher.end(groupIndex);

                String value = matcher.group(groupIndex);

                String id = groups.get(groupIndex);

                tokens.add(new Token(Symbol.createTerminator(id, morphemes.get(id).getSecond()), value, morphemes.get(id).getSecond()));
            }

            tokens.add(new Token(Symbol.DOLLAR, "__DOLLAR__", MorphemeType.NORMAL));
        }

        private int findGroup() {
            int index = -1;

            for (int i : groups.keySet()) {
                if (matcher.group(i) != null) {
                    index = i;
                }
            }

            AssertUtils.assertTrue(index != -1);

            return index;
        }

        @Override
        public boolean hasNext() {
            return index < tokens.size();
        }

        @Override
        public Token next() {
            return tokens.get(index++);
        }
    }
}
