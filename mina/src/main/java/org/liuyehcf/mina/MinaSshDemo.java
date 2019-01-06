package org.liuyehcf.mina;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelShell;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.future.ConnectFuture;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.util.io.NoCloseInputStream;
import org.apache.sshd.common.util.io.NoCloseOutputStream;

import java.io.IOException;
import java.util.Collections;

/**
 * @author hechenfeng
 * @date 2018/12/20
 */
public class MinaSshDemo extends BaseDemo {

    private MinaSshDemo() throws IOException {

    }

    public static void main(String[] args) throws Exception {
        new MinaSshDemo().boot();
    }

    private void boot() throws Exception {
        final SshClient client = SshClient.setUpDefaultClient();
        client.start();
        final ConnectFuture connect = client.connect("HCF", "localhost", 22);
        connect.await(5000L);
        final ClientSession session = connect.getSession();
        session.addPasswordIdentity("???");
        session.auth().verify(5000L);

        final ChannelShell channel = session.createShellChannel();
        channel.setIn(new NoCloseInputStream(sshClientInputStream));
        channel.setOut(new NoCloseOutputStream(sshClientOutputStream));
        channel.setErr(new NoCloseOutputStream(sshClientOutputStream));
        channel.open();

        beginRead();
//        beginWriteJnativehook();
        beginWriteStd();

        channel.waitFor(Collections.singleton(ClientChannelEvent.CLOSED), 0);
    }
}

