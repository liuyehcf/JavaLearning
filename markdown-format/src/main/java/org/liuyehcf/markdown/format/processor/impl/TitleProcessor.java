package org.liuyehcf.markdown.format.processor.impl;

import org.liuyehcf.markdown.format.constant.StringConstant;
import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.LineIterator;
import org.liuyehcf.markdown.format.processor.PreFileProcessor;

import java.util.Objects;

public class TitleProcessor implements PreFileProcessor {

    @Override
    public void process(FileContext fileContext) {
        String fileName = fileContext.getFile().getName();

        if (!fileName.endsWith(StringConstant.MARKDOWN_SUFFIX)) {
            throw new RuntimeException("file not ends with '.md', file=" + fileName);
        }

        String fileSimpleName = fileName.substring(0, fileName.length() - StringConstant.MARKDOWN_SUFFIX.length());

        LineIterator lineIterator = fileContext.getLineIterator();

        // 跳过第一行
        lineIterator.moveForward();

        // 取第二行内容
        String content = lineIterator.getCurrentLineElement().getContent();

        if (!Objects.equals("title: " + fileSimpleName, content)) {
            throw new RuntimeException("title not match, file=" + fileName);
        }
    }
}
