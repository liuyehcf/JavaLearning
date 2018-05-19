#!/bin/bash

if [ -d classes ]; then
    rm -rf classes;
fi

mkdir classes

TOOLS_PATH="${JAVA_HOME}/lib/tools.jar"

# 编译Builder注解以及注解处理器
javac -cp ${TOOLS_PATH} $(find ../java -name "*.java")  -d classes/

# 编译UserDTO.java，通过-process参数指定注解处理器
javac -cp classes:. -d classes -processor org.liuyehcf.annotation.source.processor.DataProcessor,org.liuyehcf.annotation.source.processor.NoArgsConstructorProcessor,org.liuyehcf.annotation.source.processor.AllArgsConstructorProcessor,org.liuyehcf.annotation.source.processor.BuilderProcessor UserDTO.java

# 反编译静态内部类
javap -cp classes:. -p UserDTO$UserDTOBuilder

# 运行UserDTO
java -cp classes:. UserDTO
