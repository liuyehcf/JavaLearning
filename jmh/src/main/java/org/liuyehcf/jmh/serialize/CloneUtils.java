package org.liuyehcf.jmh.serialize;

import com.alibaba.fastjson.JSON;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hechenfeng
 * @date 2019/5/1
 */
public abstract class CloneUtils {

    private static final KryoPool kryoPool = new KryoPool.Builder(Kryo::new).build();

    public static <T> Map<String, T> shallowClone(Map<String, T> origin) {
        if (origin == null) {
            return null;
        }

        return new HashMap<>(origin);
    }

    public static <T> T hessianClone(T origin) {
        return hessianDeserialize(hessianSerialize(origin));
    }

    public static byte[] hessianSerialize(Object object) {
        Hessian2Output hessian2Output = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            hessian2Output = new Hessian2Output(byteArrayOutputStream);

            hessian2Output.getSerializerFactory().setAllowNonSerializable(true);

            hessian2Output.writeObject(object);
            hessian2Output.flush();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (hessian2Output != null) {
                try {
                    hessian2Output.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T hessianDeserialize(byte[] bytes) {
        Hessian2Input hessian2Input = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            hessian2Input = new Hessian2Input(byteArrayInputStream);

            hessian2Input.getSerializerFactory().setAllowNonSerializable(true);

            return (T) hessian2Input.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (hessian2Input != null) {
                try {
                    hessian2Input.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }

    public static <T> T javaClone(T origin) {
        return javaDeserialize(javaSerialize(origin));
    }

    public static byte[] javaSerialize(Object object) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream java2Output = new ObjectOutputStream(byteArrayOutputStream)) {

            java2Output.writeObject(object);
            java2Output.flush();

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T javaDeserialize(byte[] bytes) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream java2Input = new ObjectInputStream(byteArrayInputStream)) {

            return (T) java2Input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T kryoClone(T object) {
        Kryo kryo = kryoPool.borrow();
        try {
            return kryo.copy(object);
        } finally {
            kryoPool.release(kryo);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T jsonClone(Object object) {
        return (T) JSON.parseObject(JSON.toJSONString(object));
    }
}
