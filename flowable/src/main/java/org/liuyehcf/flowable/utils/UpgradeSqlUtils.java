package org.liuyehcf.flowable.utils;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.liuyehcf.flowable.utils.CreateSqlUtils.appendCreateTableSql;
import static org.liuyehcf.flowable.utils.CreateSqlUtils.getSqlFile;


/**
 * @author chenlu
 * @date 2018/8/30
 */
public class UpgradeSqlUtils {

    private static final String OLD_VERSION = "oldVersion";
    private static final String NEW_VERSION = "newVersion";

    private static final List<String> SQL_PATH_LIST = Arrays.asList(
            "org/flowable/common/db/upgrade/flowable.all.upgradestep.${oldVersion}.to.${newVersion}.common.sql",

            "org/flowable/idm/db/upgrade/flowable.all.upgradestep.${oldVersion}.to.${newVersion}.identity.sql",
            "org/flowable/identitylink/service/db/upgrade/flowable.all.upgradestep.${oldVersion}.to.${newVersion}.identitylink.sql",

            "org/flowable/variable/service/db/upgrade/flowable.all.upgradestep.${oldVersion}.to.${newVersion}.variable.sql",

            "org/flowable/job/service/db/upgrade/flowable.all.upgradestep.${oldVersion}.to.${newVersion}.job.sql",

            "org/flowable/task/service/db/upgrade/flowable.all.upgradestep.${oldVersion}.to.${newVersion}.task.sql",

            "org/flowable/db/upgrade/flowable.all.upgradestep.${oldVersion}.to.${newVersion}.engine.sql",


            "org/flowable/idm/db/upgrade/flowable.mysql.upgradestep.${oldVersion}.to.${newVersion}.identity.sql",
            "org/flowable/identitylink/service/db/upgrade/flowable.mysql.upgradestep.${oldVersion}.to.${newVersion}.identitylink.sql",
            "org/flowable/identitylink/service/db/upgrade/flowable.mysql.upgradestep.${oldVersion}.to.${newVersion}.identitylink.history.sql",

            "org/flowable/variable/service/db/upgrade/flowable.mysql.upgradestep.${oldVersion}.to.${newVersion}.variable.sql",
            "org/flowable/variable/service/db/upgrade/flowable.mysql.upgradestep.${oldVersion}.to.${newVersion}.variable.history.sql",

            "org/flowable/job/service/db/upgrade/flowable.mysql.upgradestep.${oldVersion}.to.${newVersion}.job.sql",

            "org/flowable/task/service/db/upgrade/flowable.mysql.upgradestep.${oldVersion}.to.${newVersion}.task.sql",
            "org/flowable/task/service/db/upgrade/flowable.mysql.upgradestep.${oldVersion}.to.${newVersion}.task.history.sql",

            "org/flowable/db/upgrade/flowable.mysql.upgradestep.${oldVersion}.to.${newVersion}.engine.sql",
            "org/flowable/db/upgrade/flowable.mysql.upgradestep.${oldVersion}.to.${newVersion}.history.sql"
    );

    private static final String FILE_NAME = "update_${oldVersion}_to_${newVersion}.sql";

    private static String createSqlFile(String targetPath, String oldVersion, String newVersion) {
        File targetSqlFile;
        try {
            targetSqlFile = getSqlFile(targetPath, resolvePlaceHolder(FILE_NAME, oldVersion, newVersion));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetSqlFile))) {

            for (String sqlPath : SQL_PATH_LIST) {

                appendCreateTableSql(outputStream, resolvePlaceHolder(sqlPath, oldVersion, newVersion));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return targetSqlFile.getAbsolutePath();
    }

    private static String resolvePlaceHolder(String sqlPath, String oldVersion, String newVersion) {
        String actualSqlPath = sqlPath.replace("${" + OLD_VERSION + "}", oldVersion);
        actualSqlPath = actualSqlPath.replace("${" + NEW_VERSION + "}", newVersion);
        return actualSqlPath;
    }


    public static void main(String[] args) throws Exception {
        String targetDir = "/Users/hechenfeng/Desktop/flowable";

        List<String> updateVersions = Arrays.asList("6120", "6200", "6210", "6300", "6301");
        List<String> filePaths = new ArrayList<>();

        int updateTimes = updateVersions.size() - 1;

        /*
         * 每次更新生成更新sql文件
         */
        for (int i = 0; i < updateTimes; i++) {
            String oldVersion = updateVersions.get(i);
            String newVersion = updateVersions.get(i + 1);

            System.out.println("\n\nfrom " + oldVersion + " to " + newVersion);

            String sqlFile = createSqlFile(targetDir, oldVersion, newVersion);
            filePaths.add(sqlFile);
        }

        /*
         * 生成 merge 文件名
         */
        StringBuilder sb = new StringBuilder();
        sb.append(targetDir)
                .append('/')
                .append("update");

        for (String updateVersion : updateVersions) {
            sb.append('_')
                    .append(updateVersion);
        }
        sb.append(".sql");

        /*
         * merge 每次更新的sql，生成一个sql
         */
        OutputStream out = new FileOutputStream(sb.toString());
        for (int i = 0; i < updateTimes; i++) {
            FileInputStream in = new FileInputStream(filePaths.get(i));
            IOUtils.copy(in, out);
            in.close();
        }
        out.close();
    }

}
