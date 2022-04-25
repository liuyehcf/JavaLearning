package org.liuyehcf.markdown.format.model;

import java.io.File;

/**
 * @author hechenfeng
 * @date 2018/8/2
 */
public class Param {
    private File rootDirectory;
    private File fileDirectory;

    public File getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(File rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public File getFileDirectory() {
        return fileDirectory;
    }

    public void setFileDirectory(File fileDirectory) {
        this.fileDirectory = fileDirectory;
    }
}
