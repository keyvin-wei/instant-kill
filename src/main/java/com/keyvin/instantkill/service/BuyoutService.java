package com.keyvin.instantkill.service;

import com.keyvin.instantkill.dao.GoodsDao;
import com.keyvin.instantkill.domain.*;
import com.keyvin.instantkill.redis.OrderKey;
import com.keyvin.instantkill.redis.RedisService;
import com.keyvin.instantkill.util.MD5Util;
import com.keyvin.instantkill.util.UUIDUtil;
import com.keyvin.instantkill.util.Util;
import com.keyvin.instantkill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

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
    @Autowired
    private RedisService redisService;

    //事务
    @Transactional
    public OrderInfo buyout(TbUser tbUser, GoodsVo goodsVo) {
        //减库存
        int ret = goodsService.reduceStock(goodsVo);
        if(ret <= 0){
            //库存不足
            setGoodsOver(goodsVo.getId());
            return null;
        }
        //下订单，写入秒杀订单
        OrderInfo orderInfo = orderService.createOrder(tbUser, goodsVo);
        return orderInfo;
    }

    /**
     * 成功返回orderid，失败-1，排队中0
     */
    public Long getBuyoutResult(Integer userId, Long goodsId) {

        BuyoutOrderInfo order = orderService.getOrderByUidGid(Long.valueOf(userId), goodsId);
        if(order != null){
            //秒杀已成功
            return order.getOrderId();
        }else{
            boolean isOver = getGoodsOver(goodsId);
            if(isOver){
                return -1L;
            }else {
                return 0L;
            }
        }
    }

    private Boolean getGoodsOver(Long goodsId) {
        //判断法一：判断值
        // return redisService.get(OrderKey.isGoodsOver, ""+goodsId, Boolean.class);
        //判断法二：判断key是否存在
        return redisService.exists(OrderKey.isGoodsOver, ""+goodsId);
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(OrderKey.isGoodsOver, ""+goodsId, true);
    }

    public boolean checkPath(TbUser tbUser, Long goodsId, String path) {
        if(tbUser == null || path==null){
            return false;
        }
        String oldPath = redisService.get(OrderKey.getBuyoutPath, ""+tbUser.getId()+"_"+goodsId, String.class);
        return path.equals(oldPath);
    }

    public String createPath(TbUser tbUser, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(OrderKey.getBuyoutPath, ""+tbUser.getId()+"_"+goodsId, str);
        return str;
    }

    public BufferedImage createVarifyCodeImg(TbUser tbUser, Long goodsId) {
        if(tbUser == null || goodsId==null|| goodsId<=0){
            return null;
        }
        int width = 80;
        int height = 32;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0,0,width,height);
        g.setColor(Color.black);
        g.fillRect(0,0,width-1,height-1);
        Random ran = new Random();
        for(int i=0;i<50;i++){
            int x = ran.nextInt(width);
            int y = ran.nextInt(height);
            g.drawOval(x,y,0,0);
        }
        String code = createCode(ran);
        g.setColor(new Color(170, 146, 250));
        g.setFont(new Font("Candara",Font.BOLD,24));
        g.drawString(code,8,24);
        g.dispose();
        //验证码计算结果存到redis
        Integer result = calc(code);
        setCode(tbUser, goodsId, result);
        return image;
    }

    private static int calc(String code) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (int) engine.eval(code);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 只做：加减乘
     */
    private static String createCode(Random ran) {
        int a = ran.nextInt(10);
        int b = ran.nextInt(10);
        int c = ran.nextInt(10);
        char op1 = Util.ops[ran.nextInt(3)];
        char op2 = Util.ops[ran.nextInt(3)];
        String str = ""+a+op1+b+op2+c;
        return str;
    }

    private void setCode(TbUser tbUser, Long goodsId, Integer result) {
        redisService.delete(OrderKey.getBuyoutVerifyCode, tbUser.getId() + "," + goodsId);
        redisService.set(OrderKey.getBuyoutVerifyCode, tbUser.getId()+","+goodsId, result);
    }

    public boolean checkCode(TbUser tbUser, Long goodsId, Integer verifyCode) {
        if(tbUser == null || goodsId==null|| goodsId<=0|| verifyCode==null){
            return false;
        }
        Integer old = redisService.get(OrderKey.getBuyoutVerifyCode, tbUser.getId() + "," + goodsId, Integer.class);
        if(old != null && old == verifyCode){
            //用完后删除
            redisService.delete(OrderKey.getBuyoutVerifyCode, tbUser.getId() + "," + goodsId);
            return true;
        }
        return false;
    }
}
