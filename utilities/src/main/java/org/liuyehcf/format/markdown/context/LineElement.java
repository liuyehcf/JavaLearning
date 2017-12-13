package org.liuyehcf.format.markdown.context;

/**
 * Created by HCF on 2017/8/3.
 */
public class LineElement {
    private String content;

    private final boolean isCode;

    public LineElement(String content, boolean isCode) {
        this.content = content;
        this.isCode = isCode;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public boolean isCode() {
        return isCode;
    }
}
