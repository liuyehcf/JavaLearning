package org.liuyehcf.markdown.format.hexo.processor.impl;

import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineElement;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;
import org.liuyehcf.markdown.format.hexo.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.hexo.processor.PreFileProcessor;

import java.util.regex.Matcher;

import static org.liuyehcf.markdown.format.hexo.constant.RegexConstant.INNER_FORMULA_PATTERN;
import static org.liuyehcf.markdown.format.hexo.constant.StringConstant.*;
import static org.liuyehcf.markdown.format.hexo.log.DefaultLogger.DEFAULT_LOGGER;

public class LatexFormulaWrapperProcessor extends AbstractFileProcessor implements PreFileProcessor {
    boolean isInterFormulaStart;

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        LineElement lineElement = iterator.getCurrentLineElement();

        // escape code line
        if (isMathFile(fileContext)
                && !lineElement.isCode()) {
            String content = lineElement.getContent();
            Matcher innerMatcher = INNER_FORMULA_PATTERN.matcher(content);
            content = innerMatcher.replaceAll(FORMULA_WRAPPER_START + "$3" + FORMULA_WRAPPER_END);


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

    public static void main(String[] args) {
        String content=FORMULA_WRAPPER_START+"$d$"+FORMULA_WRAPPER_END;
        System.out.println(content);
        Matcher innerMatcher = INNER_FORMULA_PATTERN.matcher(content);
        System.out.println(innerMatcher.matches());
        System.out.println(innerMatcher.group(0));
        System.out.println(innerMatcher.group(1));
        System.out.println(innerMatcher.group(2));
        System.out.println(innerMatcher.group(3));
        content = innerMatcher.replaceAll(  "$4" );
        System.out.println(content);
    }
}
