package org.liuyehcf.markdownformat.processor.impl;

import org.liuyehcf.markdownformat.context.FileContext;
import org.liuyehcf.markdownformat.context.LineElement;
import org.liuyehcf.markdownformat.context.LineIterator;
import org.liuyehcf.markdownformat.processor.PreFileProcessor;

import java.util.regex.Pattern;

import static org.liuyehcf.markdownformat.log.CommonLogger.logger;

public class RemoveControlCharacterProcessor implements PreFileProcessor {

    private static final String CONTROL_CHARACTER_REGEX = "\u0008";

    private static final Pattern PATTERN = Pattern.compile(CONTROL_CHARACTER_REGEX);

    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {
            LineElement lineElement = iterator.getCurrentLineElement();

            String content = lineElement.getContent();

            if (isContains(content)) {
                content = content.replaceAll(CONTROL_CHARACTER_REGEX, "");

                logger.info("file '{}' contains invisible character \\u0008", fileContext.getCurrentFile());

                lineElement.setContent(content);
            }

            iterator.moveForward();
        }
    }

    private boolean isContains(String s) {
        return PATTERN.matcher(s).find();
    }
}
