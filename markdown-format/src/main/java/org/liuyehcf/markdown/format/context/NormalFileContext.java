package org.liuyehcf.markdown.format.context;

import org.liuyehcf.markdown.format.model.NormalParam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static org.liuyehcf.markdown.format.constant.StringConstant.MARKDOWN_SUFFIX;
import static org.liuyehcf.markdown.format.log.DefaultLogger.LOGGER;

/**
 * @author chenlu
 * @date 2018/8/2
 */
public class NormalFileContext implements FileContext {
    private final File rootDirectory;

    private int index;

    private List<File> files;

    private LinkedList<LineElement> lineElements;

    public NormalFileContext(NormalParam normalParam) {
        this.rootDirectory = normalParam.getRootDirectory();
        index = 0;
        initFiles();
    }

    private void initFiles() {
        files = Collections.unmodifiableList(getAllFiles());
    }

    private List<File> getAllFiles() {
        return getFilesFrom(rootDirectory);
    }

    private List<File> getFilesFrom(File directory) {
        List<File> files = new ArrayList<>();

        File[] fileArray = directory.listFiles(
                (pathname) -> pathname.getName().endsWith(MARKDOWN_SUFFIX));

        if (fileArray != null) {
            files.addAll(Arrays.asList(fileArray));
        }

        File[] directories = directory.listFiles(File::isDirectory);

        if (directories != null) {
            for (File file : directories) {
                files.addAll(getFilesFrom(file));
            }
        }

        return files;
    }

    @Override
    public void initFileContext() {
        try {
            readCurrentFile();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getFile() {
        return files.get(index);
    }

    @Override
    public LineIterator getLineIterator() {
        return new DefaultLineIterator(lineElements);
    }

    @Override
    public File getRootDirectory() {
        return rootDirectory;
    }

    @Override
    public boolean hasNextFile() {
        return index < files.size();
    }

    @Override
    public void moveForward() {
        index++;
        lineElements = null;
    }

    private void readCurrentFile() throws IOException {
        lineElements = new LinkedList<>();
        FileContext.readFile(new BufferedReader(new FileReader(getFile())), getFile(), lineElements);
    }

    @Override
    public String getProperty(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFileDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getImageDirectory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsFile(String name) {
        throw new UnsupportedOperationException();
    }
}
