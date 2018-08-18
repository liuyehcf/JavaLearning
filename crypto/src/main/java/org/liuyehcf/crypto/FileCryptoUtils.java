package org.liuyehcf.crypto;


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
                    throw new RuntimeException("create dir error");
                }
                System.out.println("create dir " + absoluteDirPath);
            }
        });
    }

    private static void encryptFile(File targetBaseDir, String sourceFilePath) {

        InputStream in = null;
        CipherOutputStream out = null;
        final File sourceFile = new File(sourceFilePath);

        try {
            final DESKeySpec desKey = new DESKeySpec(PASSWORD.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPT_ALGORITHM);
            final SecretKey secretKey = keyFactory.generateSecret(desKey);

            final Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            final String targetFilePath = targetBaseDir.getAbsolutePath() + File.separator + FILE_RELATIVE_PATH_MAP.get(sourceFilePath) + File.separator + sourceFile.getName() + ENCRYPT_FILE_SUFFIX;

            in = new FileInputStream(sourceFilePath);
            out = new CipherOutputStream(new FileOutputStream(targetFilePath), cipher);

            IOUtils.copyLarge(in, out);

            System.out.println("encrypt file " + sourceFilePath + " succeeded!");
        } catch (Throwable e) {
            e.printStackTrace();
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

    private static void decryptFile(final File targetBaseDir, final String sourceFilePath) {

        InputStream in = null;
        CipherOutputStream out = null;
        final File sourceFile = new File(sourceFilePath);

        try {
            final DESKeySpec desKey = new DESKeySpec(PASSWORD.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ENCRYPT_ALGORITHM);
            final SecretKey secretKey = keyFactory.generateSecret(desKey);

            final Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            final String sourceFileName = sourceFile.getName();
            final String targetFileName = sourceFileName.substring(0, sourceFileName.length() - ENCRYPT_FILE_SUFFIX.length());
            final String targetFilePath = targetBaseDir.getAbsolutePath() + File.separator + FILE_RELATIVE_PATH_MAP.get(sourceFilePath) + File.separator + targetFileName;

            in = new FileInputStream(sourceFilePath);
            out = new CipherOutputStream(new FileOutputStream(targetFilePath), cipher);

            IOUtils.copyLarge(in, out);

            System.out.println("decrypt file " + sourceFilePath + " succeeded!");
        } catch (Throwable e) {
            e.printStackTrace();
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
            if (!sourceBaseDir.exists() || !sourceBaseDir.isDirectory()
                    || !targetBaseDir.exists() || !targetBaseDir.isDirectory()) {
                throw new RuntimeException("base dir error");
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
            if (!sourceBaseDir.exists() || !sourceBaseDir.isDirectory()
                    || !targetBaseDir.exists() || !targetBaseDir.isDirectory()) {
                throw new RuntimeException("base dir error");
            }

            List<String> fileList = getFileList(sourceBaseDir, sourceBaseDir);
            mkdirsOnTargetBaseDir(targetBaseDir);
            fileList.forEach(filePath -> decryptFile(targetBaseDir, filePath));
        }
    }
}
