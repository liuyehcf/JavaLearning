package org.liuyehcf.markdown.format.hexo.processor.impl;

import org.liuyehcf.markdown.format.hexo.context.DefaultLineElement;
import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineElement;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;
import org.liuyehcf.markdown.format.hexo.processor.PreFileProcessor;
import org.liuyehcf.markdown.format.hexo.util.LineIteratorUtils;

import java.util.regex.Matcher;

import static org.liuyehcf.markdown.format.hexo.constant.RegexConstant.TABLE_PATTERN;

public class TableProcessor implements PreFileProcessor {
    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {
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

            iterator.moveForward();
        }
    }

    private boolean isTable(String content) {
        Matcher matcher = TABLE_PATTERN.matcher(content);
        return matcher.matches();
    }
}
