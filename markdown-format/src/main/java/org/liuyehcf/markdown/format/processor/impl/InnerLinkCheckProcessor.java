package org.liuyehcf.markdown.format.processor.impl;

import org.liuyehcf.markdown.format.constant.RegexConstant;
import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.LineElement;
import org.liuyehcf.markdown.format.context.LineIterator;
import org.liuyehcf.markdown.format.log.DefaultLogger;
import org.liuyehcf.markdown.format.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.processor.PreFileProcessor;

import java.util.regex.Matcher;

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

            Matcher m = RegexConstant.INNER_LINK_PATTERN.matcher(content);

            while (m.find()) {
                if (!fileContext.containsFile(m.group(1))) {
                    DefaultLogger.LOGGER.error("file '{}', Inner link '{}'  error", fileContext.getFile(), m.group(0));
                    throw new RuntimeException();
                }
            }
        }
    }
}
