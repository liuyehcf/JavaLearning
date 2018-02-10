package org.liuyehcf.markdown.format.hexo.processor.impl;

import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineElement;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;
import org.liuyehcf.markdown.format.hexo.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.hexo.processor.PreFileProcessor;

import java.util.regex.Matcher;

import static org.liuyehcf.markdown.format.hexo.constant.RegexConstant.INNER_FORMULA_PATTERN;
import static org.liuyehcf.markdown.format.hexo.constant.RegexConstant.INTER_FORMULA_PATTERN;
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

            StringBuffer stringBuffer = new StringBuffer();
            Matcher innerMatcher = INNER_FORMULA_PATTERN.matcher(content);
            while (innerMatcher.find()) {
                if ("$".equals(innerMatcher.group(3))
                        && "$".equals(innerMatcher.group(5))) {
                    if (FORMULA_WRAPPER_START.equals(innerMatcher.group(1))) {
                        innerMatcher.appendReplacement(
                                stringBuffer,
                                FORMULA_WRAPPER_START + "\\$" + "$4" + "\\$" + FORMULA_WRAPPER_END);
                    } else {
                        innerMatcher.appendReplacement(
                                stringBuffer,
                                "$1" + FORMULA_WRAPPER_START + "\\$" + "$4" + "\\$" + FORMULA_WRAPPER_END);
                    }
                }
            }
            innerMatcher.appendTail(stringBuffer);
            content = stringBuffer.toString();


            stringBuffer = new StringBuffer();
            Matcher interMatcher = INTER_FORMULA_PATTERN.matcher(content);
            while (interMatcher.find()) {
                if ("$$".equals(interMatcher.group(3))) {
                    if (isInterFormulaStart) {
                        if (FORMULA_WRAPPER_START.equals(interMatcher.group(1))) {
                            interMatcher.appendReplacement(
                                    stringBuffer,
                                    FORMULA_WRAPPER_START + "\\$\\$");
                        } else {
                            interMatcher.appendReplacement(
                                    stringBuffer,
                                    "$1" + FORMULA_WRAPPER_START + "\\$\\$");
                        }

                    } else {
                        if (FORMULA_WRAPPER_START.equals(interMatcher.group(1))) {
                            DEFAULT_LOGGER.error("file [{}] contains wrong formula wrapper", fileContext.getFile());
                            throw new RuntimeException();
                        }
                        interMatcher.appendReplacement(
                                stringBuffer,
                                "$1" + "\\$\\$" + FORMULA_WRAPPER_END);
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
}
