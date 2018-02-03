#!/bin/bash

if [ -d classes ]; then
    rm -rf classes;
fi

mkdir classes

TOOLS_PATH="/Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/lib/tools.jar"

# 编译Builder注解以及注解处理器
javac -cp ${TOOLS_PATH} org/liuyehcf/annotation/source/Builder.java org/liuyehcf/annotation/source/BuilderProcessor.java -d classes/

# 编译UserDTO.java，通过-process参数指定注解处理器
javac -classpath classes -d classes -processor org.liuyehcf.annotation.source.BuilderProcessor org/liuyehcf/annotation/source/UserDTO.java

# 反编译静态内部类
javap -classpath classes -p org.liuyehcf.annotation.source.UserDTO$UserDTOBuilderProcessor

# 运行UserDTO
java -classpath classes org.liuyehcf.annotation.source.UserDTO
