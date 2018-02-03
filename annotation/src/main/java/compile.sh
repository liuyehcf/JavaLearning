#!/bin/bash

if [ -d classes ]; then
    rm -rf classes;
fi

mkdir classes



javac -cp /Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/lib/tools.jar org/liuyehcf/annotation/source/Builder.java org/liuyehcf/annotation/source/BuilderProcessor.java -d classes/
javac -classpath classes -d classes -processor org.liuyehcf.annotation.source.BuilderProcessor org/liuyehcf/annotation/source/UserDTO.java
javap -classpath classes -p org.liuyehcf.annotation.source.UserDTO
java -classpath classes org.liuyehcf.annotation.source.UserDTO
