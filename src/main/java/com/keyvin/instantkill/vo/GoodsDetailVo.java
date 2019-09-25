package com.keyvin.instantkill.vo;

import com.keyvin.instantkill.domain.TbUser;

/**
 * @author weiwh
 * @date 2019/9/25 11:52
 */
public class GoodsDetailVo {

    private int isStart=0;
    private int remainSecond=0;
    private GoodsVo goodsVo;
    private TbUser tbUser;

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }

    public int getIsStart() {
        return isStart;
    }

    public void setIsStart(int isStart) {
        this.isStart = isStart;
    }

    public int getRemainSecond() {
        return remainSecond;
    }

    public void setRemainSecond(int remainSecond) {
        this.remainSecond = remainSecond;
    }

    public TbUser getTbUser() {
        return tbUser;
    }

    public void setTbUser(TbUser tbUser) {
        this.tbUser = tbUser;
    }
}
