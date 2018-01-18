package org.liuyehcf.fastjson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Liuye on 2017/12/15.
 */
public class GenericTypeDemo<T> {

    public static void main(String[] args) {
        ParameterizedType type = (ParameterizedType) new MyTypeReference<Map<Set<LinkedList<Collection<ArrayList<Queue<Stack<Map<String, Object>>>>>>>, Set<LinkedList<Collection<ArrayList<Queue<Stack<Map<String, Object>>>>>>>>>() {
            // override nothing
        }.getType();

        Type rawType = type.getRawType();
        Type[] actualTypeArguments = type.getActualTypeArguments();
        Type ownerType = type.getOwnerType();

        System.out.println("type : " + type);
        System.out.println("rawType : " + rawType);
        System.out.println("actualTypeArguments : " + Arrays.toString(actualTypeArguments));
        System.out.println("ownerType : " + ownerType);


        System.out.println("各级泛型参数");

        printGenericParams(type, 0);
    }

    private static void printGenericParams(Type type, int level) {
        System.out.println(getSpace(level) + " > " + type);
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            for (Type actualTypeArgument : actualTypeArguments) {
                printGenericParams(actualTypeArgument, level + 1);
            }
        }
    }

    private static String getSpace(int level) {
        String space = "  ";
        String res = "";
        for (int i = 0; i < level; i++) {
            res += space;
        }
        return res;
    }
}
