package org.liuyehcf.markdown.format.constant;

import java.util.regex.Pattern;

public class RegexConstant {
    private static final String RESOURCE_REGEX = "!?\\[.*?\\]\\((.*)\\)";
    public static final Pattern RESOURCE_PATTERN = Pattern.compile(RESOURCE_REGEX);

    private static final String INNER_LINK_REGEX = "\\{% post_link (.*?) %\\}";
    public static final Pattern INNER_LINK_PATTERN = Pattern.compile(INNER_LINK_REGEX);

    private static final String CONTROL_CHARACTER_REGEX = "\u0008";
    public static final Pattern CONTROL_CHARACTER_PATTERN = Pattern.compile(CONTROL_CHARACTER_REGEX);

    private static final String SUB_ITEM_REGEX = "^\\s*(\\*|[0-9]+\\.) ";
    public static final Pattern SUB_ITEM_PATTERN = Pattern.compile(SUB_ITEM_REGEX);

    private static final String TABLE_REGEX = " *\\|.*?\\| *";
    public static final Pattern TABLE_PATTERN = Pattern.compile(TABLE_REGEX);

    private static final String PROPERTY_REGEX = "^\\s*(.*?)\\s*:\\s*(.*?)\\s*$";
    public static final Pattern PROPERTY_PATTERN = Pattern.compile(PROPERTY_REGEX);

    private static final String SUB_PROPERTY_REGEX = "^\\s*-\\s+(.*)$";
    public static final Pattern SUB_PROPERTY_PATTERN = Pattern.compile(SUB_PROPERTY_REGEX);

    private static final String INNER_FORMULA_REGEX = "((\\{% raw %\\})?+)(\\$++)(.*?[^\\\\])(\\$++)((\\{% endraw %\\})?+)";
    public static final Pattern INNER_FORMULA_PATTERN = Pattern.compile(INNER_FORMULA_REGEX);

    private static final String INTER_FORMULA_REGEX = "((\\{% raw %\\})?+)(\\$\\$++)((\\{% endraw %\\})?+)";
    public static final Pattern INTER_FORMULA_PATTERN = Pattern.compile(INTER_FORMULA_REGEX);

    private static final String ILLEGAL_FORMULA_REGEX = "`.*?\\$.*?`";
    public static final Pattern ILLEGAL_FORMULA_PATTERN = Pattern.compile(ILLEGAL_FORMULA_REGEX);
}
