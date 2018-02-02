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
    private static final String STRING_DEFAULT_PREFIX = "default";

    private static final Map<Class, Object> DEFAULT_VALUE_OF_BASIC_CLASS = new HashMap<>();

    private static final String SET_METHOD_PREFIX = "set";
    private static final Integer SET_METHOD_PARAM_COUNT = 1;
    private static final Class SET_METHOD_RETURN_TYPE = void.class;

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
     * 唯一对外接口
     *
     * @param typeReference
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T createJavaBean(TypeReference<T> typeReference) {
        if (typeReference == null) {
            throw new NullPointerException();
        }
        return (T) createJavaBean(typeReference.getType());
    }

    /**
     * 初始化JavaBean
     *
     * @param type
     * @return
     */
    private static Object createJavaBean(Type type) {
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

        Object obj = createInstance(clazz);

        for (Method setMethod : getSetMethods(clazz)) {

            // 拿到set方法的参数类型
            Type paramType = setMethod.getGenericParameterTypes()[0];

            // 填充默认值
            setDefaultValue(obj, setMethod, paramType);
        }

        return obj;
    }

    /**
     * 通过带有泛型实参的ParameterizedType创建JavaBean
     *
     * @param type
     * @return
     */
    private Object createJavaBeanWithGenericType(ParameterizedType type) {

        Class clazz = (Class) type.getRawType();

        Object obj = createInstance(clazz);

        for (Method setMethod : getSetMethods(clazz)) {
            // 拿到set方法的参数类型
            Type paramType = setMethod.getGenericParameterTypes()[0];


            if (paramType instanceof TypeVariable) {
                // 如果参数类型是泛型形参，根据映射关系找到泛型形参对应的泛型实参
                Type actualType = genericTypes.get(((TypeVariable) paramType).getName());
                setDefaultValue(obj, setMethod, actualType);
            } else {
                // 参数类型是确切的类型，可能是Class，也可能是ParameterizedType
                setDefaultValue(obj, setMethod, paramType);
            }
        }

        return obj;
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
            if (method.getName().startsWith(SET_METHOD_PREFIX)
                    && SET_METHOD_PARAM_COUNT.equals(method.getParameterCount())
                    && SET_METHOD_RETURN_TYPE.equals(method.getReturnType())) {
                setMethods.add(method);
            }
        }
        return setMethods;
    }

    /**
     * 为属性设置默认值，根据参数类型进行分发
     *
     * @param obj
     * @param method
     * @param paramType
     */
    private void setDefaultValue(Object obj, Method method, Type paramType) {
        try {
            if (paramType instanceof Class) {
                // 普通参数
                setDefaultValueOfNormal(obj, method, (Class) paramType);
            } else if (paramType instanceof ParameterizedType) {
                // 泛型实参
                setDefaultValueOfGeneric(obj, method, (ParameterizedType) paramType);
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
     * @param obj
     * @param method
     * @param paramClass
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setDefaultValueOfNormal(Object obj, Method method, Class paramClass) throws IllegalAccessException, InvocationTargetException {
        if (DEFAULT_VALUE_OF_BASIC_CLASS.containsKey(paramClass)) {
            // 填充基本类型
            method.invoke(obj, DEFAULT_VALUE_OF_BASIC_CLASS.get(paramClass));
        } else if (paramClass.equals(String.class)) {
            // 填充String类型
            method.invoke(obj, STRING_DEFAULT_PREFIX + getFieldName(method));
        } else {
            // 填充其他类型
            method.invoke(obj, createJavaBean(paramClass));
        }
    }

    /**
     * set方法的参数是泛型
     *
     * @param obj
     * @param method
     * @param paramType
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setDefaultValueOfGeneric(Object obj, Method method, ParameterizedType paramType) throws IllegalAccessException, InvocationTargetException {
        Class clazz = (Class) paramType.getRawType();

        if (instanceOfContainer(clazz)) {
            // 如果是容器的话，特殊处理一下
            setDefaultValueForContainer(obj, method, paramType);
        } else {
            // 其他类型
            method.invoke(obj, createJavaBean(paramType));
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
     * @param obj
     * @param method
     * @param paramType
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void setDefaultValueForContainer(Object obj, Method method, ParameterizedType paramType) throws IllegalAccessException, InvocationTargetException {
        Class clazz = (Class) paramType.getRawType();

        if (clazz.equals(List.class)) {
            List list = new ArrayList();

            Type genericParam = paramType.getActualTypeArguments()[0];

            Object value;

            for (int i = 0; i < CONTAINER_DEFAULT_SIZE; i++) {
                if (genericParam instanceof TypeVariable) {
                    value = createJavaBean(genericTypes.get(((TypeVariable) genericParam).getName()));
                } else {
                    value = createJavaBean(genericParam);
                }

                list.add(value);
            }

            method.invoke(obj, list);
        } else if (clazz.equals(Set.class)) {
            Set set = new HashSet();

            Type genericParam = paramType.getActualTypeArguments()[0];

            Object value;

            for (int i = 0; i < CONTAINER_DEFAULT_SIZE; i++) {
                if (genericParam instanceof TypeVariable) {
                    value = createJavaBean(genericTypes.get(((TypeVariable) genericParam).getName()));
                } else {
                    value = createJavaBean(genericParam);
                }

                set.add(value);
            }

            method.invoke(obj, set);
        } else if (clazz.equals(Queue.class)) {
            Queue queue = new LinkedList();

            Type genericParam = paramType.getActualTypeArguments()[0];

            Object value;

            for (int i = 0; i < CONTAINER_DEFAULT_SIZE; i++) {
                if (genericParam instanceof TypeVariable) {
                    value = createJavaBean(genericTypes.get(((TypeVariable) genericParam).getName()));
                } else {
                    value = createJavaBean(genericParam);
                }

                queue.add(value);
            }

            method.invoke(obj, queue);
        } else if (clazz.equals(Map.class)) {
            Map map = new HashMap();

            Type genericParam1 = paramType.getActualTypeArguments()[0];
            Type genericParam2 = paramType.getActualTypeArguments()[1];

            Object key;
            Object value;

            for (int i = 0; i < CONTAINER_DEFAULT_SIZE; i++) {
                if (genericParam1 instanceof TypeVariable) {
                    key = createJavaBean(genericTypes.get(((TypeVariable) genericParam1).getName()));
                } else {
                    key = createJavaBean(genericParam1);
                }

                if (genericParam2 instanceof TypeVariable) {
                    value = createJavaBean(genericTypes.get(((TypeVariable) genericParam2).getName()));
                } else {
                    value = createJavaBean(genericParam2);
                }

                map.put(key, value);
            }

            method.invoke(obj, map);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static abstract class TypeReference<T> {
        private final Type type;

        protected TypeReference() {
            Type superClass = getClass().getGenericSuperclass();

            Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];

            this.type = type;
        }

        public final Type getType() {
            return type;
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
