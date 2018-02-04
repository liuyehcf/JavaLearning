package org.liuyehcf.reflect;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.liuyehcf.reflect.dto.AddressDTO;
import org.liuyehcf.reflect.dto.GenericDTO;
import org.liuyehcf.reflect.dto.UserDTO;

public class TestJavaBeanBuilder {
    @Test
    public void testGeneric1() {
        System.out.println(JSON.toJSONString(JavaBeanInitializerUtils.createJavaBean(
                new JavaBeanInitializerUtils.TypeReference<UserDTO<GenericDTO<UserDTO<AddressDTO>, AddressDTO>>>() {
                }
        )));
    }

    @Test
    public void testGeneric2() {
        System.out.println(JSON.toJSONString(JavaBeanInitializerUtils.createJavaBean(
                new JavaBeanInitializerUtils.TypeReference<GenericDTO<GenericDTO<UserDTO<AddressDTO>, AddressDTO>, GenericDTO<UserDTO<GenericDTO<UserDTO<AddressDTO>, AddressDTO>>, AddressDTO>>>() {
                }
        )));
    }
}
