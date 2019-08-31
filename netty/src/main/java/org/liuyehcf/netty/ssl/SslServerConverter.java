package org.liuyehcf.netty.ssl;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

/**
 * @author hechenfeng
 * @date 2019/8/29
 */
public class SslServerConverter extends AbstractSslConverter {

    private static final SslContext SSL_CONTEXT;

    private static final String STORE_TYPE = "PKCS12";
    private static final String KEY_STORE_PASSWORD = "123456";
    private static final String KEY_PASSWORD = KEY_STORE_PASSWORD;

    private static final KeyManagerFactory KEY_MANAGER_FACTORY;

    static {
        try {
            KEY_MANAGER_FACTORY = initKeyManagerFactory();

            SSL_CONTEXT = SslContextBuilder.forServer(KEY_MANAGER_FACTORY).build();
        } catch (Exception e) {
            throw new Error();
        }
    }

    private SslServerConverter(EmbeddedChannel channel) {
        super(channel);
    }

    private static KeyManagerFactory initKeyManagerFactory() throws Exception {
        InputStream keyStoreStream = ClassLoader.getSystemClassLoader().getResourceAsStream("liuyehcf_server_ks");

        // keyStore
        KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
        keyStore.load(keyStoreStream, KEY_STORE_PASSWORD.toCharArray());

        // keyManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, KEY_PASSWORD.toCharArray());

        return keyManagerFactory;
    }

    public static SslServerConverter create() {
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast(SSL_CONTEXT.newHandler(channel.alloc()));
        return new SslServerConverter(channel);
    }
}
