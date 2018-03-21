package org.liuyehcf.grammar.cfg.lr;

import java.util.List;

public class Closure {

    private static int count = 0;

    /**
     * 项目集闭包id
     */
    private final int id = count++;

    /**
     * 核心项目
     */
    private final Item coreItem;

    /**
     * 包括核心项目在内的所有项目
     */
    private final List<Item> items;


    Closure(Item coreItem, List<Item> items) {
        this.coreItem = coreItem;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public Item getCoreItem() {
        return coreItem;
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean isCoreItem(Item item) {
        return coreItem.equals(item);
    }

    @Override
    public String toString() {
        return "Closure{" +
                "id=" + id +
                ", coreItem=" + coreItem +
                ", items=" + items +
                '}';
    }
}
