package org.liuyehcf.markdownformat.util;

import org.liuyehcf.markdownformat.context.LineIterator;

/**
 * Created by HCF on 2018/1/14.
 */
public class LineIteratorUtils {
    public static boolean previousLineIsEmpty(LineIterator iterator) {
        String previousLine = iterator.getPreviousLineElement().getContent();

        return (previousLine != null
                && StringUtils.isBlankLine(previousLine));
    }

    public static boolean nextLineIsEmpty(LineIterator iterator) {
        String nextLine = iterator.getNextLineElement().getContent();

        return (nextLine != null
                && StringUtils.isBlankLine(nextLine));
    }
}
