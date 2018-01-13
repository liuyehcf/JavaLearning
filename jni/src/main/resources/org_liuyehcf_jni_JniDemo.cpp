#include <iostream>
#include "org_liuyehcf_jni_JniDemo.h"

JNIEXPORT void JNICALL Java_org_liuyehcf_jni_JniDemo_sayHello
  (JNIEnv *, jclass){
    std::cout<<"hello, This is JNI method!"<<std::endl;
}