package org.liuyehcf.markdown.format.hexo;

import org.liuyehcf.markdown.format.hexo.context.DefaultFileContext;
import org.liuyehcf.markdown.format.hexo.context.DefaultProcessorContext;
import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.ProcessorContext;
import org.liuyehcf.markdown.format.hexo.dto.BootParamDTO;

import java.io.File;

import static org.liuyehcf.markdown.format.hexo.constant.ErrorConstant.WRONG_PARAMS;
import static org.liuyehcf.markdown.format.hexo.constant.ParamConstant.DEFAULT_FILE_DIRECTORY_SIMPLE_NAME;
import static org.liuyehcf.markdown.format.hexo.constant.ParamConstant.DEFAULT_IMAGE_DIRECTORY_SIMPLE_NAME;
import static org.liuyehcf.markdown.format.hexo.log.DefaultLogger.DEFAULT_LOGGER;

/**
 * Created by HCF on 2018/1/13.
 */
public class MarkdownFormatter {

    public static void main(String[] args) {
        BootParamDTO bootParamDTO = prepareParamDTO(args);

        FileContext fileContext = new DefaultFileContext(bootParamDTO);
        ProcessorContext processorContext = new DefaultProcessorContext();

        while (fileContext.hasNextFile()) {
            // 初始化文件上下文
            fileContext.initFileContext();

            // 处理当前文件
            processorContext.process(fileContext);

            // 移动指针
            fileContext.moveForward();
        }
    }

    private static BootParamDTO prepareParamDTO(String[] args) {
        if (args.length != 1 && args.length != 3) {
            DEFAULT_LOGGER.error(WRONG_PARAMS + ",args= {}", args);
            throw new RuntimeException(WRONG_PARAMS);
        }

        File rootDirectory = new File(args[0]);
        if (!(rootDirectory.exists() && rootDirectory.isDirectory())) {
            DEFAULT_LOGGER.error(WRONG_PARAMS + ",args= {}", args);
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
            DEFAULT_LOGGER.error(WRONG_PARAMS + ",args= {}", args);
            throw new RuntimeException(WRONG_PARAMS);
        }

        BootParamDTO bootParamDTO = new BootParamDTO();
        bootParamDTO.setRootDirectory(rootDirectory);
        bootParamDTO.setFileDirectory(fileDirectory);
        bootParamDTO.setImageDirectory(imageDirectory);

        return bootParamDTO;
    }
}
