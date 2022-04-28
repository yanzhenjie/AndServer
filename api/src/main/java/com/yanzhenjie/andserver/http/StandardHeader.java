package com.yanzhenjie.andserver.http;

public class StandardHeader implements Header {
    private String name;
    private String value;
    private boolean sensitive;

    @Override
    public String getName() {
        return name;
    }

    public StandardHeader setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String getValue() {
        return value;
    }

    public StandardHeader setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean isSensitive() {
        return sensitive;
    }

    public StandardHeader setSensitive(boolean sensitive) {
        this.sensitive = sensitive;
        return this;
    }
}
