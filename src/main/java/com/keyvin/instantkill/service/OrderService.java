package com.keyvin.instantkill.service;

import com.keyvin.instantkill.dao.GoodsDao;
import com.keyvin.instantkill.dao.OrderDao;
import com.keyvin.instantkill.domain.BuyoutOrderInfo;
import com.keyvin.instantkill.domain.OrderInfo;
import com.keyvin.instantkill.domain.TbUser;
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
    private GoodsDao goodsDao;
    @Autowired
    private OrderDao orderDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsByGid(Long goodsId) {
        return goodsDao.getGoodsByGid(goodsId);
    }

    public BuyoutOrderInfo getOrderByUidGid(Long userId, Long goodsId) {
        return orderDao.getOrderByUidGid(userId, goodsId);
    }

    @Transactional
    public OrderInfo createOrder(TbUser tbUser, GoodsVo goodsVo) {
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

        return orderInfo;
    }

    @Transactional
    public boolean resetGoods() {
        //删除订单
        orderDao.deleteOrderInfo();
        orderDao.deleteByoutOrderInfo();
        //商品秒杀数还原
        goodsDao.resetBuyoutGoods();

        return true;
    }
}
