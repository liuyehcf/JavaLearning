package org.liuyehcf.markdownformat.context;

import org.liuyehcf.markdownformat.processor.FileProcessor;

/**
 * Created by HCF on 2018/1/14.
 */
public interface ProcessorContext {
    FileProcessor nextProcessor();

    boolean hasNextProcessor();

    void process(FileContext fileContext);
}
