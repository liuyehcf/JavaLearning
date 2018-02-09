package org.liuyehcf.markdown.format.hexo.processor.impl;

import org.liuyehcf.markdown.format.hexo.processor.PostFileProcessor;
import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;

import static org.liuyehcf.markdown.format.hexo.util.LineIteratorUtils.currentLineIsEmpty;
import static org.liuyehcf.markdown.format.hexo.util.LineIteratorUtils.previousLineIsEmpty;

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
