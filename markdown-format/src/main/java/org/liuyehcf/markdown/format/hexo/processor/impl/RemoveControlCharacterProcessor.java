package org.liuyehcf.markdown.format.hexo.processor.impl;

import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineElement;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;
import org.liuyehcf.markdown.format.hexo.log.DefaultLogger;
import org.liuyehcf.markdown.format.hexo.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.hexo.processor.PreFileProcessor;

import static org.liuyehcf.markdown.format.hexo.constant.RegexConstant.CONTROL_CHARACTER_PATTERN;
import static org.liuyehcf.markdown.format.hexo.log.DefaultLogger.DEFAULT_LOGGER;

public class RemoveControlCharacterProcessor extends AbstractFileProcessor implements PreFileProcessor {

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        LineElement lineElement = iterator.getCurrentLineElement();

        String content = lineElement.getContent();

        if (containsControlChar(content)) {
            content = content.replaceAll(CONTROL_CHARACTER_PATTERN.pattern(), "");

            DEFAULT_LOGGER.info("file '{}' contains invisible character \\u0008", fileContext.getFile());

            lineElement.setContent(content);
        }
    }

    private boolean containsControlChar(String s) {
        return CONTROL_CHARACTER_PATTERN.matcher(s).find();
    }
}
