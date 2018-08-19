package org.liuyehcf.crypto;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

@Slf4j
public class FileCryptoUtils {

    private static final String ENCRYPT_ALGORITHM = "DES";

    private static final String PASSWORD = "WO_AI_LV_HUA";

    private static final String ENCRYPT_FILE_SUFFIX = ".encrypt";

    private static final Set<String> RELATIVE_DIRS = new HashSet<>();
    private static final Map<String, String> FILE_RELATIVE_PATH_MAP = new HashMap<>();

    private static List<String> getFileList(File baseDir, File currentDir) {
        List<String> filePathList = new ArrayList<>();
        File[] files = currentDir.listFiles();

        if (files == null) {
            return filePathList;
        }

        String currentDirRelativePath = currentDir.getAbsolutePath().substring(baseDir.getAbsolutePath().length());
        RELATIVE_DIRS.add(currentDirRelativePath);

        for (File file : files) {
            if (file.isDirectory()) {
                filePathList.addAll(getFileList(baseDir, file));
            } else {
                filePathList.add(file.getAbsolutePath());
                FILE_RELATIVE_PATH_MAP.put(file.getAbsolutePath(), currentDirRelativePath);
            }
        }

        return filePathList;
    }

    private static void mkdirsOnTargetBaseDir(File targetBaseDir) {
        RELATIVE_DIRS.forEach(relativePath -> {
            String absoluteDirPath = targetBaseDir.getAbsolutePath() + File.separator + relativePath;
            File file = new File(absoluteDirPath);
            if (!file.exists()) {
                boolean res = file.mkdirs();
                if (!res) {
                    log.error("create dir failed. dirPath={}", absoluteDirPath);
                    throw new RuntimeException("create dir error");
                }
                log.info("create dir succeeded. dirPath={}", absoluteDirPath);
            }
        });
    }

    private static void encryptFile(File targetBaseDir, String sourceFilePath) {

        InputStream in = null;
        CipherOutputStream out = null;
        final File sourceFile = new File(sourceFilePath);
        String targetFilePath = null;

        try {
            final DESKeySpec desKey = new DESKeySpec(PASSWORD.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPT_ALGORITHM);
            final SecretKey secretKey = keyFactory.generateSecret(desKey);

            final Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            targetFilePath = targetBaseDir.getAbsolutePath() + File.separator + FILE_RELATIVE_PATH_MAP.get(sourceFilePath) + File.separator + sourceFile.getName() + ENCRYPT_FILE_SUFFIX;
            File targetFile = new File(targetFilePath);
            if (targetFile.exists()) {
                log.error("targetFile already exists. targetFilePath={}", targetFilePath);
                return;
            }

            in = new FileInputStream(sourceFilePath);
            out = new CipherOutputStream(new FileOutputStream(targetFilePath), cipher);

            log.info("encrypt started. sourceFilePath={}; targetFilePath={}", sourceFilePath, targetFilePath);

            IOUtils.copyLarge(in, out);

            log.info("encrypt ended. sourceFilePath={}; targetFilePath={}", sourceFilePath, targetFilePath);
        } catch (Throwable e) {
            log.error("encrypt error. sourceFilePath={}; targetFilePath={}", sourceFilePath, targetFilePath, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void decryptFile(File targetBaseDir, String sourceFilePath) {

        InputStream in = null;
        CipherOutputStream out = null;
        final File sourceFile = new File(sourceFilePath);
        String targetFilePath = null;

        try {
            final DESKeySpec desKey = new DESKeySpec(PASSWORD.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPT_ALGORITHM);
            final SecretKey secretKey = keyFactory.generateSecret(desKey);

            final Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            targetFilePath = targetBaseDir.getAbsolutePath() + File.separator + FILE_RELATIVE_PATH_MAP.get(sourceFilePath) + File.separator + sourceFile.getName() + ENCRYPT_FILE_SUFFIX;
            File targetFile = new File(targetFilePath);
            if (targetFile.exists()) {
                log.error("targetFile already exists. targetFilePath={}", targetFilePath);
                return;
            }

            in = new FileInputStream(sourceFilePath);
            out = new CipherOutputStream(new FileOutputStream(targetFilePath), cipher);

            log.info("decrypt started. sourceFilePath={}; targetFilePath={}", sourceFilePath, targetFilePath);

            IOUtils.copyLarge(in, out);

            log.info("decrypt ended. sourceFilePath={}; targetFilePath={}", sourceFilePath, targetFilePath);
        } catch (Throwable e) {
            log.error("decrypt error. sourceFilePath={}; targetFilePath={}", sourceFilePath, targetFilePath, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static final class Encrypto {
        public static void main(String[] args) {
            if (args == null || args.length != 2) {
                throw new RuntimeException("parameter error");
            }

            File sourceBaseDir = new File(args[0]);
            File targetBaseDir = new File(args[1]);
            if (!sourceBaseDir.exists() || !sourceBaseDir.isDirectory()) {
                log.error("sourceBaseDir does not exists. sourceBaseDirPath={}", sourceBaseDir.getAbsolutePath());
                throw new RuntimeException("sourceBaseDir does not exists. sourceBaseDirPath=" + sourceBaseDir.getAbsolutePath());
            }
            if (!targetBaseDir.exists()) {
                boolean res = targetBaseDir.mkdirs();
                if (!res) {
                    log.error("targetBaseDir does not exists create targetBaseDir error. targetBaseDir={}", targetBaseDir.getAbsolutePath());
                    throw new RuntimeException("targetBaseDir does not exists create targetBaseDir error. targetBaseDir=" + targetBaseDir.getAbsolutePath());
                }
                log.info("targetBaseDir does not exists and now create. targetBaseDir={}", targetBaseDir.getAbsolutePath());
            }

            List<String> fileList = getFileList(sourceBaseDir, sourceBaseDir);
            mkdirsOnTargetBaseDir(targetBaseDir);
            fileList.forEach(filePath -> encryptFile(targetBaseDir, filePath));
        }
    }

    private static final class Decrypto {
        public static void main(String[] args) {
            if (args == null || args.length != 2) {
                throw new RuntimeException("parameter error");
            }

            File sourceBaseDir = new File(args[0]);
            File targetBaseDir = new File(args[1]);
            if (!sourceBaseDir.exists() || !sourceBaseDir.isDirectory()) {
                log.error("sourceBaseDir does not exists. sourceBaseDirPath={}", sourceBaseDir.getAbsolutePath());
                throw new RuntimeException("sourceBaseDir does not exists. sourceBaseDirPath=" + sourceBaseDir.getAbsolutePath());
            }
            if (!targetBaseDir.exists()) {
                boolean res = targetBaseDir.mkdirs();
                if (!res) {
                    log.error("targetBaseDir does not exists create targetBaseDir error. targetBaseDir={}", targetBaseDir.getAbsolutePath());
                    throw new RuntimeException("targetBaseDir does not exists create targetBaseDir error. targetBaseDir=" + targetBaseDir.getAbsolutePath());
                }
                log.info("targetBaseDir does not exists and now create. targetBaseDir={}", targetBaseDir.getAbsolutePath());
            }

            List<String> fileList = getFileList(sourceBaseDir, sourceBaseDir);
            mkdirsOnTargetBaseDir(targetBaseDir);
            fileList.forEach(filePath -> decryptFile(targetBaseDir, filePath));
        }
    }
}
