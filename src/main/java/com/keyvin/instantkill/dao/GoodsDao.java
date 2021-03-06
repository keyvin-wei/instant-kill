package com.keyvin.instantkill.dao;

import com.keyvin.instantkill.domain.BuyoutGoods;
import com.keyvin.instantkill.domain.Goods;
import com.keyvin.instantkill.domain.User;
import com.keyvin.instantkill.vo.GoodsVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author weiwh
 * @date 2019/8/11 12:17
 */
@Mapper
public interface GoodsDao {

    @Select("select a.*,b.stock_count,b.buyout_price,b.start_date,b.end_date " +
            "from goods a left join buyout_goods b on a.id=b.goods_id")
    public List<GoodsVo> listGoodsVo();

    @Select("select a.*,b.stock_count,b.buyout_price,b.start_date,b.end_date " +
            "from buyout_goods b left join goods a on a.id=b.goods_id where a.id=#{gid}")
    public GoodsVo getGoodsByGid(@Param("gid") Long gid);

    @Update("update buyout_goods set stock_count =stock_count-1 where goods_id=#{goodsId} and stock_count>0")
    public int reduceStock(BuyoutGoods goods);

    @Update("update buyout_goods set stock_count =10")
    public void resetBuyoutGoods();
}
