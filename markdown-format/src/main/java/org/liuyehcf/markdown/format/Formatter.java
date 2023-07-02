package org.liuyehcf.markdown.format;

import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.HexoProcessorContext;
import org.liuyehcf.markdown.format.context.NormalProcessorContext;
import org.liuyehcf.markdown.format.context.ProcessorContext;
import org.liuyehcf.markdown.format.model.Param;

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
        Param param = parseParams(args);
        ProcessorContext processorContext;
        boolean isHexo;
        if ("hexo".equalsIgnoreCase(mode)) {
            processorContext = new HexoProcessorContext();
            isHexo = true;
        } else if ("normal".equalsIgnoreCase(mode)) {
            processorContext = new NormalProcessorContext();
            isHexo = false;
        } else {
            throw new RuntimeException("illegal mode, only support 'hexo' or 'normal'");
        }

        FileContext fileContext = new FileContext(param, isHexo);
        while (fileContext.hasNextFile()) {
            fileContext.initFileContext();

            processorContext.process(fileContext);

            fileContext.moveForward();
        }
    }

    private static Param parseParams(String[] args) {
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

        Param param = new Param();
        param.setRootDirectory(rootDirectory);
        param.setFileDirectory(fileDirectory);

        return param;
    }
}