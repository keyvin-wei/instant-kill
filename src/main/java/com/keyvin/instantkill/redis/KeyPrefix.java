package com.keyvin.instantkill.redis;

/**
 * @author weiwh
 * @date 2019/8/11 17:28
 */
public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}
