package org.liuyehcf.markdown.format.hexo.context;

import org.liuyehcf.markdown.format.hexo.dto.BootParamDTO;
import org.liuyehcf.markdown.format.hexo.log.CommonLogger;
import org.liuyehcf.markdown.format.hexo.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HCF on 2018/1/13.
 */
public class DefaultFileContext implements FileContext {

    private final File rootDirectory;

    private final File fileDirectory;

    private final File imageDirectory;

    private int index;

    private List<File> files;

    private Map<String, String> properties;

    private LinkedList<LineElement> lineElements;

    private static final String PROPERTY_REGEX = "^\\s*(.*?)\\s*:\\s*(.*?)\\s*$";
    private static final String SUB_PROPERTY_REGEX = "^\\s*-\\s+(.*)$";
    private static final Pattern PROPERTY_PATTERN = Pattern.compile(PROPERTY_REGEX);
    private static final Pattern SUB_PROPERTY_PATTERN = Pattern.compile(SUB_PROPERTY_REGEX);

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
    public void initFileContext() {
        try {
            readCurrentFile();
        } catch (IOException e) {
            CommonLogger.DEFAULT_LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
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
        properties = new HashMap<>();

        // 首先读取文件属性
        line = reader.readLine();
        lineElements.add(new DefaultLineElement(line, false));
        if (!line.equals("---")) {
            CommonLogger.DEFAULT_LOGGER.error("hexo file format error, \" --- \" not exists!");
            throw new RuntimeException();
        }

        StringBuilder sb = null;
        String preKey = null;
        while ((line = reader.readLine()) != null
                && !line.equals("---")) {
            if (!StringUtils.isBlankLine(line)) {
                Matcher propertyMatcher = PROPERTY_PATTERN.matcher(line);

                // 当前是正常属性
                if (propertyMatcher.matches()) {

                    // 处理一下之前的子属性
                    if (sb != null) {
                        if (preKey == null || sb.length() == 0) {
                            CommonLogger.DEFAULT_LOGGER.error("hexo file format error, file property format error");
                            throw new RuntimeException();
                        }
                        properties.put(preKey, sb.substring(0, sb.length() - 1));
                        sb = null;
                        preKey = null;
                    }

                    String propertyKey = propertyMatcher.group(1);
                    String propertyValue = propertyMatcher.group(2);

                    // 子属性
                    if ("".equals(propertyValue)) {
                        preKey = propertyKey;
                        sb = new StringBuilder();
                    }
                    // 正常属性
                    else {
                        properties.put(propertyKey, propertyValue);
                    }

                }
                // 子属性
                else {
                    Matcher subPropertyMatcher = SUB_PROPERTY_PATTERN.matcher(line);
                    if (!subPropertyMatcher.matches()) {
                        CommonLogger.DEFAULT_LOGGER.error("hexo file format error, file property format error");
                        throw new RuntimeException();
                    } else {
                        if (sb == null) {
                            CommonLogger.DEFAULT_LOGGER.error("hexo file format error, file property format error");
                            throw new RuntimeException();
                        }
                        sb.append(subPropertyMatcher.group(1) + ",");
                    }
                }
            }
            lineElements.add(new DefaultLineElement(line, false));
        }
        // 处理一下之前的子属性
        if (sb != null) {
            if (preKey == null || sb.length() == 0) {
                CommonLogger.DEFAULT_LOGGER.error("hexo file format error, file property format error");
                throw new RuntimeException();
            }
            properties.put(preKey, sb.substring(0, sb.length() - 1));
        }
        if (!line.equals("---")) {
            CommonLogger.DEFAULT_LOGGER.error("hexo file format error, \" --- \" not exists!");
            throw new RuntimeException();
        }
        lineElements.add(new DefaultLineElement(line, false));


        while ((line = reader.readLine()) != null) {
            if (line.contains("```")) {
                //cache "```" itself
                lineElements.add(new DefaultLineElement(line, true));

                while ((line = reader.readLine()) != null && !line.contains("```")) {
                    lineElements.add(new DefaultLineElement(line, true));
                }

                if (line == null) {
                    CommonLogger.DEFAULT_LOGGER.error("``` No pairs appear");
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
