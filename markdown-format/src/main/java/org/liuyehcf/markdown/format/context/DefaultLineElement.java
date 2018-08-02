package org.liuyehcf.markdown.format.context;

/**
 * Created by HCF on 2018/1/14.
 */
public class DefaultLineElement implements LineElement {
    private final boolean isCode;
    private String content;

    public DefaultLineElement(String content, boolean isCode) {
        this.isCode = isCode;
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean isCode() {
        return isCode;
    }

}
