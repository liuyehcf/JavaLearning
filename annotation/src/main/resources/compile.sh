#!/bin/bash

if [ -d classes ]; then
    rm -rf classes;
fi

mkdir classes

# tools.jar的路径
TOOLS_PATH="${JAVA_HOME}/lib/tools.jar"

# 编译Builder注解以及注解处理器
javac -cp ${TOOLS_PATH} $(find ../java -name "*.java")  -d classes/

# 统计文件 `META-INF/services/javax.annotation.processing.Processor` 的行数
LINE_NUM=$(cat META-INF/services/javax.annotation.processing.Processor | wc -l)
LINE_NUM=$((LINE_NUM+1))

# 将文件 `META-INF/services/javax.annotation.processing.Processor` 中的内容合并成串，以','分隔
PROCESSORS=$(cat META-INF/services/javax.annotation.processing.Processor | awk '{ { printf $0 } if(NR < '"$LINE_NUM"') { printf "," } }')

# 编译UserDTO.java，通过-process参数指定注解处理器
javac -cp classes:. -d classes -processor $PROCESSORS UserDTO.java

# 反编译静态内部类
javap -cp classes:. -p UserDTO$UserDTOBuilder

# 运行UserDTO
java -cp classes:. UserDTO

# 删除目录
rm -rf classes
