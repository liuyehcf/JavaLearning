package org.liuyehcf.markdown.format.hexo.processor.impl;

import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;
import org.liuyehcf.markdown.format.hexo.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.hexo.processor.PostFileProcessor;

import static org.liuyehcf.markdown.format.hexo.util.LineIteratorUtils.currentLineIsEmpty;
import static org.liuyehcf.markdown.format.hexo.util.LineIteratorUtils.previousLineIsEmpty;

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
