package org.liuyehcf.netty.ssl;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;

/**
 * @author hechenfeng
 * @date 2019/8/29
 */
public class SslClientConverter extends AbstractSslConverter {

    private static final SslContext SSL_CONTEXT;

    static {
        try {
            SSL_CONTEXT = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        } catch (SSLException e) {
            throw new Error();
        }
    }

    private SslClientConverter(EmbeddedChannel channel) {
        super(channel);
    }

    public static SslClientConverter create() {
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast(SSL_CONTEXT.newHandler(channel.alloc()));
        return new SslClientConverter(channel);
    }
}