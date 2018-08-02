package org.liuyehcf.markdown.format.processor.impl;

import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.LineIterator;
import org.liuyehcf.markdown.format.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.processor.PreFileProcessor;

import java.io.File;
import java.util.regex.Matcher;

import static org.liuyehcf.markdown.format.constant.RegexConstant.IMAGE_PATTERN;
import static org.liuyehcf.markdown.format.log.DefaultLogger.LOGGER;

/**
 * Created by HCF on 2018/1/14.
 */
public class ImageAddressCheckProcessor extends AbstractFileProcessor implements PreFileProcessor {

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        String content = iterator.getCurrentLineElement().getContent();

        String relativeImagePath;

        if ((relativeImagePath = getImagePath(content)) != null) {
            String absoluteImagePath = fileContext.getRootDirectory().getAbsolutePath() + relativeImagePath;

            File image = new File(absoluteImagePath);

            if (!(image.exists() && image.isFile())) {
                LOGGER.error("file [{}] contains wrong image source [{}]", fileContext.getFile(), relativeImagePath);
                throw new RuntimeException();
            }
        }
    }

    private String getImagePath(String content) {
        Matcher m = IMAGE_PATTERN.matcher(content);
        if (!m.find()) {
            return null;
        }
        return m.group(1);
    }
}
