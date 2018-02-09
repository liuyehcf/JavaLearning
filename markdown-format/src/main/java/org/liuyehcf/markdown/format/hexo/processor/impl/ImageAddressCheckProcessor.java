package org.liuyehcf.markdown.format.hexo.processor.impl;

import org.liuyehcf.markdown.format.hexo.processor.PreFileProcessor;
import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;

import java.io.File;
import java.util.regex.Matcher;

import static org.liuyehcf.markdown.format.hexo.constant.RegexConstant.IMAGE_PATTERN;
import static org.liuyehcf.markdown.format.hexo.log.CommonLogger.DEFAULT_LOGGER;

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