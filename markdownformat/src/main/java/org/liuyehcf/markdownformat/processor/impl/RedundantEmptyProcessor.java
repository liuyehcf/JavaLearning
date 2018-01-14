package org.liuyehcf.markdownformat.processor.impl;

import org.liuyehcf.markdownformat.context.FileContext;
import org.liuyehcf.markdownformat.context.LineIterator;
import org.liuyehcf.markdownformat.processor.PostFileProcessor;
import org.liuyehcf.markdownformat.util.StringUtils;

/**
 * Created by HCF on 2018/1/14.
 */
public class RedundantEmptyProcessor implements PostFileProcessor {
    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {

            if (currentLineIsEmpty(iterator)
                    && previousLineIsEmpty(iterator)) {
                iterator.removePreviousLine();
            }

            iterator.moveForward();
        }
    }

    private boolean currentLineIsEmpty(LineIterator iterator) {
        String currentLine = iterator.getCurrentLineElement().getContent();

        return (currentLine != null
                && StringUtils.isBlankLine(currentLine));
    }

    private boolean previousLineIsEmpty(LineIterator iterator) {
        String previousLine = iterator.getPreviousLineElement().getContent();

        return (previousLine != null
                && StringUtils.isBlankLine(previousLine));
    }
}
