package org.liuyehcf.markdown.format.hexo.constant;

import java.util.regex.Pattern;

public class RegexConstant {
    private static final String IMAGE_REGEX = "!\\[.*?\\]\\((.*)\\)";
    public static final Pattern IMAGE_PATTERN = Pattern.compile(IMAGE_REGEX);
    private static final String INNER_LINK_REGEX = "\\{% post_link (.*?) %\\}";
    public static final Pattern INNER_LINK_PATTERN = Pattern.compile(INNER_LINK_REGEX);
    private static final String CONTROL_CHARACTER_REGEX = "\u0008";
    public static final Pattern CONTROL_CHARACTER_PATTERN = Pattern.compile(CONTROL_CHARACTER_REGEX);
    private static final String RESOURCE_LINK_REGEX = "!{0,1}\\[[^\\]]*\\]\\([^\\)]*\\)";
    public static final Pattern RESOURCE_LINK_PATTERN = Pattern.compile(RESOURCE_LINK_REGEX);
    private static final String SUB_ITEM_REGEX = "^\\s*(\\*|[0-9]+\\.) ";
    public static final Pattern SUB_ITEM_PATTERN = Pattern.compile(SUB_ITEM_REGEX);
    private static final String TABLE_REGEX = " *\\|.*?\\| *";
    public static final Pattern TABLE_PATTERN = Pattern.compile(TABLE_REGEX);

    public static void main(String[] args) {
        System.out.println(TABLE_PATTERN.matcher("| asdf | asdf |").matches());
    }
}
