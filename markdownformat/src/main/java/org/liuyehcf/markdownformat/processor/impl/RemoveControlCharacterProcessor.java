package org.liuyehcf.markdownformat.processor.impl;

import org.liuyehcf.markdownformat.context.FileContext;
import org.liuyehcf.markdownformat.context.LineElement;
import org.liuyehcf.markdownformat.context.LineIterator;
import org.liuyehcf.markdownformat.processor.PreFileProcessor;

import static org.liuyehcf.markdownformat.constant.RegexConstant.CONTROL_CHARACTER_PATTERN;
import static org.liuyehcf.markdownformat.log.CommonLogger.DEFAULT_LOGGER;

public class RemoveControlCharacterProcessor implements PreFileProcessor {

    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {
            LineElement lineElement = iterator.getCurrentLineElement();

            String content = lineElement.getContent();

            if (containsControlChar(content)) {
                content = content.replaceAll(CONTROL_CHARACTER_PATTERN.pattern(), "");

                DEFAULT_LOGGER.info("file '{}' contains invisible character \\u0008", fileContext.getCurrentFile());

                lineElement.setContent(content);
            }

            iterator.moveForward();
        }
    }

    private boolean containsControlChar(String s) {
        return CONTROL_CHARACTER_PATTERN.matcher(s).find();
    }
}
