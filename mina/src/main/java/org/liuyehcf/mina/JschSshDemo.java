package org.liuyehcf.mina;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.util.concurrent.TimeUnit;


/**
 * @author hechenfeng
 * @date 2018/12/20
 */
public class JschSshDemo extends BaseDemo {

    private JschSshDemo() throws IOException {

    }

    public static void main(final String[] args) throws Exception {
        new JschSshDemo().boot();
    }

    private void boot() throws Exception {
        JSch jsch = new JSch();

        Session session = jsch.getSession("HCF", "localhost", 22);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.setPassword("???");
        session.connect();

        ChannelShell channel = (ChannelShell) session.openChannel("shell");
        channel.setInputStream(sshClientInputStream);
        channel.setOutputStream(sshClientOutputStream);
        channel.connect();

        beginRead();
        beginWriteJnativehook();
//        beginWriteStd();

        TimeUnit.SECONDS.sleep(1000000);
    }
}


