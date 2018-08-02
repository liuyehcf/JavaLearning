package org.liuyehcf.markdown.format.processor.impl;

import org.liuyehcf.markdown.format.constant.RegexConstant;
import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.LineElement;
import org.liuyehcf.markdown.format.context.LineIterator;
import org.liuyehcf.markdown.format.log.DefaultLogger;
import org.liuyehcf.markdown.format.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.processor.PreFileProcessor;

public class RemoveControlCharacterProcessor extends AbstractFileProcessor implements PreFileProcessor {

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        LineElement lineElement = iterator.getCurrentLineElement();

        String content = lineElement.getContent();

        if (containsControlChar(content)) {
            content = content.replaceAll(RegexConstant.CONTROL_CHARACTER_PATTERN.pattern(), "");

            DefaultLogger.LOGGER.info("file '{}' contains invisible character \\u0008", fileContext.getFile());

            lineElement.setContent(content);
        }
    }

    private boolean containsControlChar(String s) {
        return RegexConstant.CONTROL_CHARACTER_PATTERN.matcher(s).find();
    }
}
