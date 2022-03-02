package org.liuyehcf.markdown.format.processor.impl;

import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.LineIterator;
import org.liuyehcf.markdown.format.processor.AbstractFileProcessor;
import org.liuyehcf.markdown.format.processor.PreFileProcessor;

import java.io.File;
import java.util.regex.Matcher;

import static org.liuyehcf.markdown.format.constant.RegexConstant.RESOURCE_PATTERN;
import static org.liuyehcf.markdown.format.log.DefaultLogger.LOGGER;

/**
 * Created by HCF on 2018/1/14.
 */
public class LocalAddressCheckProcessor extends AbstractFileProcessor implements PreFileProcessor {

    @Override
    protected void doProcess(FileContext fileContext, LineIterator iterator) {
        String content = iterator.getCurrentLineElement().getContent();

        String relativeImagePath;

        if ((relativeImagePath = getResourcePath(content)) != null) {
            if (!relativeImagePath.startsWith("/")) {
                return;
            }
            String absoluteImagePath = fileContext.getRootDirectory().getAbsolutePath() + relativeImagePath;

            File image = new File(absoluteImagePath);

            if (!(image.exists() && image.isFile())) {
                LOGGER.error("file [{}] contains wrong resource source [{}]", fileContext.getFile(), relativeImagePath);
                throw new RuntimeException();
            }
        }
    }

    private String getResourcePath(String content) {
        Matcher m = RESOURCE_PATTERN.matcher(content);
        if (!m.find()) {
            return null;
        }
        return m.group(1);
    }
}
