package org.liuyehcf.format.markdown.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by t-chehe on 7/5/2017.
 */
public class FormatUtils {
    private static final String EMPTY_REGEX = "\\S";
    private static final Pattern EMPTY_PATTERN = Pattern.compile(EMPTY_REGEX);

    public static boolean isEmptyLine(String content) {
        Matcher matcher = EMPTY_PATTERN.matcher(content);
        return !matcher.find();
    }

    public static void main(String[] args) {
        System.out.println(isEmptyLine("    "));
    }
}
