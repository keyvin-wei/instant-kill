package com.keyvin.instantkill.service;

import com.keyvin.instantkill.dao.GoodsDao;
import com.keyvin.instantkill.domain.BuyoutGoods;
import com.keyvin.instantkill.domain.Goods;
import com.keyvin.instantkill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author weiwh
 * @date 2019/8/14 11:13
 */
@Service
public class GoodsService {
    @Autowired
    private GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsByGid(Long goodsId) {
        return goodsDao.getGoodsByGid(goodsId);
    }

    public int reduceStock(GoodsVo goods) {
        BuyoutGoods go = new BuyoutGoods();
        go.setGoodsId(goods.getId());
        return goodsDao.reduceStock(go);
    }


    public void resetBuyoutGoods() {
        goodsDao.resetBuyoutGoods();
    }
}
