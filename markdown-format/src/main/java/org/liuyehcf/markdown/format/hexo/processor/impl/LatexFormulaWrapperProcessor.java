package org.liuyehcf.markdown.format.hexo.processor.impl;

import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineElement;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;
import org.liuyehcf.markdown.format.hexo.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.hexo.processor.PreFileProcessor;

import static org.liuyehcf.markdown.format.hexo.constant.StringConstant.*;
import static org.liuyehcf.markdown.format.hexo.log.DefaultLogger.DEFAULT_LOGGER;

public class LatexFormulaWrapperProcessor extends AbstractFileProcessor implements PreFileProcessor {
    private boolean isInnerFormulaStart;
    private boolean isInterFormulaStart;
    private int innerFormulaCount;
    private int interFormulaCount;

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        LineElement lineElement = iterator.getCurrentLineElement();

        // escape code line
        if (isMathFile(fileContext)
                && !lineElement.isCode()) {
            String content = lineElement.getContent();

            content = processInnerFormula(fileContext, iterator, content);

            content = processInterFormula(fileContext, iterator, content);

            lineElement.setContent(content);
        }
    }

    @Override
    protected void beforeProcess(FileContext fileContext) {
        isInnerFormulaStart = true;
        isInterFormulaStart = true;
        innerFormulaCount = 0;
        interFormulaCount = 0;
    }

    @Override
    protected void afterProcess(FileContext fileContext) {
        if ((innerFormulaCount & 1) != 0
                || (interFormulaCount & 1) != 0) {
            DEFAULT_LOGGER.error("file [{}] contains unmatched inter formula wrappers!", fileContext.getFile());
        }
    }

    private boolean isMathFile(FileContext fileContext) {
        String value = fileContext.getProperty(HEXO_PROPERTY_MATHJAX);

        if (value != null) {
            if (value.equalsIgnoreCase(TRUE)) {
                return true;
            } else if (value.equalsIgnoreCase(FALSE)) {
                return false;
            } else {
                DEFAULT_LOGGER.error("file [{}] contains wrong property value, property key = {}, property key = {}", fileContext.getFile(), HEXO_PROPERTY_MATHJAX, value);
                throw new RuntimeException();
            }
        }
        return false;
    }

    private String processInnerFormula(FileContext fileContext, LineIterator iterator, String content) {
        int cursor = 0;

        StringBuilder sb = new StringBuilder();

        while (cursor < content.length()) {

            // escape inner line code
            if (content.charAt(cursor) == '`') {
                // '`' is not allowed in formula
                if (!isInnerFormulaStart) {
                    DEFAULT_LOGGER.error("file [{}] contains wrong formula grammar: '`' is not allowed in formula. line content: {}", fileContext.getFile(), content);
                    throw new RuntimeException();
                }

                sb.append(content.charAt(cursor++));

                while (content.charAt(cursor) != '`') {
                    sb.append(content.charAt(cursor++));
                }

                sb.append(content.charAt(cursor++));
            } else if (isInnerFormulaBoundary(content, cursor)) {
                if (isInnerFormulaStart) {
                    if (hasFormulaStart(content, cursor)) {
                        sb.append("$");
                    } else {
                        sb.append(FORMULA_WRAPPER_START)
                                .append("$");
                    }
                } else {
                    if (hasFormulaEnd(content, cursor)) {
                        sb.append("$");
                    } else {
                        sb.append("$")
                                .append(FORMULA_WRAPPER_END);
                    }
                }
                isInnerFormulaStart = !isInnerFormulaStart;
                innerFormulaCount++;
                cursor++;
            } else {
                sb.append(content.charAt(cursor++));
            }
        }

        return sb.toString();
    }

    /**
     * 当前符号是否是一个行内公式的边界符号
     *
     * @param content
     * @param cursor
     * @return
     */
    private boolean isInnerFormulaBoundary(String content, int cursor) {
        if (content.charAt(cursor) == '$'
                && (cursor == content.length() - 1 || content.charAt(cursor + 1) != '$')) {
            if (cursor == 0) {
                return true;
            } else if (cursor == 1) {
                return content.charAt(cursor - 1) != '\\' && content.charAt(cursor - 1) != '$';
            } else {
                return content.charAt(cursor - 1) != '\\' && !(content.charAt(cursor - 1) == '$' && content.charAt(cursor - 2) != '\\');
            }
        }
        return false;
    }

    /**
     * 是否已经包含了{% raw %}
     *
     * @param content
     * @param cursor
     * @return
     */
    private boolean hasFormulaStart(String content, int cursor) {
        int len = FORMULA_WRAPPER_START.length();
        return cursor >= len
                && FORMULA_WRAPPER_START.equals(content.substring(cursor - len, cursor));
    }

    /**
     * 是否已经包含了{% endraw %}
     *
     * @param content
     * @param cursor
     * @return
     */
    private boolean hasFormulaEnd(String content, int cursor) {
        int len = FORMULA_WRAPPER_END.length();
        return cursor + len < content.length()
                && FORMULA_WRAPPER_END.equals(content.substring(cursor + 1, cursor + len + 1));
    }

    private boolean isCrossLine(String content) {
        return content.length() > 1
                && content.charAt(content.length() - 1) == '\\'
                && content.charAt(content.length() - 2) == '\\';
    }

    private String processInterFormula(FileContext fileContext, LineIterator iterator, String content) {
        int cursor = 0;

        StringBuilder sb = new StringBuilder();

        while (cursor < content.length()) {
            // escape inner line code
            if (content.charAt(cursor) == '`') {
                // '`' is not allowed in formula
                if (!isInterFormulaStart) {
                    DEFAULT_LOGGER.error("file [{}] contains wrong formula grammar: '`' is not allowed in formula. line content: {}", fileContext.getFile(), content);
                    throw new RuntimeException();
                }

                sb.append(content.charAt(cursor++));

                while (content.charAt(cursor) != '`') {
                    sb.append(content.charAt(cursor++));
                }

                sb.append(content.charAt(cursor++));
            } else if (isInterFormulaBoundary(content, cursor)) {
                if (isInterFormulaStart) {
                    if (hasFormulaStart(content, cursor)) {
                        sb.append("$$");
                    } else {
                        sb.append(FORMULA_WRAPPER_START)
                                .append("$$");
                    }
                } else {
                    if (hasFormulaEnd(content, cursor + 1)) {
                        sb.append("$$");
                    } else {
                        sb.append("$$").append(FORMULA_WRAPPER_END);
                    }
                }
                cursor += 2;
                isInterFormulaStart = !isInterFormulaStart;
                interFormulaCount++;
            } else {
                sb.append(content.charAt(cursor++));
            }
        }

        return sb.toString();
    }

    private boolean isInterFormulaBoundary(String content, int cursor) {
        if (cursor < content.length() - 1
                && content.charAt(cursor) == '$'
                && content.charAt(cursor + 1) == '$'
                && (cursor == content.length() - 2 || content.charAt(cursor + 2) != '$')) {
            if (cursor == 0) {
                return true;
            } else if (cursor == 1) {
                return content.charAt(cursor - 1) != '\\' && content.charAt(cursor - 1) != '$';
            } else {
                return content.charAt(cursor - 1) != '\\' && !(content.charAt(cursor - 1) == '$' && content.charAt(cursor - 2) != '\\');
            }
        }
        return false;
    }
}
