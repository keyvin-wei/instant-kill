package com.keyvin.instantkill.redis;

/**
 * @author weiwh
 * @date 2019/8/11 17:32
 */
public class GoodsKey extends BasePrefix {
    public static GoodsKey getBuyoutGoodsStock = new GoodsKey(0, "gs");


    public GoodsKey(String prefix) {
        super(prefix);
    }

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
