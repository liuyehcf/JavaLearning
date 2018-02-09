package org.liuyehcf.markdown.format.hexo.context;

import org.liuyehcf.markdown.format.hexo.processor.FileProcessor;

/**
 * Created by HCF on 2018/1/14.
 */
public interface ProcessorContext {
    FileProcessor nextProcessor();

    boolean hasNextProcessor();

    void process(FileContext fileContext);
}
