package org.liuyehcf.mina;

import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author chenlu
 * @date 2018/12/20
 */
class BaseDemo {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private static final int PIPE_STREAM_BUFFER_SIZE = 1024 * 100;
    final PipedInputStream sshClientInputStream = new PipedInputStream(PIPE_STREAM_BUFFER_SIZE);
    final PipedOutputStream sshClientOutputStream = new PipedOutputStream();
    private final PipedInputStream bizInputStream = new PipedInputStream(PIPE_STREAM_BUFFER_SIZE);
    private final PipedOutputStream bizOutputStream = new PipedOutputStream();

    BaseDemo() throws IOException {
        sshClientInputStream.connect(bizOutputStream);
        sshClientOutputStream.connect(bizInputStream);
    }

    void beginRead() {
        EXECUTOR.execute(() -> {
            final byte[] buffer = new byte[10240];
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    int readNum = bizInputStream.read(buffer);

                    final byte[] actualBytes = new byte[readNum];
                    System.arraycopy(buffer, 0, actualBytes, 0, readNum);

                    println(new String(actualBytes, Charset.defaultCharset()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void beginWriteJnativehook() {
        EXECUTOR.execute(() -> {
            try {
                Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
                logger.setLevel(Level.OFF);
                GlobalScreen.registerNativeHook();
                GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                    @Override
                    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
                        byte keyCode = (byte) nativeKeyEvent.getKeyChar();

                        try {
                            bizOutputStream.write(keyCode);
                            bizOutputStream.flush();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
                        // default
                    }

                    @Override
                    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
                        // default
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    void beginWriteStd() {
        EXECUTOR.execute(() -> {
            try {
                final Scanner scanner = new Scanner(System.in);
                while (!Thread.currentThread().isInterrupted()) {
                    final String command = scanner.nextLine();

                    bizOutputStream.write((command + "\n").getBytes());
                    bizOutputStream.flush();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    private void println(Object obj) {
        synchronized (System.out) {
            System.out.println(obj);
            System.out.flush();
        }
    }
}


