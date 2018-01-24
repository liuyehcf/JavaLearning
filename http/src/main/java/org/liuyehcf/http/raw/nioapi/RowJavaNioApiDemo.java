package org.liuyehcf.http.raw.nioapi;

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
public class RowJavaNioApiDemo {

    private static byte[] castToPrimitiveByteArray(List<Byte> bytes) {
        byte[] byteArray = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++) {
            byteArray[i] = bytes.get(i);
        }
        return byteArray;
    }

    public static void main(String[] args) {
        new MyHomeHttpClient().doRequest();

        new MyLoginHttpClient("张三", "12345678").doRequest();

        new MyComputeHttpClient("1.2", "2.4", "+").doRequest();
    }

    private static abstract class RawHttpClientTemplate {
        final public void doRequest() {
            try {
                SocketChannel socketChannel = SocketChannel.open();

                // todo 需要启动String Boot模块中的web应用作为服务端
                socketChannel.connect(new InetSocketAddress("localhost", 8080));


                String requestContent = buildRequest();
                System.out.print("\n\n>>>>>>>>>>>>>>>>HTTP REQUEST<<<<<<<<<<<<<<<<\n\n");
                System.out.println(requestContent);


                ByteBuffer requestByteBuffer = ByteBuffer.wrap(requestContent.getBytes());
                socketChannel.write(requestByteBuffer);


                ByteBuffer responseByteBuffer = ByteBuffer.allocate(16);
                List<Byte> receivedBytes = new ArrayList<>();
                int bytesRead;
                while ((bytesRead = socketChannel.read(responseByteBuffer)) != -1) {
                    responseByteBuffer.flip();

                    while (responseByteBuffer.hasRemaining()) {
                        receivedBytes.add(responseByteBuffer.get());
                    }

                    responseByteBuffer.clear();
                }


                System.out.print("\n\n>>>>>>>>>>>>>>>>HTTP RESPONSE<<<<<<<<<<<<<<<<\n\n");
                System.out.println(new String(castToPrimitiveByteArray(receivedBytes)));

            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }

        protected abstract String buildRequest();
    }

    private static final class MyHomeHttpClient extends RawHttpClientTemplate {
        @Override
        protected String buildRequest() {
            HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder();
            httpRequestBuilder
                    .method("GET")
                    .url("http://127.0.0.1:8080/home")
                    .addHeader("Host", "8080")
                    .addHeader("Connection", "close");  // 避免read阻塞
            return httpRequestBuilder.build();
        }
    }

    private static final class MyComputeHttpClient extends RawHttpClientTemplate {
        private String value1;

        private String value2;

        private String operator;

        public MyComputeHttpClient(String value1, String value2, String operator) {
            this.value1 = value1;
            this.value2 = value2;
            this.operator = operator;
        }

        @Override
        protected String buildRequest() {
            HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder();
            httpRequestBuilder
                    .method("GET")
                    .url("http://127.0.0.1:8080/compute?value1=" + value1 + "&value2=" + value2)
                    .addHeader("Host", "8080")
                    .addHeader("Connection", "close")  // 避免read阻塞
                    .addHeader("operator", operator);
            return httpRequestBuilder.build();
        }
    }

    private static final class MyLoginHttpClient extends RawHttpClientTemplate {
        private String name;
        private String password;

        public MyLoginHttpClient(String name, String password) {
            this.name = name;
            this.password = password;
        }

        @Override
        protected String buildRequest() {
            HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder();
            httpRequestBuilder
                    .method("POST")
                    .url("http://127.0.0.1:8080/login")
                    .addHeader("Host", "8080")
                    .addHeader("Connection", "close")  // 避免read阻塞
                    .addHeader("Content-Type", "application/json")
                    .body("{\"name\":\"" + this.name + "\",\"password\":\"" + this.password + "\"}");

            return httpRequestBuilder.build();
        }
    }
}
