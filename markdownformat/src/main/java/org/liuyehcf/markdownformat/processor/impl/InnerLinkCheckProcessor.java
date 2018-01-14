package org.liuyehcf.markdownformat.processor.impl;

import org.liuyehcf.markdownformat.context.FileContext;
import org.liuyehcf.markdownformat.context.LineElement;
import org.liuyehcf.markdownformat.context.LineIterator;
import org.liuyehcf.markdownformat.processor.PreFileProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.liuyehcf.markdownformat.log.CommonLogger.logger;

/**
 * Created by HCF on 2018/1/14.
 */
public class InnerLinkCheckProcessor implements PreFileProcessor {
    static final String INNER_LINK_REGEX = "\\{% post_link (.*?) %\\}";

    static final Pattern INNER_LINK_PATTERN = Pattern.compile(INNER_LINK_REGEX);


    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {
            LineElement lineElement = iterator.getCurrentLineElement();

            // escape code line
            if (!lineElement.isCode()) {

                String content = lineElement.getContent();

                Matcher m = INNER_LINK_PATTERN.matcher(content);

                while (m.find()) {
                    if (!fileContext.containsFile(m.group(1))) {
                        logger.error("file '{}', Inner link '{}'  error", fileContext.getCurrentFile(), m.group(0));
                    }
                }
            }

            iterator.moveForward();
        }
    }
}
