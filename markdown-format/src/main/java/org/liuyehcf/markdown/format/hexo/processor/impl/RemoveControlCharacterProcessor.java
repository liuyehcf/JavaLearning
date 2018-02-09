package org.liuyehcf.markdown.format.hexo.processor.impl;

import org.liuyehcf.markdown.format.hexo.log.CommonLogger;
import org.liuyehcf.markdown.format.hexo.processor.PreFileProcessor;
import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineElement;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;

import static org.liuyehcf.markdown.format.hexo.constant.RegexConstant.CONTROL_CHARACTER_PATTERN;

public class RemoveControlCharacterProcessor implements PreFileProcessor {

    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {
            LineElement lineElement = iterator.getCurrentLineElement();

            String content = lineElement.getContent();

            if (containsControlChar(content)) {
                content = content.replaceAll(CONTROL_CHARACTER_PATTERN.pattern(), "");

                CommonLogger.DEFAULT_LOGGER.info("file '{}' contains invisible character \\u0008", fileContext.getCurrentFile());

                lineElement.setContent(content);
            }

            iterator.moveForward();
        }
    }

    private boolean containsControlChar(String s) {
        return CONTROL_CHARACTER_PATTERN.matcher(s).find();
    }
}
