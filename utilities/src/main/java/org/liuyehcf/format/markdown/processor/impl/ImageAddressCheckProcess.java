package org.liuyehcf.format.markdown.processor.impl;

import org.liuyehcf.format.markdown.context.FormatContext;
import org.liuyehcf.format.markdown.context.LineElement;
import org.liuyehcf.format.markdown.processor.AbstractLineProcessor;

import java.io.File;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HCF on 2018/1/13.
 */
public class ImageAddressCheckProcess extends AbstractLineProcessor {
    private static final String IMAGE_REGEX = "!\\[.*?\\]\\((.*)\\)";
    private static final Pattern IMAGE_PATTERN = Pattern.compile(IMAGE_REGEX);

    @Override
    public void process(FormatContext context, ListIterator<LineElement> lineContentListIterator) {
        getContextLineElement(lineContentListIterator);

        String content = getContentOfCurLineElement();

        String relativeImagePath;

        if ((relativeImagePath = getImagePath(content)) == null) {
            return;
        }

        String absoluteImagePath = context.getSourceDir() + relativeImagePath;

        File image = new File(absoluteImagePath);

        if (!(image.exists() && image.isFile())) {
            printErrorMessage(context.getFile(), relativeImagePath);
        }
    }

    private String getImagePath(String content) {
        Matcher m = IMAGE_PATTERN.matcher(content);
        if (!m.find()) {
            return null;
        }
        return m.group(1);
    }

    @Override
    public void reset() {

    }

    private void printErrorMessage(File file, String relativeImagePath) {
        System.err.println(file.getAbsolutePath() + " : not found image <" + relativeImagePath + ">");
    }
}
