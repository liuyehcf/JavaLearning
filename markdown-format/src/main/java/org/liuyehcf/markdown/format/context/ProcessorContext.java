package org.liuyehcf.markdown.format.context;

import org.liuyehcf.markdown.format.processor.FileProcessor;

/**
 * Created by HCF on 2018/1/14.
 */
public interface ProcessorContext {
    FileProcessor nextProcessor();

    boolean hasNextProcessor();

    void process(FileContext fileContext);
}
