package org.liuyehcf.markdown.format.hexo.processor;

import org.liuyehcf.markdown.format.hexo.context.FileContext;

/**
 * Created by HCF on 2018/1/13.
 */
public interface FileProcessor {
    /**
     * 文件处理器的处理方法
     *
     * @param fileContext
     */
    void process(FileContext fileContext);
}
