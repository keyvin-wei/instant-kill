package com.keyvin.instantkill.controller;

import com.keyvin.instantkill.domain.BuyoutOrderInfo;
import com.keyvin.instantkill.domain.OrderInfo;
import com.keyvin.instantkill.domain.TbUser;
import com.keyvin.instantkill.redis.RedisService;
import com.keyvin.instantkill.service.BuyoutService;
import com.keyvin.instantkill.service.GoodsService;
import com.keyvin.instantkill.service.OrderService;
import com.keyvin.instantkill.service.TbUserService;
import com.keyvin.instantkill.util.CodeMsg;
import com.keyvin.instantkill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 商品列表
 * @author weiwh
 * @date 2019/8/12 10:20
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {
    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private BuyoutService buyoutService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;

    //参数验证在 UserArgumentResolver
    @RequestMapping("/list")
    public String list(Model model, TbUser tbUser){
        model.addAttribute("user", tbUser);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        System.out.println("goods list");
        return "goods";
    }

    @RequestMapping("/detail/{goodsId}")
    public String detail(Model model, TbUser tbUser, @PathVariable("goodsId")Long goodsId){
        model.addAttribute("user", tbUser);
        GoodsVo goodsVo = goodsService.getGoodsByGid(goodsId);
        model.addAttribute("goodsVo", goodsVo);

        long startAt = goodsVo.getStartDate().getTime();
        long endAt = goodsVo.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int isStart = 0;        //是否开始：0未开始，1正在，2未开始
        int remainSecond = 0;   //距离开始还有多少秒
        if(now<startAt){
            isStart = 0;
            remainSecond = (int)(startAt-now)/1000;
        }else if(now>endAt){
            isStart = 2;
            remainSecond = -1;
        }else{
            isStart = 1;
        }
        model.addAttribute("isStart", isStart);
        model.addAttribute("remainSecond", remainSecond);

        return "goods_detail";
    }

    /**
     * 秒杀接口
     */
    @RequestMapping("/buyout")
    public String buyout(Model model, TbUser tbUser, @RequestParam("goodsId")Long goodsId){
        model.addAttribute("user", tbUser);
        //判断库存
        GoodsVo goodsVo = goodsService.getGoodsByGid(goodsId);
        int stock = goodsVo.getStock();
        if(stock<=0){
            model.addAttribute("error_msg", CodeMsg.INVENTORY_SHORTAGE);
            return "buyout_fail";
        }
        //判断是否已经秒杀
        BuyoutOrderInfo buyoutOrderInfo = orderService.getOrderByUidGid(tbUser.getUserId(), goodsId);
        if (buyoutOrderInfo != null){
            model.addAttribute("error_msg", CodeMsg.BUYOUT_REPEAT);
            return "buyout_fail";
        }
        //减库存，下订单，写入秒杀订单
        OrderInfo orderInfo  = buyoutService.buyout(tbUser, goodsVo);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goodsVo", goodsVo);

        return "buyout_success";
    }


    @ResponseBody
    @RequestMapping("/reset")
    public String resetGoods(Model model, TbUser tbUser){
        orderService.resetGoods();
        return "success";
    }

}
