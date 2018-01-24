package org.liuyehcf.markdownformat.util;

import org.liuyehcf.markdownformat.context.LineElement;
import org.liuyehcf.markdownformat.context.LineIterator;

/**
 * Created by HCF on 2018/1/14.
 */
public class LineIteratorUtils {
    public static boolean previousLineIsEmpty(LineIterator iterator) {
        LineElement previousLineElement = iterator.getPreviousLineElement();

        return (previousLineElement != null
                && StringUtils.isBlankLine(previousLineElement.getContent()));
    }

    public static boolean nextLineIsEmpty(LineIterator iterator) {
        LineElement nextLineElement = iterator.getNextLineElement();

        return (nextLineElement != null
                && StringUtils.isBlankLine(nextLineElement.getContent()));
    }
}
