package org.liuyehcf.markdownformat.processor.impl;

import org.liuyehcf.markdownformat.context.FileContext;
import org.liuyehcf.markdownformat.context.LineIterator;
import org.liuyehcf.markdownformat.processor.PreFileProcessor;

import java.io.File;
import java.util.regex.Matcher;

import static org.liuyehcf.markdownformat.constant.RegexConstant.IMAGE_PATTERN;
import static org.liuyehcf.markdownformat.log.CommonLogger.DEFAULT_LOGGER;

/**
 * Created by HCF on 2018/1/14.
 */
public class ImageAddressCheckProcessor implements PreFileProcessor {

    @Override
    public void process(FileContext fileContext) {
        LineIterator iterator = fileContext.getLineIteratorOfCurrentFile();

        while (iterator.isNotFinish()) {
            String content = iterator.getCurrentLineElement().getContent();

            String relativeImagePath;

            if ((relativeImagePath = getImagePath(content)) != null) {
                String absoluteImagePath = fileContext.getRootDirectory().getAbsolutePath() + relativeImagePath;

                File image = new File(absoluteImagePath);

                if (!(image.exists() && image.isFile())) {
                    DEFAULT_LOGGER.error("file [{}] contains wrong image source [{}]", fileContext.getCurrentFile(), relativeImagePath);
                }
            }
            iterator.moveForward();
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