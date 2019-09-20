package com.keyvin.instantkill.service;

import com.keyvin.instantkill.dao.GoodsDao;
import com.keyvin.instantkill.domain.BuyoutGoods;
import com.keyvin.instantkill.domain.Goods;
import com.keyvin.instantkill.domain.OrderInfo;
import com.keyvin.instantkill.domain.TbUser;
import com.keyvin.instantkill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author weiwh
 * @date 2019/8/14 17:53
 */
@Service
public class BuyoutService {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;

    //事务
    @Transactional
    public OrderInfo buyout(TbUser tbUser, GoodsVo goodsVo) {
        //减库存，下订单，写入秒杀订单
        BuyoutGoods goods = new BuyoutGoods();
        goods.setId(goodsVo.getId());
        goods.setStockCount(goodsVo.getStock()-1);//没意义，sql已经减一了
        goodsService.reduceStock(goods);

        OrderInfo orderInfo = orderService.createOrder(tbUser, goodsVo);

        return orderInfo;
    }
}
