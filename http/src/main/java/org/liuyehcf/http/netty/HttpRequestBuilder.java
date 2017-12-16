package org.liuyehcf.http.netty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HCF on 2017/12/16.
 */
public class HttpRequestBuilder {

    private static final String SPACE = " ";

    private static final String ENTER = "\r";

    private static final String LINE_FEED = "\n";

    private static final String COLON = ":";
    private final Map<String, String> headers = new HashMap<>();
    private String method = "GET";
    private String url = null;
    private String version = "HTTP/1.1";
    private String body = null;

    public HttpRequestBuilder method(String method) {
        this.method = method;
        return this;
    }

    public HttpRequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public HttpRequestBuilder version(String version) {
        this.version = version;
        return this;
    }

    public HttpRequestBuilder addHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    public HttpRequestBuilder body(String body) {
        this.body = body;
        return this;
    }

    public String build() {
        if (url == null) {
            throw new RuntimeException("url尚未初始化");
        }
        return method + SPACE + url + SPACE + version + ENTER + LINE_FEED
                + headers()
                + emptyLine()
                + (body == null ? "" : body);
    }

    private String headers() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            sb.append(header.getKey())
                    .append(COLON)
                    .append(header.getValue())
                    .append(ENTER)
                    .append(LINE_FEED);
        }
        return sb.toString();
    }

    private String emptyLine() {
        return LINE_FEED;
    }
}
