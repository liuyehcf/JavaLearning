package org.liuyehcf.markdown.format.processor.impl;

import org.liuyehcf.markdown.format.context.DefaultLineElement;
import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.LineElement;
import org.liuyehcf.markdown.format.context.LineIterator;
import org.liuyehcf.markdown.format.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.processor.PreFileProcessor;
import org.liuyehcf.markdown.format.util.LineIteratorUtils;

import java.util.regex.Matcher;

import static org.liuyehcf.markdown.format.constant.RegexConstant.TABLE_PATTERN;

public class TableProcessor extends AbstractFileProcessor implements PreFileProcessor {

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        LineElement lineElement = iterator.getCurrentLineElement();

        String content = lineElement.getContent();

        if (!lineElement.isCode() && isTable(content)) {
            // 首先去除多余的空白
            while (LineIteratorUtils.previousLineIsEmpty(iterator)) {
                iterator.removePreviousLine();
            }

            // 给第一个Table加上前置空白
            if (!(iterator.getPreviousLineElement() != null
                    && isTable(iterator.getPreviousLineElement().getContent()))) {
                iterator.insertPrevious(new DefaultLineElement("", false));
            }
        }
    }

    private boolean isTable(String content) {
        Matcher matcher = TABLE_PATTERN.matcher(content);
        return matcher.matches();
    }
}
