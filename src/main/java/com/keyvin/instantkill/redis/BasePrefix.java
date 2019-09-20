package com.keyvin.instantkill.redis;

/**
 * @author weiwh
 * @date 2019/8/11 17:30
 */
public abstract class BasePrefix implements KeyPrefix {
    private int expireSeconds;
    private String prefix;

    public BasePrefix(String prefix) {
        this.expireSeconds = 0;
        this.prefix = prefix;
    }
    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public int expireSeconds(){//0代表永不过期
        return this.expireSeconds;
    }

    public String getPrefix(){
        String className = getClass().getSimpleName();
        return className + "_" + prefix + "_";
    }

}
