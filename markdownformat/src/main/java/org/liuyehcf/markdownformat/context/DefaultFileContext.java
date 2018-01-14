package org.liuyehcf.markdownformat.context;

import org.liuyehcf.markdownformat.dto.BootParamDTO;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.liuyehcf.markdownformat.log.CommonLogger.logger;

/**
 * Created by HCF on 2018/1/13.
 */
public class DefaultFileContext implements FileContext {

    private final File rootDirectory;

    private final File fileDirectory;

    private final File imageDirectory;

    private int index;

    private List<File> files;

    private LinkedList<LineElement> lineElements;

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
    public File getCurrentFile() {
        return files.get(index);
    }

    @Override
    public LineIterator getLineIteratorOfCurrentFile() {
        if (lineElements == null) {
            try {
                readCurrentFile();
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return new DefaultLineIterator(lineElements);
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
    public boolean hasNextFile() {
        return index < files.size();
    }

    @Override
    public void moveForward() {
        index++;
        lineElements = null;
    }

    private void readCurrentFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(files.get(index)));

        String line;
        lineElements = new LinkedList<>();

        while ((line = reader.readLine()) != null) {
            if (line.contains("```")) {
                //cache "```" itself
                lineElements.add(new DefaultLineElement(line, true));

                while ((line = reader.readLine()) != null && !line.contains("```")) {
                    lineElements.add(new DefaultLineElement(line, true));
                }

                if (line == null) {
                    logger.error("``` No pairs appear");
                    throw new RuntimeException();
                }

                //cache "```" itself
                lineElements.add(new DefaultLineElement(line, true));

            } else {

                lineElements.add(new DefaultLineElement(line, false));
            }
        }
    }

    @Override
    public boolean containsFile(String name) {
        if (!name.endsWith(".md")) {
            name = name + ".md";
        }

        for (File file : files) {
            if (file.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
