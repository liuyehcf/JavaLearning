package org.liuyehcf.reflect;

import java.lang.reflect.*;
import java.util.*;

public class JavaBeanBuilderUtils {
    private static final Byte BYTE_DEFAULT_VALUE = 0;
    private static final Character CHAR_DEFAULT_VALUE = 0;
    private static final Short SHORT_DEFAULT_VALUE = 0;
    private static final Integer INTEGER_DEFAULT_VALUE = 0;
    private static final Long LONG_DEFAULT_VALUE = 0L;
    private static final Float FLOAT_DEFAULT_VALUE = 0.0f;
    private static final Double DOUBLE_DEFAULT_VALUE = 0.0;
    private static final Boolean BOOLEAN_DEFAULT_VALUE = false;

    private static final Map<Class, Object> DEFAULT_VALUE_OF_BASIC_CLASS = new HashMap<>();

    private static final String SET = "set";
    private static final Integer SET_PARAM_COUNT = 1;
    private static final Class VOID_CLASS = void.class;

    private static final Set<Class> CONTAINER_CLASS_SET = new HashSet<>();
    private static final Integer CONTAINER_DEFAULT_SIZE = 3;


    /**
     * 从泛型参数名映射到实际的类型
     */
    private Map<String, Type> genericTypes;

    /**
     * 要初始化的类型
     */
    private final Type type;

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

    private JavaBeanBuilderUtils(Type type) {
        this.type = type;
        genericTypes = new HashMap<>();
        init();
    }

    /**
     * 对于泛型类型，初始化泛型参数描述与实际泛型参数的映射关系
     * 例如List有一个泛型参数T，如果传入的是List<String>类型，那么建立 "T"->java.lang.String 的映射
     */
    private void init() {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            Class clazz = (Class) parameterizedType.getRawType();

            // 通过Class可以拿到泛型形参，但无法拿到泛型实参
            TypeVariable[] typeVariables = clazz.getTypeParameters();

            // 通过ParameterizedType可以拿到泛型实参，通过继承结构保留泛型实参
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            // 维护泛型形参到泛型实参的映射关系
            for (int i = 0; i < typeVariables.length; i++) {
                genericTypes.put(
                        typeVariables[i].getName(),
                        actualTypeArguments[i]
                );
            }
        }
    }

    /**
     * 创建JavaBean，根据type的实际类型进行分发
     *
     * @return
     */
    private Object doCreateJavaBean() {
        if (type instanceof Class) {
            // 创建非泛型实例
            return createJavaBeanWithClass((Class) type);
        } else if (type instanceof ParameterizedType) {
            // 创建泛型实例
            return createJavaBeanWithGenericType((ParameterizedType) type);
        } else {
            throw new UnsupportedOperationException("暂不支持此类型的默认初始化，type: " + type);
        }
    }

    /**
     * 通过普通的Class创建JavaBean
     *
     * @param clazz
     * @return
     */
    private Object createJavaBeanWithClass(Class clazz) {

        Object data = createInstance(clazz);

        for (Method setMethod : getSetMethods(clazz)) {

            // 拿到set方法的参数类型
            Type paramType = setMethod.getGenericParameterTypes()[0];

            // 填充默认值
            setDefaultValue(data, setMethod, paramType);
        }

        return data;
    }

    /**
     * 通过带有泛型实参的ParameterizedType创建JavaBean
     *
     * @param type
     * @return
     */
    private Object createJavaBeanWithGenericType(ParameterizedType type) {

        Class clazz = (Class) type.getRawType();

        Object data = createInstance(clazz);

        for (Method setMethod : getSetMethods(clazz)) {
            // 拿到set方法的参数类型
            Type paramType = setMethod.getGenericParameterTypes()[0];


            if (paramType instanceof TypeVariable) {
                // 如果参数类型是泛型形参，根据映射关系找到泛型形参对应的泛型实参
                Type actualType = genericTypes.get(((TypeVariable) paramType).getName());
                setDefaultValue(data, setMethod, actualType);
            } else {
                // 参数类型是确切的类型，可能是Class，也可能是ParameterizedType
                setDefaultValue(data, setMethod, paramType);
            }
        }

        return data;
    }

    /**
     * 通过反射创建实例
     *
     * @param clazz
     * @return
     */
    private Object createInstance(Class clazz) {
        Object obj;
        try {
            obj = clazz.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException("不能实例化接口/抽象类/没有无参构造方法的类");
        }
        return obj;
    }

    /**
     * 返回所有set方法
     *
     * @param clazz
     * @return
     */
    private List<Method> getSetMethods(Class clazz) {
        List<Method> setMethods = new ArrayList<>();
        Method[] methods = clazz.getMethods();

        for (Method method : methods) {
            if (method.getName().startsWith(SET)
                    && SET_PARAM_COUNT.equals(method.getParameterCount())
                    && VOID_CLASS.equals(method.getReturnType())) {
                setMethods.add(method);
            }
        }
        return setMethods;
    }

    /**
     * 为属性设置默认值，根据参数类型进行分发
     *
     * @param data
     * @param method
     * @param paramType
     */
    private void setDefaultValue(Object data, Method method, Type paramType) {
        try {
            if (paramType instanceof Class) {
                // 普通参数
                setDefaultValueOfNormal(data, method, (Class) paramType);
            } else if (paramType instanceof ParameterizedType) {
                // 泛型实参
                setDefaultValueOfGeneric(data, method, (ParameterizedType) paramType);
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException();
        }
    }

    /**
     * 获取属性名
     *
     * @param method
     * @return
     */
    private String getFieldName(Method method) {
        return method.getName().substring(3);
    }


    /**
     * set方法参数是普通的类型
     *
     * @param data
     * @param method
     * @param paramClass
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setDefaultValueOfNormal(Object data, Method method, Class paramClass) throws IllegalAccessException, InvocationTargetException {
        if (DEFAULT_VALUE_OF_BASIC_CLASS.containsKey(paramClass)) {
            // 填充基本类型
            method.invoke(data, DEFAULT_VALUE_OF_BASIC_CLASS.get(paramClass));
        } else if (paramClass.equals(String.class)) {
            // 填充String类型
            method.invoke(data, "default" + getFieldName(method));
        } else {
            // 填充其他类型
            method.invoke(data, createJavaBean(paramClass));
        }
    }

    /**
     * set方法的参数是泛型
     *
     * @param data
     * @param method
     * @param paramType
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setDefaultValueOfGeneric(Object data, Method method, ParameterizedType paramType) throws IllegalAccessException, InvocationTargetException {
        Class clazz = (Class) paramType.getRawType();

        if (instanceOfContainer(clazz)) {
            // 如果是容器的话，特殊处理一下
            setDefaultValueForContainer(data, method, paramType);
        } else {
            // 其他类型
            method.invoke(data, createJavaBean(paramType));
        }
    }

    /**
     * 判断是否是容器类型
     *
     * @param clazz
     * @return
     */
    private boolean instanceOfContainer(Class clazz) {
        return CONTAINER_CLASS_SET.contains(clazz);
    }

    /**
     * 为几种不同的容器设置默认值，由于容器没有set方法，走默认逻辑就会得到一个空的容器。因此为容器填充一个值
     *
     * @param data
     * @param method
     * @param paramType
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setDefaultValueForContainer(Object data, Method method, ParameterizedType paramType) throws IllegalAccessException, InvocationTargetException {
        Class clazz = (Class) paramType.getRawType();

        if (clazz.equals(List.class)) {
            List list = new ArrayList();

            Type genericParam = paramType.getActualTypeArguments()[0];

            Object obj;

            for (int i = 0; i < CONTAINER_DEFAULT_SIZE; i++) {
                if (genericParam instanceof TypeVariable) {
                    obj = createJavaBean(genericTypes.get(((TypeVariable) genericParam).getName()));
                } else {
                    obj = createJavaBean(genericParam);
                }

                list.add(obj);
            }

            method.invoke(data, list);
        } else if (clazz.equals(Set.class)) {
            Set set = new HashSet();

            Type genericParam = paramType.getActualTypeArguments()[0];

            Object obj;

            for (int i = 0; i < CONTAINER_DEFAULT_SIZE; i++) {
                if (genericParam instanceof TypeVariable) {
                    obj = createJavaBean(genericTypes.get(((TypeVariable) genericParam).getName()));
                } else {
                    obj = createJavaBean(genericParam);
                }

                set.add(obj);
            }

            method.invoke(data, set);
        } else if (clazz.equals(Queue.class)) {
            Queue queue = new LinkedList();

            Type genericParam = paramType.getActualTypeArguments()[0];

            Object obj;

            for (int i = 0; i < CONTAINER_DEFAULT_SIZE; i++) {
                if (genericParam instanceof TypeVariable) {
                    obj = createJavaBean(genericTypes.get(((TypeVariable) genericParam).getName()));
                } else {
                    obj = createJavaBean(genericParam);
                }

                queue.add(obj);
            }

            method.invoke(data, queue);
        } else if (clazz.equals(Map.class)) {
            Map map = new HashMap();

            Type genericParam1 = paramType.getActualTypeArguments()[0];
            Type genericParam2 = paramType.getActualTypeArguments()[1];

            Object obj1;
            Object obj2;

            for (int i = 0; i < CONTAINER_DEFAULT_SIZE; i++) {
                if (genericParam1 instanceof TypeVariable) {
                    obj1 = createJavaBean(genericTypes.get(((TypeVariable) genericParam1).getName()));
                } else {
                    obj1 = createJavaBean(genericParam1);
                }

                if (genericParam2 instanceof TypeVariable) {
                    obj2 = createJavaBean(genericTypes.get(((TypeVariable) genericParam2).getName()));
                } else {
                    obj2 = createJavaBean(genericParam2);
                }

                map.put(obj1, obj2);
            }

            method.invoke(data, map);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    static {
        DEFAULT_VALUE_OF_BASIC_CLASS.put(Byte.class, BYTE_DEFAULT_VALUE);
        DEFAULT_VALUE_OF_BASIC_CLASS.put(byte.class, BYTE_DEFAULT_VALUE);

        DEFAULT_VALUE_OF_BASIC_CLASS.put(Character.class, CHAR_DEFAULT_VALUE);
        DEFAULT_VALUE_OF_BASIC_CLASS.put(char.class, CHAR_DEFAULT_VALUE);

        DEFAULT_VALUE_OF_BASIC_CLASS.put(Short.class, SHORT_DEFAULT_VALUE);
        DEFAULT_VALUE_OF_BASIC_CLASS.put(short.class, SHORT_DEFAULT_VALUE);

        DEFAULT_VALUE_OF_BASIC_CLASS.put(Integer.class, INTEGER_DEFAULT_VALUE);
        DEFAULT_VALUE_OF_BASIC_CLASS.put(int.class, INTEGER_DEFAULT_VALUE);

        DEFAULT_VALUE_OF_BASIC_CLASS.put(Long.class, LONG_DEFAULT_VALUE);
        DEFAULT_VALUE_OF_BASIC_CLASS.put(long.class, LONG_DEFAULT_VALUE);

        DEFAULT_VALUE_OF_BASIC_CLASS.put(Float.class, FLOAT_DEFAULT_VALUE);
        DEFAULT_VALUE_OF_BASIC_CLASS.put(float.class, FLOAT_DEFAULT_VALUE);

        DEFAULT_VALUE_OF_BASIC_CLASS.put(Double.class, DOUBLE_DEFAULT_VALUE);
        DEFAULT_VALUE_OF_BASIC_CLASS.put(double.class, DOUBLE_DEFAULT_VALUE);

        DEFAULT_VALUE_OF_BASIC_CLASS.put(Boolean.class, BOOLEAN_DEFAULT_VALUE);
        DEFAULT_VALUE_OF_BASIC_CLASS.put(boolean.class, BOOLEAN_DEFAULT_VALUE);
    }


    static {
        CONTAINER_CLASS_SET.add(List.class);
        CONTAINER_CLASS_SET.add(Map.class);
        CONTAINER_CLASS_SET.add(Set.class);
        CONTAINER_CLASS_SET.add(Queue.class);
    }


}
