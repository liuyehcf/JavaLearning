package org.liuyehcf.reflect;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Date;

public class GenericParamDemo<Data, Value> {
    private Data data;

    private Value value;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public <Data, Value> void func(Data data, Value value, String s) {

    }

    public static void main(String[] args) throws Exception {
        ParameterizedType type = (ParameterizedType) new JavaBeanInitializerUtils.TypeReference<GenericParamDemo<String, Date>>() {
        }.getType();

        System.out.println("rawType: " + type.getRawType());
        System.out.println("ownerType: " + type.getOwnerType());

        for (int i = 0; i < type.getActualTypeArguments().length; i++) {
            System.out.println("actualType" + i + ": " + type.getActualTypeArguments()[i]);
        }

        TypeVariable[] typeParameters = GenericParamDemo.class.getTypeParameters();
        System.out.println("typeParameters: " + Arrays.toString(typeParameters));

        for (int i = 0; i < typeParameters.length; i++) {
            System.out.println("typeParameters" + i + "'s name: " + typeParameters[i].getName());
            System.out.println("typeParameters" + i + "'s typeName: " + typeParameters[i].getTypeName());
            System.out.println("typeParameters" + i + "'s genericDeclaration: " + typeParameters[i].getGenericDeclaration());

        }

        Method method1 = GenericParamDemo.class.getMethod("setValue", Object.class);
        Method method2 = GenericParamDemo.class.getMethod("setData", Object.class);
        Method method3 = GenericParamDemo.class.getMethod("func", Object.class, Object.class, String.class);


        printMethod(method1);
        printMethod(method2);
        printMethod(method3);

    }

    private static void printMethod(Method method) {
        System.out.println("-----------------------------");

        System.out.println("method: " + method);

        Type[] typeParameters = method.getTypeParameters();
        System.out.println("typeParameters: " + Arrays.toString(typeParameters));

        Type[] parameterTypes = method.getParameterTypes();
        System.out.println("parameterTypes: " + Arrays.toString(parameterTypes));

        Type[] genericParameterTypes = method.getGenericParameterTypes();
        System.out.println("genericParameterTypes: " + Arrays.toString(genericParameterTypes));

        Parameter[] parameters = method.getParameters();
        System.out.println("parameters: " + Arrays.toString(parameters));

        System.out.println("-----------------------------\n\n\n");
    }
}
