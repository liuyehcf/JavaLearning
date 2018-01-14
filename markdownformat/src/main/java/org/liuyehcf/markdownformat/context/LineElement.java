package org.liuyehcf.markdownformat.context;

/**
 * Created by HCF on 2018/1/14.
 */
public interface LineElement {
    boolean isCode();

    String getContent();

    void setContent(String content);
}
