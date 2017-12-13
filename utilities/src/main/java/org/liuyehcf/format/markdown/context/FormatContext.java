package org.liuyehcf.format.markdown.context;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

/**
 * Created by t-chehe on 7/5/2017.
 */

public class FormatContext {
    public FormatContext(File[] files) {
        fileNames = new HashSet<String>();
        int i = 0;
        for (File file : files) {
            if (!fileNames.add(file.getName().split("\\.")[0])) {
                throw new RuntimeException();
            }
        }
    }

    private File file;

    private Set<String> fileNames;

    private LinkedList<LineElement> lineCache = new LinkedList<>();

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }

    public boolean containsFileName(String name) {
        return fileNames.contains(name);
    }

    public ListIterator<LineElement> getIterator() {
        return lineCache.listIterator();
    }

    public void add(LineElement lineElement) {
        lineCache.add(lineElement);
    }

    public int size() {
        return lineCache.size();
    }

    public void clear() {
        lineCache.clear();
    }

}
