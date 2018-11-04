package org.liuyehcf.http.raw.nio;

import org.liuyehcf.http.raw.HttpRequestBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HCF on 2017/12/16.
 */
public class JavaNioClient {

    private static byte[] toByteArray(List<Byte> bytes) {
        byte[] byteArray = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++) {
            byteArray[i] = bytes.get(i);
        }
        return byteArray;
    }

    public static void main(String[] args) {
        new HomeHttpRequest().doRequest();

        new LoginHttpRequest("张三").doRequest();

        new ComputeHttpRequest("1.2", "2.4", "+").doRequest();
    }

    private static abstract class RawHttpRequestTemplate {
        final void doRequest() {
            try {
                SocketChannel socketChannel = SocketChannel.open();

                // 需要启动String Boot模块中的web应用作为服务端
                socketChannel.connect(new InetSocketAddress("localhost", 8080));

                String requestContent = buildRequest();
                printRequest(requestContent);

                ByteBuffer requestByteBuffer = ByteBuffer.wrap(requestContent.getBytes());
                socketChannel.write(requestByteBuffer);

                ByteBuffer responseByteBuffer = ByteBuffer.allocate(16);
                List<Byte> bytes = new ArrayList<>();

                while ((socketChannel.read(responseByteBuffer)) != -1) {
                    responseByteBuffer.flip();

                    while (responseByteBuffer.hasRemaining()) {
                        bytes.add(responseByteBuffer.get());
                    }

                    responseByteBuffer.clear();
                }

                printResponse(new String(toByteArray(bytes)));

                System.out.println("-------------------------------------------");

            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }

        protected abstract String buildRequest();

        private void printRequest(String request) {
            System.out.println("HTTP REQUEST:");
            System.out.println("[");
            System.out.println(request);
            System.out.println("]");
        }

        private void printResponse(String response) {
            System.out.println(",");
            System.out.println("HTTP RESPONSE:");
            System.out.println("[");
            System.out.println(response);
            System.out.println("]");
        }
    }


    private static final class HomeHttpRequest extends RawHttpRequestTemplate {
        @Override
        protected String buildRequest() {
            return HttpRequestBuilder.builder()
                    .method("GET")
                    .url("http://127.0.0.1:8080/home")
                    .addHeader("Host", "8080")
                    .addHeader("Connection", "close")  // 避免read阻塞
                    .build();
        }
    }

    private static final class ComputeHttpRequest extends RawHttpRequestTemplate {
        private String value1;

        private String value2;

        private String operator;

        private ComputeHttpRequest(String value1, String value2, String operator) {
            this.value1 = value1;
            this.value2 = value2;
            this.operator = operator;
        }

        @Override
        protected String buildRequest() {
            return HttpRequestBuilder.builder()
                    .method("GET")
                    .url("http://127.0.0.1:8080/compute?value1=" + value1 + "&value2=" + value2)
                    .addHeader("Host", "8080")
                    .addHeader("Connection", "close")  // 避免read阻塞
                    .addHeader("operator", operator)
                    .build();
        }
    }

    private static final class LoginHttpRequest extends RawHttpRequestTemplate {
        private String name;

        private LoginHttpRequest(String name) {
            this.name = name;
        }

        @Override
        protected String buildRequest() {
            return HttpRequestBuilder.builder()
                    .method("POST")
                    .url("http://127.0.0.1:8080/login")
                    .addHeader("Host", "8080")
                    .addHeader("Connection", "close")  // 避免read阻塞
                    .addHeader("Content-Type", "application/json")
                    .body("{\"name\":\"" + this.name + "\"}") // JSON格式的请求包体
                    .build();
        }
    }
}
