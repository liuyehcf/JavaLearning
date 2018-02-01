package org.liuyehcf.reflect;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaBeanBuilderUtils {
    private static final Byte BYTE_DEFAULT_VALUE = 0;
    private static final Character CHAR_DEFAULT_VALUE = 0;
    private static final Short SHORT_DEFAULT_VALUE = 0;
    private static final Integer INTEGER_DEFAULT_VALUE = 0;
    private static final Long LONG_DEFAULT_VALUE = 0L;
    private static final Float FLOAT_DEFAULT_VALUE = 0.0f;
    private static final Double DOUBLE_DEFAULT_VALUE = 0.0;
    private static final Boolean BOOLEAN_DEFAULT_VALUE = false;

    /**
     * 从泛型参数名映射到实际的类型
     */
    private Map<String, Type> typeParams;

    /**
     * 要初始化的类型
     */
    private final Type type;

    private JavaBeanBuilderUtils(Type type) {
        this.type = type;
        typeParams = new HashMap<>();
        init();
    }

    private void init() {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            Class clazz = (Class) parameterizedType.getRawType();

            TypeVariable[] typeVariables = clazz.getTypeParameters();
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            for (int i = 0; i < typeVariables.length; i++) {
                typeParams.put(
                        typeVariables[i].getName(),
                        actualTypeArguments[i]
                );
            }
        }
    }

    /**
     * 初始化任意Bean
     *
     * @param type
     * @return
     */
    public static Object createJavaBean(Type type) {
        if (type == null) {
            throw new NullPointerException();
        }
        return new JavaBeanBuilderUtils(type)
                .doCreateJavaBean();
    }

    private Object doCreateJavaBean() {
        if (type instanceof Class) {
            return doCreateJavaBean((Class) type);
        } else if (type instanceof ParameterizedType) {
            return doCreateJavaBean((ParameterizedType) type);
        } else {
            throw new UnsupportedOperationException("暂不支持此类型的默认初始化，type: " + type);
        }
    }

    private Object doCreateJavaBean(Class clazz) {

        Object data;
        try {
            data = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("不能实例化接口/抽象类/没有无参构造方法的类");
        }

        for (Method setMethod : getSetMethods(clazz)) {
            Type paramType = setMethod.getGenericParameterTypes()[0];
            setDefaultValue(data, setMethod, paramType);
        }

        return data;
    }

    private Object doCreateJavaBean(ParameterizedType type) {

        Class clazz = (Class) type.getRawType();
        Object data;
        try {
            data = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("不能实例化接口/抽象类/没有无参构造方法的类");
        }

        for (Method setMethod : getSetMethods(clazz)) {
            Type paramType = setMethod.getGenericParameterTypes()[0];

            if (paramType instanceof TypeVariable) {
                Type actualType = typeParams.get(((TypeVariable) paramType).getName());
                setDefaultValue(data, setMethod, actualType);
            } else {
                setDefaultValue(data, setMethod, paramType);
            }
        }

        return data;
    }

    private List<Method> getSetMethods(Class clazz) {
        List<Method> setMethods = new ArrayList<>();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (method.getName().startsWith("set")
                    && method.getParameterCount() == 1
                    && method.getReturnType() == void.class) {
                setMethods.add(method);
            }
        }
        return setMethods;
    }

    private void setDefaultValue(Object data, Method method, Type paramType) {
        try {
            if (paramType instanceof Class) {
                setDefaultValueOfNormal(data, method, (Class) paramType);
            } else if (paramType instanceof ParameterizedType) {
                setDefaultValueOfGeneric(data, method, (ParameterizedType) paramType);
            } else if (paramType instanceof TypeVariable) {
                Type actualType = typeParams.get(((TypeVariable) paramType).getName());
                setDefaultValue(data, method, actualType);
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException();
        }
    }

    private String getFieldName(Method method) {
        return method.getName().substring(3);
    }


    private void setDefaultValueOfNormal(Object data, Method method, Class paramClass) throws IllegalAccessException, InvocationTargetException {
        if (paramClass.equals(Byte.class)
                || paramClass.equals(byte.class)) {
            method.invoke(data, BYTE_DEFAULT_VALUE);
        } else if (paramClass.equals(Character.class)
                || paramClass.equals(char.class)) {
            method.invoke(data, CHAR_DEFAULT_VALUE);
        } else if (paramClass.equals(Short.class)
                || paramClass.equals(short.class)) {
            method.invoke(data, SHORT_DEFAULT_VALUE);
        } else if (paramClass.equals(Integer.class)
                || paramClass.equals(int.class)) {
            method.invoke(data, INTEGER_DEFAULT_VALUE);
        } else if (paramClass.equals(Long.class)
                || paramClass.equals(long.class)) {
            method.invoke(data, LONG_DEFAULT_VALUE);
        } else if (paramClass.equals(Float.class)
                || paramClass.equals(float.class)) {
            method.invoke(data, FLOAT_DEFAULT_VALUE);
        } else if (paramClass.equals(Double.class)
                || paramClass.equals(double.class)) {
            method.invoke(data, DOUBLE_DEFAULT_VALUE);
        } else if (paramClass.equals(Boolean.class)
                || paramClass.equals(boolean.class)) {
            method.invoke(data, BOOLEAN_DEFAULT_VALUE);
        } else if (paramClass.equals(String.class)) {
            method.invoke(data, "default" + getFieldName(method));
        } else {
            method.invoke(data, createJavaBean((Class) paramClass));
        }
    }

    private void setDefaultValueOfGeneric(Object data, Method method, ParameterizedType paramType) throws IllegalAccessException, InvocationTargetException {
        Class rawClass = (Class) paramType.getRawType();

        if (rawClass.equals(List.class)) {
            List list = new ArrayList();

            Type genericParam = paramType.getActualTypeArguments()[0];

            Object obj;

            if (genericParam instanceof TypeVariable) {
                obj = createJavaBean(typeParams.get(((TypeVariable) genericParam).getName()));
            } else {
                obj = createJavaBean(genericParam);
            }

            list.add(obj);

            method.invoke(data, list);
        } else if (rawClass.equals(Map.class)) {
            Map map = new HashMap();

            Type genericParam1 = paramType.getActualTypeArguments()[0];
            Type genericParam2 = paramType.getActualTypeArguments()[1];

            Object obj1;
            Object obj2;

            if (genericParam1 instanceof TypeVariable) {
                obj1 = createJavaBean(typeParams.get(((TypeVariable) genericParam1).getName()));
            } else {
                obj1 = createJavaBean(genericParam1);
            }

            if (genericParam2 instanceof TypeVariable) {
                obj2 = createJavaBean(typeParams.get(((TypeVariable) genericParam2).getName()));
            } else {
                obj2 = createJavaBean(genericParam2);
            }

            map.put(obj1, obj2);

            method.invoke(data, map);
        } else {
            method.invoke(data, createJavaBean(paramType));
        }
    }
}
