package org.liuyehcf.markdown.format.processor.impl;

import org.liuyehcf.markdown.format.constant.StringConstant;
import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.LineElement;
import org.liuyehcf.markdown.format.context.LineIterator;
import org.liuyehcf.markdown.format.log.DefaultLogger;
import org.liuyehcf.markdown.format.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.processor.PreFileProcessor;

import java.io.File;

public class LatexFormulaWrapperProcessor extends AbstractFileProcessor implements PreFileProcessor {

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        LineElement lineElement = iterator.getCurrentLineElement();

        // escape code line
        if (isMathFile(fileContext)
                && !lineElement.isCode()) {
            String content = lineElement.getContent();

            content = new InnerFormulaWrapper(fileContext.getFile(), content).format();

            content = new InterFormulaWrapper(fileContext.getFile(), content).format();

            lineElement.setContent(content);
        }
    }

    @Override
    protected void beforeProcess(FileContext fileContext) {
        InnerFormulaWrapper.isMatchLeft = true;
        InterFormulaWrapper.isMatchLeft = true;
        InnerFormulaWrapper.formulaCount = 0;
        InterFormulaWrapper.formulaCount = 0;
        InnerFormulaWrapper.isCrossing = false;
    }

    @Override
    protected void afterProcess(FileContext fileContext) {
        if ((InnerFormulaWrapper.formulaCount & 1) != 0
                || (InterFormulaWrapper.formulaCount & 1) != 0) {
            DefaultLogger.LOGGER.error("file [{}] contains wrong formula grammar: Inner or inter formula wrapper is not matched!!", fileContext.getFile());
            throw new RuntimeException();
        }
    }

    private boolean isMathFile(FileContext fileContext) {
        String value = fileContext.getProperty(StringConstant.HEXO_PROPERTY_MATHJAX);

        if (value != null) {
            if (value.equalsIgnoreCase(StringConstant.TRUE)) {
                return true;
            } else if (value.equalsIgnoreCase(StringConstant.FALSE)) {
                return false;
            } else {
                DefaultLogger.LOGGER.error("file [{}] contains wrong property value, property key = {}, property key = {}", fileContext.getFile(), StringConstant.HEXO_PROPERTY_MATHJAX, value);
                throw new RuntimeException();
            }
        }
        return false;
    }

    private static abstract class AbstractFormulaWrapper {
        final File file;
        final String content;
        int cursor;
        StringBuilder sb;

        AbstractFormulaWrapper(File file, String content) {
            cursor = 0;
            this.file = file;
            this.content = content;
            sb = new StringBuilder();
        }

        protected abstract String format();

        /**
         * 是否已经包含了{% raw %}
         *
         * @return boolean
         */
        final boolean hasFormulaStart() {
            int len = StringConstant.FORMULA_WRAPPER_START.length();
            return cursor >= len
                    && StringConstant.FORMULA_WRAPPER_START.equals(content.substring(cursor - len, cursor));
        }

        /**
         * 是否已经包含了{% endraw %}
         *
         * @param cursor cursor
         * @return boolean
         */
        final boolean hasFormulaEnd(int cursor) {
            int len = StringConstant.FORMULA_WRAPPER_END.length();
            return cursor + len < content.length()
                    && StringConstant.FORMULA_WRAPPER_END.equals(content.substring(cursor + 1, cursor + len + 1));
        }

        /**
         * 是否为边界的前缀
         * 前面不可以是转义字符"\"
         * 前面不可以是正常的$
         *
         * @return boolean
         */
        final boolean isPrefixBoundary() {
            if (cursor == 0) {
                return true;
            } else if (cursor == 1) {
                return content.charAt(cursor - 1) != '\\' && content.charAt(cursor - 1) != '$';
            } else {
                return content.charAt(cursor - 1) != '\\' && !(content.charAt(cursor - 1) == '$' && content.charAt(cursor - 2) != '\\');
            }
        }
    }

    private static final class InnerFormulaWrapper extends AbstractFormulaWrapper {
        private static boolean isMatchLeft;
        private static int formulaCount;
        private static boolean isCrossing;

        InnerFormulaWrapper(File file, String content) {
            super(file, content);
        }

        @Override
        protected String format() {
            boolean originCrossing = isCrossing;
            int originFormulaCount = formulaCount;

            while (cursor < content.length()) {

                // escape inner line code
                if (content.charAt(cursor) == '`') {
                    // '`' is not allowed in formula
                    if (!isMatchLeft) {
                        DefaultLogger.LOGGER.error("file [{}] contains wrong formula grammar: '`' is not allowed in formula. line content: {}", file, content);
                        throw new RuntimeException();
                    }

                    sb.append(content.charAt(cursor++));

                    while (content.charAt(cursor) != '`') {
                        sb.append(content.charAt(cursor++));
                    }

                    sb.append(content.charAt(cursor++));
                } else if (isBoundary()) {
                    if (isMatchLeft) {
                        if (!hasFormulaStart()) {
                            sb.append(StringConstant.FORMULA_WRAPPER_START);
                        }
                        sb.append("$");
                    } else {
                        sb.append("$");
                        if (!hasFormulaEnd()) {
                            sb.append(StringConstant.FORMULA_WRAPPER_END);
                        }
                    }
                    isMatchLeft = !isMatchLeft;
                    formulaCount++;
                    cursor++;
                    isCrossing = false;
                } else {
                    sb.append(content.charAt(cursor++));
                }
            }

            if (isCrossLine()) {
                isCrossing = true;
            } else if (!isMatchLeft && isCrossing) {
                DefaultLogger.LOGGER.error("file [{}] contains wrong formula grammar: Cross line inner formula must end with \\\\. line content: {}", file, content);
                throw new RuntimeException();
            }

            // 如果是非跨行行内公式，必须满足'$'成对出现的条件
            if (!(isCrossing || originCrossing)
                    && ((formulaCount - originFormulaCount) & 1) != 0) {
                DefaultLogger.LOGGER.error("file [{}] contains wrong formula grammar: Inner formula is not match in one line. line content: {}", file, content);
                throw new RuntimeException();
            }

            return sb.toString();
        }

        private boolean isBoundary() {
            return content.charAt(cursor) == '$'
                    && (cursor == content.length() - 1 || content.charAt(cursor + 1) != '$')
                    && isPrefixBoundary();
        }

        private boolean hasFormulaEnd() {
            return hasFormulaEnd(cursor);
        }

        private boolean isCrossLine() {
            return content.length() > 1
                    && content.charAt(content.length() - 1) == '\\'
                    && content.charAt(content.length() - 2) == '\\';
        }
    }

    private static final class InterFormulaWrapper extends AbstractFormulaWrapper {
        private static boolean isMatchLeft;
        private static int formulaCount;

        InterFormulaWrapper(File file, String content) {
            super(file, content);
        }

        @Override
        protected String format() {
            while (cursor < content.length()) {
                // escape inner line code
                if (content.charAt(cursor) == '`') {
                    // '`' is not allowed in formula
                    if (!isMatchLeft) {
                        DefaultLogger.LOGGER.error("file [{}] contains wrong formula grammar: '`' is not allowed in formula. line content: {}", file, content);
                        throw new RuntimeException();
                    }

                    sb.append(content.charAt(cursor++));

                    while (content.charAt(cursor) != '`') {
                        sb.append(content.charAt(cursor++));
                    }

                    sb.append(content.charAt(cursor++));
                } else if (isBoundary()) {
                    if (isMatchLeft) {
                        if (!hasFormulaStart()) {
                            sb.append(StringConstant.FORMULA_WRAPPER_START);
                        }
                        sb.append("$$");
                    } else {
                        sb.append("$$");
                        if (!hasFormulaEnd()) {
                            sb.append(StringConstant.FORMULA_WRAPPER_END);
                        }
                    }
                    cursor += 2;
                    isMatchLeft = !isMatchLeft;
                    formulaCount++;
                } else {
                    sb.append(content.charAt(cursor++));
                }
            }
            return sb.toString();
        }

        private boolean hasFormulaEnd() {
            return hasFormulaEnd(cursor + 1);
        }

        private boolean isBoundary() {
            return cursor < content.length() - 1
                    && content.charAt(cursor) == '$'
                    && content.charAt(cursor + 1) == '$'
                    && (cursor == content.length() - 2 || content.charAt(cursor + 2) != '$')
                    && isPrefixBoundary();
        }
    }
}
