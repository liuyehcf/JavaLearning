package org.liuyehcf.markdown.format.processor.impl;

import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.LineIterator;
import org.liuyehcf.markdown.format.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.processor.PostFileProcessor;

import static org.liuyehcf.markdown.format.util.LineIteratorUtils.currentLineIsEmpty;
import static org.liuyehcf.markdown.format.util.LineIteratorUtils.previousLineIsEmpty;

/**
 * Created by HCF on 2018/1/14.
 */
public class RedundantEmptyProcessor extends AbstractFileProcessor implements PostFileProcessor {

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        if (currentLineIsEmpty(iterator)
                && previousLineIsEmpty(iterator)) {
            iterator.removePreviousLine();
        }
    }
}
