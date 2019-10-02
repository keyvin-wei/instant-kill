package com.keyvin.instantkill.mq;

import com.keyvin.instantkill.domain.BuyoutOrderInfo;
import com.keyvin.instantkill.domain.OrderInfo;
import com.keyvin.instantkill.redis.RedisService;
import com.keyvin.instantkill.service.BuyoutService;
import com.keyvin.instantkill.service.GoodsService;
import com.keyvin.instantkill.service.OrderService;
import com.keyvin.instantkill.util.CodeMsg;
import com.keyvin.instantkill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 接收者
 * @author weiwh
 * @date 2019/9/26 22:33
 */
@Service
public class MQReceiver {
    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);
    @Autowired
    private BuyoutService buyoutService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = MQConfig.BUYOUT_QUEUE)
    public void receiveBuyoutMessage(String msg){
        try {
            BuyoutMessage bm = RedisService.stringToBean(msg, BuyoutMessage.class);
            log.info("MQ接收数据receiveBuyoutMessage：" + msg);

            //判断库存
            GoodsVo goodsVo = goodsService.getGoodsByGid(bm.getGoodsId());
            int stock = goodsVo.getStockCount();
            if(stock <= 0){
                return;
            }
            //判断是否已经秒杀
            BuyoutOrderInfo buyoutOrderInfo = orderService.getOrderByUidGid(bm.getTbUser().getUserId(), bm.getGoodsId());
            if (buyoutOrderInfo != null){
                return;
            }
            //减库存，下订单，写入秒杀订单
            OrderInfo orderInfo  = buyoutService.buyout(bm.getTbUser(), goodsVo);
            if(orderInfo == null){
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @RabbitListener(queues = MQConfig.QUEUE)
    public void receive(String msg){
        log.info("MQ接收数据：" + msg);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void receiveTopic1(String msg){
        log.info("MQ接收数据receiveTopic1：" + msg);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void receiveTopic2(String msg){
        log.info("MQ接收数据receiveTopic2：" + msg);
    }

    @RabbitListener(queues = MQConfig.HEADERS_QUEUE1)
    public void receiveHeaders(byte[] msg){
        log.info("MQ接收数据receiveHeaders：" + new String(msg));
    }
}
