package com.keyvin.instantkill.mq;

import com.keyvin.instantkill.domain.TbUser;

/**
 * @author weiwh
 * @date 2019/9/26 23:57
 */
public class BuyoutMessage {
    private long goodsId;
    private TbUser tbUser;

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public TbUser getTbUser() {
        return tbUser;
    }

    public void setTbUser(TbUser tbUser) {
        this.tbUser = tbUser;
    }
}
