package org.liuyehcf.markdownformat.processor.impl;

import org.liuyehcf.markdownformat.context.DefaultLineElement;
import org.liuyehcf.markdownformat.context.FileContext;
import org.liuyehcf.markdownformat.context.LineElement;
import org.liuyehcf.markdownformat.context.LineIterator;
import org.liuyehcf.markdownformat.processor.PreFileProcessor;

import java.util.regex.Matcher;

import static org.liuyehcf.markdownformat.constant.RegexConstant.SUB_ITEM_PATTERN;
import static org.liuyehcf.markdownformat.util.LineIteratorUtils.previousLineIsEmpty;

/**
 * Created by HCF on 2018/1/14.
 */
public class SubItemProcessor implements PreFileProcessor {

    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {
            LineElement lineElement = iterator.getCurrentLineElement();
            String content = iterator.getCurrentLineElement().getContent();

            if (!lineElement.isCode() && isSubItem(content)) {
                // 首先去除多余的空白
                while (previousLineIsEmpty(iterator)) {
                    iterator.removePreviousLine();
                }

                // 给第一个subItem加上前置空白
                if (!(iterator.getPreviousLineElement() != null
                        && isSubItem(iterator.getPreviousLineElement().getContent()))) {
                    iterator.insertPrevious(new DefaultLineElement("", false));
                }
            }
            iterator.moveForward();
        }
    }

    private boolean isSubItem(String content) {
        Matcher matcher = SUB_ITEM_PATTERN.matcher(content);
        return matcher.find();
    }
}
