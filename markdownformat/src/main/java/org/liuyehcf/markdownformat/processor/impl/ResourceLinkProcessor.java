package org.liuyehcf.markdownformat.processor.impl;

import org.liuyehcf.markdownformat.context.DefaultLineElement;
import org.liuyehcf.markdownformat.context.FileContext;
import org.liuyehcf.markdownformat.context.LineElement;
import org.liuyehcf.markdownformat.context.LineIterator;
import org.liuyehcf.markdownformat.processor.PreFileProcessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.liuyehcf.markdownformat.processor.impl.SubItemProcessor.SUB_ITEM_PATTERN;
import static org.liuyehcf.markdownformat.util.LineIteratorUtils.nextLineIsEmpty;
import static org.liuyehcf.markdownformat.util.LineIteratorUtils.previousLineIsEmpty;

/**
 * Created by HCF on 2018/1/14.
 */
public class ResourceLinkProcessor implements PreFileProcessor {

    private static final String RESOURCE_LINK_REGEX = "!{0,1}\\[[^\\]]*\\]\\([^\\)]*\\)";

    private static final Pattern RESOURCE_LINK_PATTERN = Pattern.compile(RESOURCE_LINK_REGEX);


    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {

            LineElement lineElement = iterator.getCurrentLineElement();
            String content;

            if (!lineElement.isCode()
                    && !isSubItem((content = lineElement.getContent()))
                    && isMatched(content)) {

                if (!previousLineIsEmpty(iterator)) {
                    iterator.insertPrevious(new DefaultLineElement("", false));
                }

                if (!nextLineIsEmpty(iterator)) {
                    iterator.insertNext(new DefaultLineElement("", false));
                }
            }

            iterator.moveForward();
        }
    }

    private boolean isMatched(String content) {
        Matcher matcher = RESOURCE_LINK_PATTERN.matcher(content);
        return matcher.find();
    }

    private boolean isSubItem(String content) {
        Matcher matcher1 = SUB_ITEM_PATTERN.matcher(content);
        return matcher1.find();
    }

}
