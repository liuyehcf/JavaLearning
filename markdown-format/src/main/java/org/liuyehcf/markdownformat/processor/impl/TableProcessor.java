package org.liuyehcf.markdownformat.processor.impl;

import org.liuyehcf.markdownformat.context.DefaultLineElement;
import org.liuyehcf.markdownformat.context.FileContext;
import org.liuyehcf.markdownformat.context.LineElement;
import org.liuyehcf.markdownformat.context.LineIterator;
import org.liuyehcf.markdownformat.processor.PreFileProcessor;

import java.util.regex.Matcher;

import static org.liuyehcf.markdownformat.constant.RegexConstant.TABLE_PATTERN;
import static org.liuyehcf.markdownformat.util.LineIteratorUtils.previousLineIsEmpty;

public class TableProcessor implements PreFileProcessor {
    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {
            LineElement lineElement = iterator.getCurrentLineElement();

            String content = lineElement.getContent();

            if (!lineElement.isCode() && isTable(content)) {
                // 首先去除多余的空白
                while (previousLineIsEmpty(iterator)) {
                    iterator.removePreviousLine();
                }

                // 给第一个Table加上前置空白
                if (!(iterator.getPreviousLineElement() != null
                        && isTable(iterator.getPreviousLineElement().getContent()))) {
                    iterator.insertPrevious(new DefaultLineElement("", false));
                }
            }

            iterator.moveForward();
        }
    }

    private boolean isTable(String content) {
        Matcher matcher = TABLE_PATTERN.matcher(content);
        return matcher.matches();
    }
}
