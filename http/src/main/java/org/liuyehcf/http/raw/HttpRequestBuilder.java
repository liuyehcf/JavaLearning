package org.liuyehcf.http.raw;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HCF on 2017/12/16.
 */
public class HttpRequestBuilder {

    private static final String SPACE = " ";
    private static final String ENTER = "\r";
    private static final String LINE_FEED = "\n";
    private static final String CONTENT_LENGTH = "content-length";
    private static final String COLON = ":";

    private Map<String, String> headers;
    private String method;
    private String url;
    private String version;
    private String body;

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    /**
     * 构造方法，填充默认值
     */
    private HttpRequestBuilder() {
        headers = new HashMap<>();
        method = "GET";
        url = null;
        version = "HTTP/1.1";
        body = null;
    }

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
        headers.put(key.toLowerCase(), value.toLowerCase());
        return this;
    }

    public HttpRequestBuilder body(String body) {
        this.body = body;
        addHeader(CONTENT_LENGTH, Integer.toString(body.getBytes().length));
        return this;
    }

    public String build() {
        check();

        return method + SPACE + url + SPACE + version + ENTER + LINE_FEED
                + headers()
                + LINE_FEED
                + (body == null ? "" : body);
    }

    private void check() {
        if (url == null) {
            throw new RuntimeException("url尚未初始化");
        }

        if (body != null) {
            int bodyLength = body.getBytes().length;
            if (!headers.containsKey(CONTENT_LENGTH)) {
                throw new RuntimeException("设置了请求Body，单位设置长度参数<content-length>");
            }

            String key = headers.get(CONTENT_LENGTH);

            if (Integer.parseInt(key) != bodyLength) {
                throw new RuntimeException("Body长度参数<content-length>设置错误");
            }
        } else {
            String key;
            if (headers.containsKey(CONTENT_LENGTH)
                    && (key = headers.get(CONTENT_LENGTH)) != null
                    && Integer.parseInt(key) != 0) {
                throw new RuntimeException("Body为空，但是Body长度参数<content-length>不为0");
            }
        }
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
}
