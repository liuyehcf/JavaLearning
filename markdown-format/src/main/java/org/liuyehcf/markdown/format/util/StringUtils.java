package org.liuyehcf.markdown.format.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HCF on 2018/1/14.
 */
public class StringUtils {
    private static final String EMPTY_REGEX = "\\s*";
    private static final Pattern EMPTY_PATTERN = Pattern.compile(EMPTY_REGEX);

    public static boolean isBlankLine(String s) {
        Matcher m = EMPTY_PATTERN.matcher(s);
        return m.matches();
    }

    public static boolean isBlank(String s) {
        return s == null || s.isEmpty();
    }
}
