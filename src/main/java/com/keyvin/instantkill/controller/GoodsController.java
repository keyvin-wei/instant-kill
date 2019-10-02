package com.keyvin.instantkill.controller;

import com.keyvin.instantkill.anotation.AccessLimit;
import com.keyvin.instantkill.domain.BuyoutOrderInfo;
import com.keyvin.instantkill.domain.OrderInfo;
import com.keyvin.instantkill.domain.TbUser;
import com.keyvin.instantkill.mq.BuyoutMessage;
import com.keyvin.instantkill.mq.MQSender;
import com.keyvin.instantkill.redis.GoodsKey;
import com.keyvin.instantkill.redis.OrderKey;
import com.keyvin.instantkill.redis.RedisService;
import com.keyvin.instantkill.service.BuyoutService;
import com.keyvin.instantkill.service.GoodsService;
import com.keyvin.instantkill.service.OrderService;
import com.keyvin.instantkill.util.CodeMsg;
import com.keyvin.instantkill.util.MD5Util;
import com.keyvin.instantkill.util.Result;
import com.keyvin.instantkill.util.UUIDUtil;
import com.keyvin.instantkill.vo.GoodsDetailVo;
import com.keyvin.instantkill.vo.GoodsVo;
import com.keyvin.instantkill.vo.OrderVo;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品列表
 * @author weiwh
 * @date 2019/8/12 10:20
 */
@Controller
@RequestMapping("/goods")
public class GoodsController implements InitializingBean {
    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private BuyoutService buyoutService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 参数验证在 UserArgumentResolver
     * tps：可将页面手动渲染之后html放到redis，返回html静态页面，60秒过期更新，访问时取redis，可提高qps几倍
     */
    @RequestMapping("/list")
    public String list(Model model, TbUser tbUser){
        model.addAttribute("user", tbUser);
        //查询商品列表
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        model.addAttribute("goodsList", goodsList);
        System.out.println("goods list");

        return "goods";
    }

    @ResponseBody
    @RequestMapping("/detail/{goodsId}")
    public Result<GoodsDetailVo> detail(Model model, TbUser tbUser, @PathVariable("goodsId")Long goodsId){
        GoodsDetailVo detailVo = new GoodsDetailVo();
        GoodsVo goodsVo = goodsService.getGoodsByGid(goodsId);
        detailVo.setTbUser(tbUser);
        detailVo.setGoodsVo(goodsVo);
        BuyoutOrderInfo order = orderService.getOrderByUidGid(Long.valueOf(tbUser.getId()), goodsId);
        detailVo.setBuyoutOrder(order);

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
        detailVo.setIsStart(isStart);
        detailVo.setRemainSecond(remainSecond);
        return Result.success(detailVo);
    }

    /**
     * 秒杀接口
     * QPS:186
     * 3000个
     * 优化后，QPS：614,1w个
     * GET/POST区别：GET是幂等的，获取服务端数据，执行多次结果不变，POST向服务端提交数据
     */
    @ResponseBody
    @AccessLimit(seconds=5, maxCount=10, needLogin=true)
    @RequestMapping(value = "/buyout/{path}", method = RequestMethod.POST)
    public Result<Integer> buyout(TbUser tbUser, @RequestParam("goodsId")Long goodsId,
                                  @PathVariable("path")String path){
        //验证path
        boolean check = buyoutService.checkPath(tbUser, goodsId, path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_PATH_ILLEGAL);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.INVENTORY_SHORTAGE);
        }
        //redis预减库存
        long stock = redisService.decr(GoodsKey.getBuyoutGoodsStock, ""+goodsId);
        if(stock < 0){
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.INVENTORY_SHORTAGE);
        }
        //判断是否已经秒杀
        BuyoutOrderInfo buyoutOrderInfo = orderService.getOrderByUidGid(Long.valueOf(tbUser.getId()), goodsId);
        if (buyoutOrderInfo != null){
            return Result.error(CodeMsg.BUYOUT_REPEAT);
        }
        //入队MQ
        BuyoutMessage bm = new BuyoutMessage();
        bm.setGoodsId(goodsId);
        bm.setTbUser(tbUser);
        mqSender.sendBuyoutMessage(bm);
        //返回0代表排队中
        return Result.success(0);

        /*
        //判断库存
        GoodsVo goodsVo = goodsService.getGoodsByGid(goodsId);
        int stock = goodsVo.getStockCount();
        log.info("库存："+stock);
        if(stock<=0){
            return Result.error(CodeMsg.INVENTORY_SHORTAGE);
        }
        //判断是否已经秒杀
        BuyoutOrderInfo buyoutOrderInfo = orderService.getOrderByUidGid(tbUser.getUserId(), goodsId);
        log.info("是否已经秒杀："+buyoutOrderInfo);
        if (buyoutOrderInfo != null){
            return Result.error(CodeMsg.BUYOUT_REPEAT);
        }
        //减库存，下订单，写入秒杀订单
        OrderInfo orderInfo  = buyoutService.buyout(tbUser, goodsVo);
        if(orderInfo==null){
            return Result.error(CodeMsg.INVENTORY_SHORTAGE);
        }
        log.info("秒杀成功：" + tbUser.getUserId());
        return Result.success(orderInfo);
        */
    }

    /**
     * 查询秒杀结果：成功返回orderid，失败-1，排队中0
     */
    @ResponseBody
    @AccessLimit(seconds=5, maxCount=10,needLogin=true)
    @RequestMapping(value = "/buyout-result", method = RequestMethod.GET)
    public Result<Long> result(TbUser tbUser, @RequestParam("goodsId")Long goodsId){
        Long orderId = buyoutService.getBuyoutResult(tbUser.getId(), goodsId);
        return Result.success(orderId);
    }

    @ResponseBody
    @AccessLimit(seconds=5, maxCount=5,needLogin=true)
    @RequestMapping(value = "/buyout-path", method = RequestMethod.GET)
    public Result<String> getBuyoutPath(TbUser tbUser, @RequestParam("goodsId")Long goodsId,
                                        @RequestParam("verifyCode")Integer verifyCode){

        //验证输入的验证码
        boolean checked = buyoutService.checkCode(tbUser, goodsId, verifyCode);
        if(!checked){
            return Result.error(CodeMsg.VERIFY_CODE_ERROR);
        }
        //验证码正确后生成路径
        String path = buyoutService.createPath(tbUser, goodsId);
        return Result.success(path);
    }

    //生成验证码图片
    @ResponseBody
    @RequestMapping(value = "/varify-code", method = RequestMethod.GET)
    public Result<String> getVarifyCode(@RequestParam("goodsId")Long goodsId,
                                        TbUser tbUser, HttpServletResponse response){
        try {
            BufferedImage image = buyoutService.createVarifyCodeImg(tbUser, goodsId);
            OutputStream stream = response.getOutputStream();
            ImageIO.write(image, "JPEG", stream);
            stream.flush();
            stream.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(CodeMsg.BUYOUT_FAIL);
        }
    }

    @ResponseBody
    @RequestMapping("/order/{orderId}")
    public Result<OrderVo> orderDetail(TbUser tbUser, @PathVariable("orderId")Integer orderId){
        if (tbUser==null){
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        GoodsVo gv = goodsService.getGoodsByGid(orderInfo.getGoodsId());
        OrderVo orderVo = OrderVo.createFromBase(orderInfo, null);
        orderVo.setImg(gv.getImg());
        orderVo.setPrice(gv.getPrice());
        if(orderInfo==null){
            return Result.error(CodeMsg.ORDER_NOT_FOUND);
        }
        return Result.success(orderVo);
    }

    @ResponseBody
    @RequestMapping("/reset")
    public String resetGoods(Model model, TbUser tbUser){
        try {
            orderService.resetGoods();
            afterPropertiesSet();
            redisService.delete(OrderKey.getOrderByUidGid, ""+tbUser.getId()+"_1");
            redisService.delete(OrderKey.getOrderByUidGid, ""+tbUser.getId()+"_2");

        }catch (Exception e){
            e.printStackTrace();
        }
        return "success";
    }

    @ResponseBody
    @RequestMapping("/mq")
    public String testMQ(){
        mqSender.send("Controller发送了一条测试数据！Direct");
        mqSender.sendTopic("Controller发送了一条测试数据！topic");
        mqSender.sendFanout("Controller发送了一条测试数据！广播");
        mqSender.sendHeaders("Controller发送了一条测试数据！headers");
        return "success";
    }

    /**
     * 系统初始化 afterPropertiesSet
     * 系统启动时将商品库存加载到redis缓存中
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList==null){
            return;
        }
        for (GoodsVo vo: goodsList){
            redisService.set(GoodsKey.getBuyoutGoodsStock, ""+vo.getId(), vo.getStockCount());
            localOverMap.put(vo.getId(), false);
        }

    }
}
