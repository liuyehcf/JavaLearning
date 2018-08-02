package org.liuyehcf.markdown.format;

import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.NormalFileContext;
import org.liuyehcf.markdown.format.context.NormalProcessorContext;
import org.liuyehcf.markdown.format.context.ProcessorContext;
import org.liuyehcf.markdown.format.model.NormalParam;

import java.io.File;
import java.util.Arrays;

import static org.liuyehcf.markdown.format.constant.ErrorConstant.WRONG_PARAMS;
import static org.liuyehcf.markdown.format.log.DefaultLogger.LOGGER;

/**
 * @author chenlu
 * @date 2018/8/2
 */
public class NormalFormatter {
    public static void main(String[] args) {
        NormalParam normalParam = prepareParam(args);

        FileContext fileContext = new NormalFileContext(normalParam);

        ProcessorContext processorContext = new NormalProcessorContext();

        while (fileContext.hasNextFile()) {
            // 初始化文件上下文
            fileContext.initFileContext();

            // 处理当前文件
            processorContext.process(fileContext);

            // 移动指针
            fileContext.moveForward();
        }
    }

    private static NormalParam prepareParam(String[] args) {
        if (args.length != 1) {
            LOGGER.error(WRONG_PARAMS + ",args= {}", Arrays.toString(args));
            throw new RuntimeException(WRONG_PARAMS);
        }

        File rootDirectory = new File(args[0]);
        if (!(rootDirectory.exists() && rootDirectory.isDirectory())) {
            LOGGER.error(WRONG_PARAMS + ",args= {}", Arrays.toString(args));
            throw new RuntimeException(WRONG_PARAMS);
        }

        NormalParam normalParam = new NormalParam();
        normalParam.setRootDirectory(rootDirectory);
        return normalParam;
    }
}
