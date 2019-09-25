package com.keyvin.instantkill.service;

import com.keyvin.instantkill.dao.GoodsDao;
import com.keyvin.instantkill.dao.OrderDao;
import com.keyvin.instantkill.domain.BuyoutOrderInfo;
import com.keyvin.instantkill.domain.OrderInfo;
import com.keyvin.instantkill.domain.TbUser;
import com.keyvin.instantkill.redis.OrderKey;
import com.keyvin.instantkill.redis.RedisService;
import com.keyvin.instantkill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author weiwh
 * @date 2019/8/14 11:13
 */
@Service
public class OrderService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private OrderDao orderDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsService.listGoodsVo();
    }

    public GoodsVo getGoodsByGid(Long goodsId) {
        return goodsService.getGoodsByGid(goodsId);
    }

    public BuyoutOrderInfo getOrderByUidGid(Long userId, Long goodsId) {
        return redisService.get(OrderKey.getOrderByUidGid, ""+userId+"_"+goodsId, BuyoutOrderInfo.class);
    }

    @Transactional
    public OrderInfo createOrder(TbUser tbUser, GoodsVo goodsVo) {
        //生成订单
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(22L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setGoodsName(goodsVo.getName());
        orderInfo.setGoodsPrice(goodsVo.getBuyoutPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(tbUser.getUserId());
        Long orderId = orderDao.insertOrderInfo(orderInfo);

        BuyoutOrderInfo bo = new BuyoutOrderInfo();
        bo.setGoodsId(goodsVo.getId());
        bo.setOrderId(orderId);
        bo.setUserId(tbUser.getUserId());
        orderDao.insetBuyoutOrderInfo(bo);
        //订单加到缓存，取时取缓存
        redisService.set(OrderKey.getOrderByUidGid, ""+tbUser.getId()+"_"+goodsVo.getId(), bo);

        return orderInfo;
    }

    @Transactional
    public boolean resetGoods() {
        //删除订单
        orderDao.deleteOrderInfo();
        orderDao.deleteByoutOrderInfo();
        //商品秒杀数还原
        goodsService.resetBuyoutGoods();

        return true;
    }

    public OrderInfo getOrderById(Integer id) {
        return orderDao.getOrderById(id);
    }
}
