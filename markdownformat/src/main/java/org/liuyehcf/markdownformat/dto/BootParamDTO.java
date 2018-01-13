package org.liuyehcf.markdownformat.dto;

import java.io.File;

/**
 * Created by HCF on 2018/1/13.
 */
public class BootParamDTO {
    private File rootDirectory;

    private File fileDirectory;

    private File imageDirectory;

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

    public File getImageDirectory() {
        return imageDirectory;
    }

    public void setImageDirectory(File imageDirectory) {
        this.imageDirectory = imageDirectory;
    }
}
