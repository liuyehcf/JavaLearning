package org.liuyehcf.format.markdown.processor.impl;

import org.liuyehcf.format.markdown.context.FormatContext;
import org.liuyehcf.format.markdown.context.LineElement;
import org.liuyehcf.format.markdown.processor.AbstractLineProcessor;

import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.liuyehcf.format.markdown.processor.impl.SubItemProcessor.SUB_ITEM_PATTERN;

/**
 * Created by HCF on 2017/8/3.
 */
public class ResourceLinkProcessor extends AbstractLineProcessor {
    static final String RESOURCE_LINK_REGEX = "!{0,1}\\[[^\\]]*\\]\\([^\\)]*\\)";

    static final Pattern RESOURCE_LINK_PATTERN = Pattern.compile(RESOURCE_LINK_REGEX);


    @Override
    public void process(FormatContext context, ListIterator<LineElement> lineContentListIterator) {

        getContextLineElement(lineContentListIterator);

        // escape code line
        if (curContentIsCode()) return;

        String content = getContentOfCurLineElement();

        if (isSubItem(content)) return;

        if (!isMatched(content)) return;

        if (!preContentIsEmpty()) {
            addPrevLineElement(lineContentListIterator, "", false);
        }

        if (!nextContentIsEmpty()) {
            addNextLineElement(lineContentListIterator, "", false);
        }
    }

    @Override
    public void reset() {

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
