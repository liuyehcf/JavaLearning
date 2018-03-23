package org.liuyehcf.grammar.cfg.lr;

import org.liuyehcf.grammar.utils.ListUtils;

import java.util.Collections;
import java.util.List;

public class Closure {
    /**
     * 项目集闭包id
     */
    private final int id;

    /**
     * 核心项目集
     */
    private final List<Item> coreItems;

    /**
     * 不包括核心项目在内的其他等价项目
     */
    private final List<Item> equalItems;

    /**
     * 括核心项目在内的所有项目
     */
    private final List<Item> items;

    Closure(int id, List<Item> coreItems, List<Item> equalItems) {
        this.id = id;
        this.coreItems = Collections.unmodifiableList(ListUtils.sort(coreItems));
        this.equalItems = Collections.unmodifiableList(ListUtils.sort(equalItems));
        this.items = Collections.unmodifiableList(ListUtils.of(coreItems, equalItems));
    }

    public int getId() {
        return id;
    }

    public List<Item> getItems() {
        return items;
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
        StringBuilder sb = new StringBuilder();

        sb.append('{');

        sb.append('\"')
                .append("id")
                .append('\"')
                .append(':')
                .append('\"')
                .append(id)
                .append('\"');

        sb.append(',');

        sb.append('\"')
                .append("coreItems")
                .append('\"')
                .append(':')
                .append('\"')
                .append(coreItems)
                .append('\"');

        sb.append(',');

        sb.append('\"')
                .append("equalItems")
                .append('\"')
                .append(':')
                .append('\"')
                .append(equalItems)
                .append('\"');

        sb.append('}');

        return sb.toString();
    }
}
