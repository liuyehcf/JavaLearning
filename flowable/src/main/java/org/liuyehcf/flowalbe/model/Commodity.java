package org.liuyehcf.flowalbe.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chenlu
 * @date 2018/7/26
 */
public class Commodity {

    public static final Map<String, Integer> PRODUCT_MAP;

    public static final Map<Integer, String> ORDER_MAP;

    static {
        Map<String, Integer> productMap = new LinkedHashMap<>();
        Map<Integer, String> orderMap = new LinkedHashMap<>();

        addProduct(orderMap, productMap, "TV", 10000);
        addProduct(orderMap, productMap, "Air Condition", 3000);
        addProduct(orderMap, productMap, "Wash Machine", 3000);
        addProduct(orderMap, productMap, "Refrigerator", 1500);
        addProduct(orderMap, productMap, "Induction Cooker", 900);
        addProduct(orderMap, productMap, "Rice Cooker", 800);
        addProduct(orderMap, productMap, "Hood", 2300);
        addProduct(orderMap, productMap, "Dishwasher", 3900);

        PRODUCT_MAP = Collections.unmodifiableMap(productMap);
        ORDER_MAP = Collections.unmodifiableMap(orderMap);
    }

    private static void addProduct(Map<Integer, String> orderMap, Map<String, Integer> productMap, String name, Integer price) {
        productMap.put(name, price);
        orderMap.put(productMap.size(), name);
    }
}
