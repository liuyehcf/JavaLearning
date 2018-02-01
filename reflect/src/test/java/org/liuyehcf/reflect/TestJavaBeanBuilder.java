package org.liuyehcf.reflect;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.liuyehcf.reflect.dto.AddressDTO;
import org.liuyehcf.reflect.dto.UserDTO;

public class TestJavaBeanBuilder {
    @Test
    public void testGeneric() {
        System.out.println(JSON.toJSONString(JavaBeanBuilderUtils.createJavaBean(
                new TypeReference<UserDTO<AddressDTO>>() {
                }.getType()
        )));
    }
}
