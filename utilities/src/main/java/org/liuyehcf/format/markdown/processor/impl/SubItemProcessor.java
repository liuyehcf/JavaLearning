package org.liuyehcf.format.markdown.processor.impl;

import org.liuyehcf.format.markdown.context.FormatContext;
import org.liuyehcf.format.markdown.context.LineElement;
import org.liuyehcf.format.markdown.processor.AbstractLineProcessor;

import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by t-chehe on 7/5/2017.
 */
public class SubItemProcessor extends AbstractLineProcessor {
    static final String SUB_ITEM_REGEX = "^\\s*(\\*|[0-9]+\\.) ";

    static final Pattern SUB_ITEM_PATTERN = Pattern.compile(SUB_ITEM_REGEX);


    @Override
    public void process(FormatContext context, ListIterator<LineElement> lineContentListIterator) {

        getContextLineElement(lineContentListIterator);

        // escape code line
        if (curContentIsCode()) return;

        String content = getContentOfCurLineElement();

        if (!isMatched(content)) {
            return;
        }

        // 首先去除多余的空白
        while (preContentIsEmpty()) {
            removePrevLineElement(lineContentListIterator);
        }

        // 给第一个subItem加上前置空白
        if (!(hasPreLineElement()
                && isMatched(getContentOfPreLineElement())))
            addPrevLineElement(lineContentListIterator, "", false);
    }

    @Override
    public void reset() {

    }

    private boolean isMatched(String content) {
        Matcher matcher = SUB_ITEM_PATTERN.matcher(content);
        return matcher.find();
    }
}
