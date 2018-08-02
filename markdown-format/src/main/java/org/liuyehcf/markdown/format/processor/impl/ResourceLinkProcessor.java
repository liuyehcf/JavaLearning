package org.liuyehcf.markdown.format.processor.impl;

import org.liuyehcf.markdown.format.context.DefaultLineElement;
import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.LineElement;
import org.liuyehcf.markdown.format.context.LineIterator;
import org.liuyehcf.markdown.format.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.processor.PreFileProcessor;
import org.liuyehcf.markdown.format.util.LineIteratorUtils;

import java.util.regex.Matcher;

import static org.liuyehcf.markdown.format.constant.RegexConstant.*;

/**
 * Created by HCF on 2018/1/14.
 */
public class ResourceLinkProcessor extends AbstractFileProcessor implements PreFileProcessor {

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        LineElement lineElement = iterator.getCurrentLineElement();
        String content;

        if (!lineElement.isCode()
                && !isSubItem((content = lineElement.getContent()))
                && !isTable(content)
                && isResourceLink(content)) {

            if (!LineIteratorUtils.previousLineIsEmpty(iterator)) {
                iterator.insertPrevious(new DefaultLineElement("", false));
            }

            if (!LineIteratorUtils.nextLineIsEmpty(iterator)) {
                iterator.insertNext(new DefaultLineElement("", false));
            }
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
        Matcher matcher = TABLE_PATTERN.matcher(content);
        return matcher.find();
    }
}
