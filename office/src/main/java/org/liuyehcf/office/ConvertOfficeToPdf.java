package org.liuyehcf.office;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

import java.io.File;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;


/**
 * 需要安装openoffice,进入到C:\Program Files (x86)\OpenOffice 4\program中,打开cmd执行以下命令开启服务
 * "soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard"
 * Created by liuye on 2017/4/27 0027.
 */
public class ConvertOfficeToPdf {
    private static final String REGEX = ".*\\.(docx|doc|ppt|pptx)";

    /**
     * ConvertOfficeToPdf必须是单例,否则totalFiles是无效的
     */
    private static volatile int totalFiles;

    public static void main(String[] args) throws Exception {
        String sourceDirect = "F:\\Notes";//word文档根目录
        String destinationDirect = "F:\\PDF";//转入PDF的目录
        ConvertOfficeToPdf converter = new ConvertOfficeToPdf();
        converter.convert(sourceDirect, destinationDirect);
    }

    public void convert(String sourceDirect, String destinationDirect) {
        List<File> docList = new ArrayList<File>();
        File sourceFile = new File(sourceDirect);
        searchDFS(sourceFile, docList);

        totalFiles = docList.size();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (File docFile : docList) {
            String outputPath = destinationDirect + "\\" + getNameWithoutExtension(docFile) + ".pdf";
            File outputFile = new File(outputPath);
            if (outputFile.exists()) {
                boolean isDelete = outputFile.delete();//如果目标文件已存在,先删除
                if (!isDelete) throw new RuntimeException();
            }
            ConvertTask task = new ConvertTask(docFile, outputFile);
            executorService.execute(task);
        }
        executorService.shutdown();
    }

    private void searchDFS(File file, List<File> docList) {
        if (!file.exists()) throw new RuntimeException();
        if (file.isDirectory()) {
            File[] subFiles = file.listFiles();
            for (File subFile : subFiles) {
                searchDFS(subFile, docList);
            }
        } else if (file.isFile()) {
            if (isMatches(file.getName())) {
                docList.add(file);
            }
        } else {
            throw new RuntimeException();
        }
    }

    private boolean isMatches(String filename) {
        return Pattern.compile(REGEX).matcher(filename).matches();
    }

    private String getNameWithoutExtension(File file) {
        String[] splits = file.getName().split("\\.");
        if (splits.length != 2) throw new RuntimeException();
        return splits[0];
    }

    private static final class ConvertTask implements Runnable {
        private static final ReentrantLock lock = new ReentrantLock();
        private File inputFile;
        private File outputFile;

        public ConvertTask(File inputFile, File outputFile) {
            this.inputFile = inputFile;
            this.outputFile = outputFile;
        }

        public void run() {
            long start = System.currentTimeMillis();
            // connect to an OpenOffice.org instance running on port 8100
            OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
            try {
                connection.connect();

                // convert
                DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
                converter.convert(inputFile, outputFile);
            } catch (ConnectException cex) {
                cex.printStackTrace();
            } finally {
                // close the connection
                if (connection != null) {
                    connection.disconnect();
                    connection = null;
                }
            }
            long end = System.currentTimeMillis();
            try {
                lock.lock();
                System.out.println("生成" + outputFile.getName() + "耗费：" + (end - start) / 1000 + "秒, 还剩" + (--totalFiles) + "个word文档待转换");
            } finally {
                lock.unlock();
            }
        }
    }
}

