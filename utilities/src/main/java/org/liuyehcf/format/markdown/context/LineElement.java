package org.liuyehcf.format.markdown.context;

/**
 * Created by HCF on 2017/8/3.
 */
public class LineElement {
    private final boolean isCode;
    private String content;

    public LineElement(String content, boolean isCode) {
        this.content = content;
        this.isCode = isCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCode() {
        return isCode;
    }
}
