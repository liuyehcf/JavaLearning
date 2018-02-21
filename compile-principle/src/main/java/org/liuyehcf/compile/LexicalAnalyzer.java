package org.liuyehcf.compile;

import org.liuyehcf.compile.core.MorphemeType;
import org.liuyehcf.compile.parse.Token;
import org.liuyehcf.compile.utils.Pair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.liuyehcf.compile.utils.AssertUtils.assertTrue;

/**
 * 词法分析器，直接用JDK的正则表达式进行解析
 * todo 需要将morphemes按照词素类型进行分类，正则表达式的匹配必须放在最后面
 */
public class LexicalAnalyzer {

    private final Map<String, Pair<String, MorphemeType>> morphemes;

    private final Map<Integer, String> groups;

    private final Pattern pattern;

    private LexicalAnalyzer(Map<String, Pair<String, MorphemeType>> morphemes, Map<Integer, String> groups, Pattern pattern) {
        this.morphemes = Collections.unmodifiableMap(morphemes);
        this.groups = Collections.unmodifiableMap(groups);
        this.pattern = pattern;
    }

    public static Builder builder() {
        return new Builder();
    }

    public TokenIterator iterator(String expression) {
        return new TokenIterator(pattern.matcher(expression));
    }

    public static final class Builder {

        // 词素id -> (词素,类型) 的映射表
        private Map<String, Pair<String, MorphemeType>> morphemes = new HashMap<>();

        // 正则表达式group -> 词素id 的映射表
        private Map<Integer, String> groups = new HashMap<>();

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
            return this;
        }

        public LexicalAnalyzer build() {
            StringBuilder regex = new StringBuilder();

            int groupId = 1;

            for (Map.Entry<String, Pair<String, MorphemeType>> entry : morphemes.entrySet()) {
                String id = entry.getKey();
                String morpheme = entry.getValue().getFirst();

                regex.append('(').append(morpheme)
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

            if (!morphemes.isEmpty()) {
                regex.setLength(regex.length() - 1);
            }

            Pattern pattern;
            try {
                pattern = Pattern.compile(regex.toString());
            } catch (Throwable e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            return new LexicalAnalyzer(morphemes, groups, pattern);
        }
    }

    public final class TokenIterator implements Iterator<Token> {

        private final Matcher matcher;

        List<Token> tokens = new ArrayList<>();

        private int index = 0;

        private TokenIterator(Matcher matcher) {
            this.matcher = matcher;
            init();
        }


        private void init() {
            while (matcher.find()) {
                int groupIndex = findGroup();

                String value = matcher.group(groupIndex);

                String id = groups.get(groupIndex);

                tokens.add(new Token(id, value, morphemes.get(id).getSecond()));
            }

            tokens.add(new Token("__DOLLAR__", "__DOLLAR__", MorphemeType.INTERNAL));
        }

        private int findGroup() {
            int index = -1;

            for (int i : groups.keySet()) {
                if (matcher.group(i) != null) {
                    index = i;
                }
            }

            assertTrue(index != -1);

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
