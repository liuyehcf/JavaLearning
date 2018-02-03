if [ -d classes ]; then
    rm -rf classes;
fi
mkdir classes
javac -cp /Library/Java/JavaVirtualMachines/jdk1.8.0_121.jdk/Contents/Home/lib/tools.jar org/liuyehcf/lombok/annotation/Builder.java org/liuyehcf/lombok/processor/BuilderProcessor.java -d classes/
javac -classpath classes -d classes -processor org.liuyehcf.lombok.processor.BuilderProcessor org/liuyehcf/lombok/UserDTO.java
javap -classpath classes -p org.liuyehcf.lombok.UserDTO
java -classpath classes org.liuyehcf.lombok.UserDTO
