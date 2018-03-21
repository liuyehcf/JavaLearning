package org.liuyehcf.grammar.core.parse;

import org.liuyehcf.grammar.core.MorphemeType;
import org.liuyehcf.grammar.core.definition.Symbol;

public class Token {

    // token的id。若type不是REGEX，那么id.value与value相同
    private final Symbol id;

    // token的值
    private final String value;

    // 词素类型
    private final MorphemeType type;

    public Token(Symbol id, String value, MorphemeType type) {
        this.id = id;
        this.value = value;
        this.type = type;
    }

    public Symbol getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public MorphemeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + '\"' +
                ", \"value\":\"" + value + '\"' +
                ", \"type\":\"" + type + '\"' +
                '}';
    }
}
