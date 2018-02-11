package org.liuyehcf.markdown.format.hexo.processor.impl;

import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineElement;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;
import org.liuyehcf.markdown.format.hexo.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.hexo.processor.PreFileProcessor;

import java.util.regex.Matcher;

import static org.liuyehcf.markdown.format.hexo.constant.RegexConstant.INNER_LINK_PATTERN;
import static org.liuyehcf.markdown.format.hexo.log.DefaultLogger.DEFAULT_LOGGER;

/**
 * Created by HCF on 2018/1/14.
 */
public class InnerLinkCheckProcessor extends AbstractFileProcessor implements PreFileProcessor {

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        LineElement lineElement = iterator.getCurrentLineElement();

        // escape code line
        if (!lineElement.isCode()) {

            String content = lineElement.getContent();

            Matcher m = INNER_LINK_PATTERN.matcher(content);

            while (m.find()) {
                if (!fileContext.containsFile(m.group(1))) {
                    DEFAULT_LOGGER.error("file '{}', Inner link '{}'  error", fileContext.getFile(), m.group(0));
                    throw new RuntimeException();
                }
            }
        }
    }
}
