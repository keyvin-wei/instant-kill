package com.keyvin.instantkill.redis;

/**
 * @author weiwh
 * @date 2019/8/11 17:32
 */
public class OrderKey extends BasePrefix {
    public static OrderKey getOrderByUidGid = new OrderKey("moug");
    public static OrderKey isGoodsOver = new OrderKey("go");
    public static OrderKey getBuyoutPath = new OrderKey(60, "bp");
    public static OrderKey getBuyoutVerifyCode = new OrderKey(300, "vc");
    public static OrderKey access = new OrderKey(5, "ac");
    public static OrderKey withExpire(int expireSeconds){
        return new OrderKey(expireSeconds, "ac");
    }


    public OrderKey(String prefix) {
        super(prefix);
    }

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
