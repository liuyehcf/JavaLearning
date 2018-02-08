package org.liuyehcf.markdownformat.processor.impl;

import org.liuyehcf.markdownformat.context.FileContext;
import org.liuyehcf.markdownformat.context.LineElement;
import org.liuyehcf.markdownformat.context.LineIterator;
import org.liuyehcf.markdownformat.processor.PreFileProcessor;

/**
 * Created by HCF on 2018/1/14.
 */
public class CodeProcessor implements PreFileProcessor {
    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {


            if (iterator.getCurrentLineElement().isCode()) {

                LineElement lineElement = iterator.getCurrentLineElement();
                String content = lineElement.getContent();

                content = content.replaceAll("// *", "// ");

                lineElement.setContent(content);
            }
            iterator.moveForward();
        }
    }
}
