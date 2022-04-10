package org.liuyehcf.markdown.format;

import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.HexoProcessorContext;
import org.liuyehcf.markdown.format.context.NormalProcessorContext;
import org.liuyehcf.markdown.format.context.ProcessorContext;
import org.liuyehcf.markdown.format.model.HexoParam;
import org.liuyehcf.markdown.format.model.NormalParam;

import java.io.File;
import java.util.Arrays;

import static org.liuyehcf.markdown.format.constant.ErrorConstant.ILLEGAL_PARAMS;
import static org.liuyehcf.markdown.format.constant.ParamConstant.DEFAULT_FILE_DIRECTORY_SIMPLE_NAME;
import static org.liuyehcf.markdown.format.log.DefaultLogger.LOGGER;

/**
 * Created by HCF on 2022/4/10.
 */
public class Formatter {
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new RuntimeException(ILLEGAL_PARAMS);
        }
        String mode = args[0];
        NormalParam param;
        ProcessorContext processorContext;
        if ("hexo".equalsIgnoreCase(mode)) {
            param = parseHexoParams(args);
            processorContext = new HexoProcessorContext();
        } else if ("normal".equalsIgnoreCase(mode)) {
            param = parseNormalParam(args);
            processorContext = new NormalProcessorContext();
        } else {
            throw new RuntimeException("illegal mode, only support 'hexo' or 'normal'");
        }

        FileContext fileContext = FileContext.create(param);
        while (fileContext.hasNextFile()) {
            fileContext.initFileContext();

            processorContext.process(fileContext);

            fileContext.moveForward();
        }
    }

    private static HexoParam parseHexoParams(String[] args) {
        if (args.length != 2 && args.length != 3) {
            LOGGER.error(ILLEGAL_PARAMS + ",args= {}", Arrays.toString(args));
            throw new RuntimeException(ILLEGAL_PARAMS);
        }

        File rootDirectory = new File(args[1]);
        if (!(rootDirectory.exists() && rootDirectory.isDirectory())) {
            LOGGER.error(ILLEGAL_PARAMS + ",args= {}", Arrays.toString(args));
            throw new RuntimeException(ILLEGAL_PARAMS);
        }

        String fileDirectoryName = DEFAULT_FILE_DIRECTORY_SIMPLE_NAME;

        if (args.length == 3) {
            fileDirectoryName = args[2];
        }

        File fileDirectory = new File(rootDirectory.getAbsolutePath() + "/" + fileDirectoryName);

        if (!(fileDirectory.exists() && fileDirectory.isDirectory())) {
            LOGGER.error(ILLEGAL_PARAMS + ",args= {}", Arrays.toString(args));
            throw new RuntimeException(ILLEGAL_PARAMS);
        }

        HexoParam hexoParam = new HexoParam();
        hexoParam.setRootDirectory(rootDirectory);
        hexoParam.setFileDirectory(fileDirectory);

        return hexoParam;
    }

    private static NormalParam parseNormalParam(String[] args) {
        if (args.length != 2) {
            LOGGER.error(ILLEGAL_PARAMS + ",args= {}", Arrays.toString(args));
            throw new RuntimeException(ILLEGAL_PARAMS);
        }

        File rootDirectory = new File(args[1]);
        if (!(rootDirectory.exists() && rootDirectory.isDirectory())) {
            LOGGER.error(ILLEGAL_PARAMS + ",args= {}", Arrays.toString(args));
            throw new RuntimeException(ILLEGAL_PARAMS);
        }

        NormalParam normalParam = new NormalParam();
        normalParam.setRootDirectory(rootDirectory);
        return normalParam;
    }
}
