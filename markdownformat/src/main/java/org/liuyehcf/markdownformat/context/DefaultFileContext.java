package org.liuyehcf.markdownformat.context;

import org.liuyehcf.markdownformat.dto.BootParamDTO;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HCF on 2018/1/13.
 */
public class DefaultFileContext implements FileContext {

    private final File rootDirectory;

    private final File fileDirectory;

    private final File imageDirectory;

    private int index;

    private List<File> files;

    private LineContext lineContext;

    public DefaultFileContext(BootParamDTO paramDTO) {
        rootDirectory = paramDTO.getRootDirectory();
        fileDirectory = paramDTO.getFileDirectory();
        imageDirectory = paramDTO.getImageDirectory();
        index = 0;
        initFiles();
    }

    private void initFiles() {
        File[] fileArray = fileDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".md");
            }
        });

        files = new ArrayList<>();

        for (int i = 0; i < fileArray.length; i++) {
            files.add(fileArray[i]);
        }

        files = Collections.unmodifiableList(files);
    }

    @Override
    public LineContext getCurrentLineContext() {
        return lineContext;
    }

    @Override
    public File getRootDirectory() {
        return rootDirectory;
    }

    @Override
    public File getFileDirectory() {
        return fileDirectory;
    }

    @Override
    public File getImageDirectory() {
        return imageDirectory;
    }

    @Override
    public boolean isFinished() {
        return index < files.size();
    }

    @Override
    public void moveForward() {
        index++;
    }
}
