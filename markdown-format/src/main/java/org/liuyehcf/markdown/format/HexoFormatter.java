package org.liuyehcf.markdown.format;

import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.HexoFileContext;
import org.liuyehcf.markdown.format.context.HexoProcessorContext;
import org.liuyehcf.markdown.format.context.ProcessorContext;
import org.liuyehcf.markdown.format.model.HexoParam;

import java.io.File;
import java.util.Arrays;

import static org.liuyehcf.markdown.format.constant.ErrorConstant.WRONG_PARAMS;
import static org.liuyehcf.markdown.format.constant.ParamConstant.DEFAULT_FILE_DIRECTORY_SIMPLE_NAME;
import static org.liuyehcf.markdown.format.constant.ParamConstant.DEFAULT_IMAGE_DIRECTORY_SIMPLE_NAME;
import static org.liuyehcf.markdown.format.log.DefaultLogger.LOGGER;

/**
 * Created by HCF on 2018/1/13.
 */
public class HexoFormatter {

    public static void main(String[] args) {
        HexoParam hexoParam = prepareParam(args);

        FileContext fileContext = new HexoFileContext(hexoParam);
        ProcessorContext processorContext = new HexoProcessorContext();

        while (fileContext.hasNextFile()) {
            // 初始化文件上下文
            fileContext.initFileContext();

            // 处理当前文件
            processorContext.process(fileContext);

            // 移动指针
            fileContext.moveForward();
        }
    }

    private static HexoParam prepareParam(String[] args) {
        if (args.length != 1 && args.length != 3) {
            LOGGER.error(WRONG_PARAMS + ",args= {}", Arrays.toString(args));
            throw new RuntimeException(WRONG_PARAMS);
        }

        File rootDirectory = new File(args[0]);
        if (!(rootDirectory.exists() && rootDirectory.isDirectory())) {
            LOGGER.error(WRONG_PARAMS + ",args= {}", Arrays.toString(args));
            throw new RuntimeException(WRONG_PARAMS);
        }

        String fileDirectoryName = DEFAULT_FILE_DIRECTORY_SIMPLE_NAME;
        String imageDirectoryName = DEFAULT_IMAGE_DIRECTORY_SIMPLE_NAME;

        if (args.length == 3) {
            fileDirectoryName = args[1];
            imageDirectoryName = args[2];
        }

        File fileDirectory = new File(rootDirectory.getAbsolutePath() + "/" + fileDirectoryName);
        File imageDirectory = new File(rootDirectory.getAbsolutePath() + "/" + imageDirectoryName);


        if (!(fileDirectory.exists() && fileDirectory.isDirectory())
                || !(imageDirectory.exists() && imageDirectory.isDirectory())) {
            LOGGER.error(WRONG_PARAMS + ",args= {}", Arrays.toString(args));
            throw new RuntimeException(WRONG_PARAMS);
        }

        HexoParam hexoParam = new HexoParam();
        hexoParam.setRootDirectory(rootDirectory);
        hexoParam.setFileDirectory(fileDirectory);
        hexoParam.setImageDirectory(imageDirectory);

        return hexoParam;
    }
}
