package org.liuyehcf.flowable.utils;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author hechenfeng
 * @date 2018/8/18
 */
public class CreateSqlUtils {

    private static final List<String> SQL_PATH_LIST = Arrays.asList(
            "org/flowable/common/db/create/flowable.mysql.create.common.sql",

            "org/flowable/idm/db/create/flowable.mysql.create.identity.sql",
            "org/flowable/identitylink/service/db/create/flowable.mysql.create.identitylink.sql",
            "org/flowable/identitylink/service/db/create/flowable.mysql.create.identitylink.history.sql",

            "org/flowable/variable/service/db/create/flowable.mysql.create.variable.sql",
            "org/flowable/variable/service/db/create/flowable.mysql.create.variable.history.sql",
            "org/flowable/job/service/db/create/flowable.mysql.create.job.sql",
            "org/flowable/task/service/db/create/flowable.mysql.create.task.sql",
            "org/flowable/task/service/db/create/flowable.mysql.create.task.history.sql",

            "org/flowable/db/create/flowable.mysql.create.engine.sql",

            "org/flowable/db/create/flowable.mysql.create.history.sql"
    );

    private static final String FILE_NAME = "create.sql";

    public static void createSqlFile(String targetPath) {
        File targetSqlFile;
        try {
            targetSqlFile = getSqlFile(targetPath, FILE_NAME);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetSqlFile))) {

            appendCreateDatabaseSql(outputStream);

            for (String sqlPath : SQL_PATH_LIST) {
                appendCreateTableSql(outputStream, sqlPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static File getSqlFile(String targetPath, String fileName) throws IOException {
        if (targetPath == null) {
            throw new NullPointerException();
        }

        File targetDir = new File(targetPath);

        if (!targetDir.exists()) {
            throw new FileNotFoundException(targetPath + " is not exists");
        }

        File sqlFile = new File(targetDir.getAbsolutePath() + File.separator + fileName);

        if (sqlFile.exists() && !sqlFile.delete()) {
            throw new IOException("failed to delete file " + sqlFile.getAbsolutePath());
        } else if (!sqlFile.createNewFile()) {
            throw new IOException("failed to create file " + sqlFile.getAbsolutePath());
        }

        return sqlFile;
    }

    private static void appendCreateDatabaseSql(OutputStream outputStream) throws IOException {
        outputStream.write(("/**************************************************************/\n" +
                "/*    [START CREATING DATABASE]\n" +
                "/**************************************************************/\n").getBytes());

        outputStream.write("DROP DATABASE IF EXISTS `flowable`;\n".getBytes());
        outputStream.write("CREATE DATABASE `flowable`;\n".getBytes());
        outputStream.write("USE `flowable`;\n".getBytes());

        outputStream.write(("/**************************************************************/\n" +
                "/*    [END CREATING DATABASE]\n" +
                "/**************************************************************/\n").getBytes());

        outputStream.write("\n\n\n".getBytes());
    }

    static void appendCreateTableSql(OutputStream outputStream, String fileClassPath) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String simpleFilePath = fileClassPath.substring(fileClassPath.lastIndexOf(File.separator) + 1).trim();

        InputStream inputStream = classLoader.getResourceAsStream(fileClassPath);

        if (inputStream == null) {
            System.err.println(fileClassPath);
            return;
        }

        System.out.println(fileClassPath);

        outputStream.write(("/**************************************************************/\n" +
                "/*    [START]\n" +
                "/*  " + simpleFilePath + "\n" +
                "/**************************************************************/\n").getBytes());

        IOUtils.copy(inputStream, outputStream);
        outputStream.write("\n".getBytes());

        outputStream.write(("/**************************************************************/\n" +
                "/*    [END]\n" +
                "/*  " + simpleFilePath + "\n" +
                "/**************************************************************/\n").getBytes());

        outputStream.write("\n\n\n".getBytes());
        inputStream.close();
    }

    public static void main(String[] args) {
        createSqlFile("/Users/hechenfeng/Desktop/flowable");
    }

}
