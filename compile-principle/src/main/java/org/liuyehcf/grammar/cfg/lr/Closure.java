package org.liuyehcf.grammar.cfg.lr;

import java.util.List;

public class Closure {

    private static int count = 0;

    /**
     * 项目集闭包id
     */
    private final int id = count++;

    /**
     * 核心项目集
     */
    private final List<Item> coreItems;

    /**
     * 包括核心项目在内的所有项目
     */
    private final List<Item> items;


    Closure(List<Item> coreItems, List<Item> items) {
        this.coreItems = coreItems;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Item> getCoreItems() {
        return coreItems;
    }

    /**
     * 判断两个Closure是否相同，仅需要判断核心项目集合是否完全一致即可
     */
    public boolean isSame(List<Item> coreItems) {
        return this.coreItems.containsAll(coreItems)
                && coreItems.containsAll(this.coreItems);
    }

    @Override
    public String toString() {
        return "Closure{" +
                "id=" + id +
                ", coreItems=" + coreItems +
                ", items=" + items +
                '}';
    }
}
