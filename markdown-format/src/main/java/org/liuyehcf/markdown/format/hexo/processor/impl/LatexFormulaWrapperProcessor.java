package org.liuyehcf.markdown.format.hexo.processor.impl;

import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineElement;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;
import org.liuyehcf.markdown.format.hexo.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.hexo.processor.PreFileProcessor;

import java.util.regex.Matcher;

import static org.liuyehcf.markdown.format.hexo.constant.RegexConstant.*;
import static org.liuyehcf.markdown.format.hexo.constant.StringConstant.*;
import static org.liuyehcf.markdown.format.hexo.log.DefaultLogger.DEFAULT_LOGGER;

public class LatexFormulaWrapperProcessor extends AbstractFileProcessor implements PreFileProcessor {
    private boolean isInterFormulaStart;
    private int interFormulaCount;

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        LineElement lineElement = iterator.getCurrentLineElement();

        // escape code line
        if (isMathFile(fileContext)
                && !lineElement.isCode()) {
            String content = lineElement.getContent();

            if (isIllegal(content)) {
                DEFAULT_LOGGER.error("file [{}] contains wrong formula grammar `$`", fileContext.getFile());
                throw new RuntimeException();
            }

            StringBuffer stringBuffer = new StringBuffer();
            Matcher innerMatcher = INNER_FORMULA_PATTERN.matcher(content);
            while (innerMatcher.find()) {
                int startOfGroup3 = innerMatcher.start(3);

                // 普通$...$
                boolean case1 = "$".equals(innerMatcher.group(3))
                        && (startOfGroup3 == 0 || content.charAt(startOfGroup3 - 1) != '\\')
                        && "$".equals(innerMatcher.group(5));

                // \$$...$
                boolean case2 = "$$".equals(innerMatcher.group(3))
                        && (startOfGroup3 > 0 && content.charAt(startOfGroup3 - 1) == '\\')
                        && "$".equals(innerMatcher.group(5));

                String extra = "";
                // 补上原本不该捕获的，已经被转义的"$"
                if (case2) {
                    extra = "\\$";
                }

                if (case1 || case2) {
                    innerMatcher.appendReplacement(
                            stringBuffer,
                            extra + FORMULA_WRAPPER_START + "\\$" + "$4" + "\\$" + FORMULA_WRAPPER_END);
                }
            }
            innerMatcher.appendTail(stringBuffer);
            content = stringBuffer.toString();


            stringBuffer = new StringBuffer();
            Matcher interMatcher = INTER_FORMULA_PATTERN.matcher(content);
            while (interMatcher.find()) {
                int startOfGroup3 = interMatcher.start(3);

                // 普通$$
                boolean case1 = "$$".equals(interMatcher.group(3))
                        && (startOfGroup3 == 0 || content.charAt(startOfGroup3 - 1) != '\\');

                // \$$$
                boolean case2 = "$$$".equals(interMatcher.group(3))
                        && (startOfGroup3 > 0 && content.charAt(startOfGroup3 - 1) == '\\');

                String extra = "";
                // 补上原本不该捕获的，已经被转义的"$"
                if (case2) {
                    extra = "\\$";
                }

                if (case1 || case2) {
                    if (isInterFormulaStart) {
                        if (FORMULA_WRAPPER_END.equals(interMatcher.group(4))) {
                            DEFAULT_LOGGER.error("file [{}] contains wrong formula wrapper", fileContext.getFile());
                            throw new RuntimeException();
                        }
                        interMatcher.appendReplacement(
                                stringBuffer,
                                extra + FORMULA_WRAPPER_START + "\\$\\$");
                    } else {
                        if (FORMULA_WRAPPER_START.equals(interMatcher.group(1))) {
                            DEFAULT_LOGGER.error("file [{}] contains wrong formula wrapper", fileContext.getFile());
                            throw new RuntimeException();
                        }
                        interMatcher.appendReplacement(
                                stringBuffer,
                                extra + "\\$\\$" + FORMULA_WRAPPER_END);
                    }
                    isInterFormulaStart = !isInterFormulaStart;
                    interFormulaCount++;
                }
            }
            interMatcher.appendTail(stringBuffer);
            content = stringBuffer.toString();

            lineElement.setContent(content);
        }
    }

    @Override
    protected void beforeProcess(FileContext fileContext) {
        isInterFormulaStart = true;
        interFormulaCount = 0;
    }

    @Override
    protected void afterProcess(FileContext fileContext) {
        if ((interFormulaCount & 1) != 0) {
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

    private boolean isIllegal(String content) {
        Matcher m = ILLEGAL_FORMULA_PATTERN.matcher(content);
        return m.find();
    }
}
