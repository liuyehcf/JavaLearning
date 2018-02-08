package org.liuyehcf.markdownformat.processor.impl;

import org.liuyehcf.markdownformat.context.DefaultLineElement;
import org.liuyehcf.markdownformat.context.FileContext;
import org.liuyehcf.markdownformat.context.LineElement;
import org.liuyehcf.markdownformat.context.LineIterator;
import org.liuyehcf.markdownformat.processor.PreFileProcessor;

import java.util.regex.Matcher;

import static org.liuyehcf.markdownformat.constant.RegexConstant.RESOURCE_LINK_PATTERN;
import static org.liuyehcf.markdownformat.constant.RegexConstant.SUB_ITEM_PATTERN;
import static org.liuyehcf.markdownformat.util.LineIteratorUtils.nextLineIsEmpty;
import static org.liuyehcf.markdownformat.util.LineIteratorUtils.previousLineIsEmpty;

/**
 * Created by HCF on 2018/1/14.
 */
public class ResourceLinkProcessor implements PreFileProcessor {

    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {

            LineElement lineElement = iterator.getCurrentLineElement();
            String content;

            if (!lineElement.isCode()
                    && !isSubItem((content = lineElement.getContent()))
                    && isResourceLink(content)) {

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

    private boolean isResourceLink(String content) {
        Matcher matcher = RESOURCE_LINK_PATTERN.matcher(content);
        return matcher.find();
    }

    private boolean isSubItem(String content) {
        Matcher matcher = SUB_ITEM_PATTERN.matcher(content);
        return matcher.find();
    }

    private boolean isTable(String content) {
//        Matcher matcher=
        return false;
    }

}
