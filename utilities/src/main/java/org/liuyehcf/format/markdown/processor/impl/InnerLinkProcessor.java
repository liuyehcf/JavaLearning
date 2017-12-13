package org.liuyehcf.format.markdown.processor.impl;

import org.liuyehcf.format.markdown.context.FormatContext;
import org.liuyehcf.format.markdown.context.LineElement;
import org.liuyehcf.format.markdown.processor.AbstractLineProcessor;

import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HCF on 2017/7/25.
 */
public class InnerLinkProcessor extends AbstractLineProcessor {
    static final String INNER_LINK_REGEX = "\\{% post_link (.*?) %\\}";

    static final Pattern INNER_LINK_PATTERN = Pattern.compile(INNER_LINK_REGEX);


    @Override
    public void process(FormatContext context, ListIterator<LineElement> lineContentListIterator) {

        getContextLineElement(lineContentListIterator);

        // escape code line
        if (curContentIsCode()) return;

        String content = getContentOfCurLineElement();

        Matcher m = INNER_LINK_PATTERN.matcher(content);

        while (m.find()) {
            if (!context.containsFileName(m.group(1))) {
                System.err.println(context.getFile().getName() + " : " + m.group());
            }
        }
    }

    @Override
    public void reset() {

    }
}
