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

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        LineElement lineElement = iterator.getCurrentLineElement();

        // escape code line
        if (isMathFile(fileContext)
                && !lineElement.isCode()) {
            String content = lineElement.getContent();

            StringBuffer stringBuffer = new StringBuffer();

            Matcher innerMatcher = INNER_FORMULA_PATTERN.matcher(content);
            while (innerMatcher.find()
                    && innerMatcher.group(3) == null
                    && innerMatcher.group(4) == null
                    && innerMatcher.group(5) == null) {
                innerMatcher.appendReplacement(
                        stringBuffer,
                        FORMULA_WRAPPER_START + "$2" + FORMULA_WRAPPER_END
                );
            }
            innerMatcher.appendTail(stringBuffer);

            content = stringBuffer.toString();

            stringBuffer = new StringBuffer();

            Matcher interMatcher = INTER_FORMULA_PATTERN.matcher(content);
            while (interMatcher.find()
                    && interMatcher.group(3) == null
                    && interMatcher.group(4) == null
                    && interMatcher.group(5) == null) {
                interMatcher.appendReplacement(
                        stringBuffer,
                        isInterFormulaStart ?
                                (FORMULA_WRAPPER_START + "$2")
                                :
                                ("$2" + FORMULA_WRAPPER_END)
                );
                isInterFormulaStart = !isInterFormulaStart;
            }
            interMatcher.appendTail(stringBuffer);

            content = stringBuffer.toString();

            lineElement.setContent(content);
        }
    }

    @Override
    protected void reset() {
        isInterFormulaStart = true;
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

    public static void main(String[] args) {
        boolean isInterFormulaStart = true;
        String content = "$$sdfasdfasdf$$";
        StringBuffer stringBuffer = new StringBuffer();

        Matcher interMatcher = INTER_FORMULA_PATTERN.matcher(content);
        while (interMatcher.find()
                && interMatcher.group(3) == null) {
            interMatcher.appendReplacement(
                    stringBuffer,
                    isInterFormulaStart ?
                            (FORMULA_WRAPPER_START + "$2")
                            :
                            ("$2" + FORMULA_WRAPPER_END)
            );
            isInterFormulaStart = !isInterFormulaStart;
        }
        interMatcher.appendTail(stringBuffer);

        content = stringBuffer.toString();
        System.out.println(content);
    }
}
