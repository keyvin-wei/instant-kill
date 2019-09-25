package com.keyvin.instantkill.dao;

import com.keyvin.instantkill.domain.BuyoutOrderInfo;
import com.keyvin.instantkill.domain.OrderInfo;
import com.keyvin.instantkill.vo.GoodsVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author weiwh
 * @date 2019/8/11 12:17
 */
@Mapper
public interface OrderDao {

    @Select("select * from buyout_order_info where user_id=#{uid} and goods_id=#{gid}")
    public BuyoutOrderInfo getOrderByUidGid(@Param("uid")Long userId,
                                            @Param("gid")Long goodsId);

    @Insert("insert into order_info(user_id,goods_id,delivery_addr_id,goods_name,goods_count,goods_price,order_channel,status,create_date) values" +
            "(#{userId},#{goodsId},#{deliveryAddrId},#{goodsName},#{goodsCount},#{goodsPrice},#{orderChannel},#{status},#{createDate})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = Long.class, before = false, statement = "select last_insert_id()")
    public Long insertOrderInfo(OrderInfo orderInfo);

    @Insert("insert into buyout_order_info(user_id,order_id,goods_id) values" +
            "(#{userId},#{orderId},#{goodsId})")
    public int insetBuyoutOrderInfo(BuyoutOrderInfo bo);

    @Delete("delete from order_info where id > 1")
    public void deleteOrderInfo();

    @Delete("delete from buyout_order_info where id > 1")
    public void deleteByoutOrderInfo();

    @Select("select * from order_info where id=#{id}")
    public OrderInfo getOrderById(Integer id);
}
