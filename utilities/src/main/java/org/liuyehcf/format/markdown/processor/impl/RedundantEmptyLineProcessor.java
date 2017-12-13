package org.liuyehcf.format.markdown.processor.impl;

import org.liuyehcf.format.markdown.context.FormatContext;
import org.liuyehcf.format.markdown.context.LineElement;
import org.liuyehcf.format.markdown.processor.AbstractLineProcessor;

import java.util.ListIterator;

/**
 * Created by HCF on 2017/8/3.
 */
public class RedundantEmptyLineProcessor extends AbstractLineProcessor {

    @Override
    public void process(FormatContext context, ListIterator<LineElement> lineContentListIterator) {

        getContextLineElement(lineContentListIterator);

        String content = getContentOfCurLineElement();

        if (curContentIsEmpty()
                && preContentIsEmpty()) {
            removePrevLineElement(lineContentListIterator);
        }
    }

    @Override
    public void reset() {

    }
}

