package org.liuyehcf.markdownformat.context;

/**
 * Created by HCF on 2018/1/14.
 */
public interface LineContext {

    boolean hasPreviousLine();

    boolean hasNextLine();

    String getPreviousLine();

    String getCurrentLine();

    String getNextLine();

    void moveForward();

    void moveBackward();
}
