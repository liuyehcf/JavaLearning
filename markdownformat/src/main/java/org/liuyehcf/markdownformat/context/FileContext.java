package org.liuyehcf.markdownformat.context;

import java.io.File;
import java.util.List;

/**
 * Created by HCF on 2018/1/13.
 */
public interface FileContext {
    LineContext getCurrentLineContext();

    File getRootDirectory();

    File getFileDirectory();

    File getImageDirectory();

    boolean isFinished();

    void moveForward();
}
