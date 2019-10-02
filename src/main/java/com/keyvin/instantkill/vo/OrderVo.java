package com.keyvin.instantkill.vo;

import com.keyvin.instantkill.domain.Goods;
import com.keyvin.instantkill.domain.OrderInfo;

import java.math.BigDecimal;

/**
 * @author weiwh
 * @date 2019/10/2 16:11
 */
public class OrderVo extends OrderInfo {
    private String img;
    private BigDecimal price;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public static OrderVo createFromBase(OrderInfo order, Goods g){
        OrderVo vo = new OrderVo();
        vo.setCreateDate(order.getCreateDate());
        vo.setDeliveryAddrId(order.getDeliveryAddrId());
        vo.setGoodsCount(order.getGoodsCount());
        vo.setGoodsId(order.getGoodsId());
        vo.setGoodsName(order.getGoodsName());
        vo.setGoodsPrice(order.getGoodsPrice());
        vo.setOrderChannel(order.getOrderChannel());
        vo.setStatus(order.getStatus());
        vo.setUserId(order.getUserId());
        vo.setId(order.getId());
        if(g!=null){
            vo.setImg(g.getImg());
            vo.setPrice(g.getPrice());
        }
        return vo;
    }
}
