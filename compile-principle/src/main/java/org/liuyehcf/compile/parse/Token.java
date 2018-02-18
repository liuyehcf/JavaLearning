package org.liuyehcf.compile.parse;

public class Token {
    private String value;
    private Boolean isTerminator;
    private Integer type;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getTerminator() {
        return isTerminator;
    }

    public void setTerminator(Boolean terminator) {
        isTerminator = terminator;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
