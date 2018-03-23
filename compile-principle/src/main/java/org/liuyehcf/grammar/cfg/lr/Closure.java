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
        this.items = Collections.unmodifiableList(
                ListUtils.sort(
                        ListUtils.of(this.coreItems, this.equalItems)
                )
        );
    }

    /**
     * 两个闭包是否同心
     */
    static boolean isConcentric(Closure closureI, Closure closureJ) {
        if (closureI == closureJ) return true;

        if (closureI.getItems().size() != closureJ.getItems().size()) {
            return false;
        }

        for (int i = 0; i < closureI.getItems().size(); i++) {
            if (!closureI.items.get(i).getPrimaryProduction().equals(
                    closureJ.items.get(i).getPrimaryProduction())) {
                return false;
            }
        }

        return true;
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

        // id
        sb.append('\"')
                .append("id")
                .append('\"')
                .append(':')
                .append('\"')
                .append(id)
                .append('\"');

        sb.append(',');

        // coreItems
        sb.append('\"')
                .append("coreItems")
                .append('\"')
                .append(':')
                .append('{');
        if (!coreItems.isEmpty()) {
            int cnt = 1;
            for (Item item : coreItems) {
                sb.append('\"')
                        .append(cnt++)
                        .append('\"')
                        .append(':')
                        .append('\"')
                        .append(item)
                        .append('\"')
                        .append(',');
            }
            sb.setLength(sb.length() - 1);
        }
        sb.append('}');

        sb.append(',');

        // equalItems
        sb.append('\"')
                .append("equalItems")
                .append('\"')
                .append(':')
                .append('{');
        if (!equalItems.isEmpty()) {
            int cnt = 1;
            for (Item item : equalItems) {
                sb.append('\"')
                        .append(cnt++)
                        .append('\"')
                        .append(':')
                        .append('\"')
                        .append(item)
                        .append('\"')
                        .append(',');
            }
            sb.setLength(sb.length() - 1);
        }
        sb.append('}');


        sb.append('}');

        return sb.toString();
    }
}
