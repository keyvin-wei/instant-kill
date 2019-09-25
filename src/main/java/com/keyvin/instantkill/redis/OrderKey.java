package com.keyvin.instantkill.redis;

/**
 * @author weiwh
 * @date 2019/8/11 17:32
 */
public class OrderKey extends BasePrefix {
    public static OrderKey getOrderByUidGid = new OrderKey("moug");


    public OrderKey(String prefix) {
        super(prefix);
    }

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
