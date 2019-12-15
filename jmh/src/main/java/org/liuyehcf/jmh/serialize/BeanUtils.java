package org.liuyehcf.jmh.serialize;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author chenfeng.hcf
 * @date 2019/10/18
 */
public abstract class BeanUtils {

    @SuppressWarnings("unchecked")
    public static <T> T clone(T bean) {
        if (bean == null) {
            return null;
        }

        try {
            return (T) doClone(bean, Lists.newLinkedList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object doClone(Object bean, LinkedList<Object> visited) throws Exception {
        if (bean == null) {
            return null;
        } else if (ClassUtils.isPrimitiveOrWrapper(bean.getClass())) {
            return bean;
        } else if (bean instanceof String) {
            return bean;
        } else if (bean instanceof BigInteger) {
            return new BigInteger(bean.toString());
        } else if (bean instanceof BigDecimal) {
            return new BigDecimal(bean.toString());
        } else if (bean instanceof Map) {
            pushReference(visited, bean);
            Map clone = doCloneMap((Map) bean, visited);
            popReference(visited);
            return clone;
        } else if (bean instanceof Collection) {
            pushReference(visited, bean);
            Collection clone = doCloneCollection((Collection) bean, visited);
            popReference(visited);
            return clone;
        } else {
            pushReference(visited, bean);
            Object clone = doCloneBean(bean, visited);
            popReference(visited);
            return clone;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map doCloneMap(Map map, LinkedList<Object> visited) throws Exception {
        List<Pair<String, Object>> properties = getAllProperties(map);

        Constructor<?> noArgsConstructor = map.getClass().getDeclaredConstructor();
        noArgsConstructor.setAccessible(true);

        Map clone = (Map) noArgsConstructor.newInstance();

        for (Pair<String, Object> property : properties) {
            String propertyName = property.getKey();
            Object value = property.getValue();

            if (propertyName == null || value == null) {
                continue;
            }

            clone.put(propertyName, doClone(value, visited));
        }

        return clone;
    }

    @SuppressWarnings("unchecked")
    private static Collection doCloneCollection(Collection col, LinkedList<Object> visited) throws Exception {
        Constructor<?> noArgsConstructor = col.getClass().getDeclaredConstructor();
        noArgsConstructor.setAccessible(true);

        Collection clone = (Collection) noArgsConstructor.newInstance();

        for (Object value : col) {
            if (value == null) {
                clone.add(null);
            } else {
                clone.add(doClone(value, visited));
            }
        }

        return clone;
    }

    private static Object doCloneBean(Object bean, LinkedList<Object> visited) throws Exception {
        List<Pair<String, Object>> properties = getAllProperties(bean);

        Constructor<?> noArgsConstructor = bean.getClass().getDeclaredConstructor();
        noArgsConstructor.setAccessible(true);

        Object clone = noArgsConstructor.newInstance();

        for (Pair<String, Object> property : properties) {
            String propertyName = property.getKey();
            Object value = property.getValue();

            if (propertyName == null || value == null) {
                continue;
            }

            Method setMethod = getSetMethod(bean, propertyName, value.getClass());

            if (setMethod == null) {
                continue;
            }

            setMethod.setAccessible(true);
            setMethod.invoke(clone, doClone(value, visited));
        }

        return clone;
    }

    @SuppressWarnings("unchecked")
    private static List<Pair<String, Object>> getAllProperties(Object bean) throws Exception {
        List<Pair<String, Object>> properties = Lists.newArrayList();

        if (bean instanceof Map) {
            Set<Map.Entry> entrySet = ((Map) bean).entrySet();

            for (Map.Entry entry : entrySet) {
                Object key = entry.getKey();

                // skip non-string properties
                if (key instanceof String) {
                    Object value = entry.getValue();
                    if (value != null) {
                        properties.add(new ImmutablePair<>((String) key, value));
                    }
                }
            }
        } else {
            List<Method> allGetMethods = getAllGetMethods(bean);

            for (Method getMethod : allGetMethods) {
                String propertyName = getPropertyNameOfGetMethod(getMethod);

                getMethod.setAccessible(true);
                Object value = getMethod.invoke(bean);

                if (value != null) {
                    properties.add(new ImmutablePair<>(propertyName, value));
                }
            }
        }

        return properties;
    }

    private static List<Method> getAllGetMethods(Object bean) {
        List<Method> getMethods = Lists.newArrayList();
        if (bean == null) {
            return getMethods;
        }

        Method[] methods = bean.getClass().getMethods();
        if (ArrayUtils.isEmpty(methods)) {
            return getMethods;
        }

        for (Method method : methods) {
            if (!isGetMethod(method)) {
                continue;
            }

            String propertyName = getPropertyNameOfGetMethod(method);

            Method setMethod = getSetMethod(bean, propertyName, method.getReturnType());
            if (setMethod == null) {
                continue;
            }

            getMethods.add(method);
        }

        return getMethods;
    }

    private static Method getSetMethod(Object bean, String propertyName, Class<?> paramType) {
        if (bean == null || paramType == null) {
            return null;
        }

        String setMethodName = getSetMethodNameOfPropertyName(propertyName);

        Method setMethod = null;

        if (paramType.isPrimitive()) {
            setMethod = getMethod(bean, setMethodName, paramType);
            if (setMethod == null) {
                setMethod = getMethod(bean, setMethodName, ClassUtils.primitiveToWrapper(paramType));
            }

            if (setMethod == null) {
                return null;
            }

            if (!isVoid(setMethod.getReturnType())) {
                return null;
            }
        } else if (ClassUtils.isPrimitiveWrapper(paramType)) {
            setMethod = getMethod(bean, setMethodName, paramType);
            if (setMethod == null) {
                setMethod = getMethod(bean, setMethodName, ClassUtils.wrapperToPrimitive(paramType));
            }

            if (setMethod == null) {
                return null;
            }

            if (!isVoid(setMethod.getReturnType())) {
                return null;
            }
        } else {
            Method[] methods = bean.getClass().getMethods();
            for (Method method : methods) {
                if (isSetMethod(method, setMethodName, paramType)) {
                    return method;
                }
            }
        }

        return setMethod;
    }

    private static String getPropertyNameOfGetMethod(Method getMethod) {
        String methodName = getMethod.getName();
        if (isBoolean(getMethod.getReturnType())) {
            if (methodName.startsWith("is")) {
                return methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
            } else {
                return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
            }
        } else {
            return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        }
    }

    private static String getSetMethodNameOfPropertyName(String propertyName) {
        String setMethodName = "set";
        if (propertyName.length() == 1) {
            setMethodName += propertyName.toUpperCase();
        } else {
            setMethodName += propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        }

        return setMethodName;
    }

    private static boolean isGetMethod(Method method) {
        if (method.getParameterCount() != 0) {
            return false;
        }

        Class<?> returnType = method.getReturnType();
        if (isVoid(returnType)) {
            return false;
        }

        String methodName = method.getName();
        if (isBoolean(method.getReturnType())) {
            if (methodName.startsWith("is")) {
                return methodName.length() > 2;
            } else if (methodName.startsWith("get")) {
                return methodName.length() > 3;
            } else {
                return false;
            }
        } else {
            if (methodName.startsWith("get")) {
                return methodName.length() > 3;
            } else {
                return false;
            }
        }
    }

    private static boolean isSetMethod(Method method, String setMethodName, Class<?> actualParameterType) {
        if (!Objects.equals(setMethodName, method.getName())) {
            return false;
        }

        if (method.getParameterCount() != 1) {
            return false;
        }

        if (!isVoid(method.getReturnType())) {
            return false;
        }

        Class<?> parameterType = method.getParameterTypes()[0];
        return parameterType.isAssignableFrom(actualParameterType);
    }

    private static boolean isVoid(Class<?> clazz) {
        return void.class.equals(clazz);
    }

    private static boolean isBoolean(Class<?> clazz) {
        return boolean.class.equals(clazz) ||
                Boolean.class.equals(clazz);
    }

    private static Method getMethod(Object obj, String name, Class<?>... parameterTypes) {
        try {
            return obj.getClass().getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static void pushReference(LinkedList<Object> visited, Object bean) {
        if (containsReference(visited, bean)) {
            throw new RuntimeException("bean clone does not support mutual reference");
        }

        visited.push(bean);
    }

    private static void popReference(LinkedList<Object> visited) {
        visited.pop();
    }

    private static boolean containsReference(LinkedList<Object> visited, Object obj) {
        for (Object visitedObj : visited) {
            if (visitedObj == obj) {
                return true;
            }
        }
        return false;
    }
}
