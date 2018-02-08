package org.liuyehcf.markdownformat.processor.impl;

import org.liuyehcf.markdownformat.context.FileContext;
import org.liuyehcf.markdownformat.context.LineIterator;
import org.liuyehcf.markdownformat.processor.PostFileProcessor;

import static org.liuyehcf.markdownformat.util.LineIteratorUtils.currentLineIsEmpty;
import static org.liuyehcf.markdownformat.util.LineIteratorUtils.previousLineIsEmpty;

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
}
