package com.keyvin.instantkill.mq;

import com.keyvin.instantkill.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 发送者
 * @author weiwh
 * @date 2019/9/26 22:33
 */
@Service
public class MQSender {
    private static Logger log = LoggerFactory.getLogger(MQSender.class);

    @Autowired
    private AmqpTemplate amqpTemplate;


    public void sendBuyoutMessage(BuyoutMessage bm) {
        String str = RedisService.beanToString(bm);
        log.info("MQ发送数据sendBuyoutMessage：" + str);
        amqpTemplate.convertAndSend(MQConfig.BUYOUT_QUEUE, str);
    }

    /**
     * 发出去
     */
    public void send(Object msg){
        String str = RedisService.beanToString(msg);
        log.info("MQ发送数据：" + str);
        amqpTemplate.convertAndSend(MQConfig.QUEUE, str);
    }

    public void sendTopic(Object msg){
        String str = RedisService.beanToString(msg);
        log.info("MQ发送数据topic：" + str);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY1, str+"1");
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.ROUTING_KEY2, str+"2");
    }

    public void sendFanout(Object msg){
        String str = RedisService.beanToString(msg);
        log.info("MQ发送数据fanout：" + str);
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", str);
    }

    public void sendHeaders(Object msg){
        String str = RedisService.beanToString(msg);
        log.info("MQ发送数据Headers：" + str);
        MessageProperties properties = new MessageProperties();
        properties.setHeader("headers1", "value1");
        properties.setHeader("headers2", "value2");
        Message m = new Message(str.getBytes(), properties);

        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", m);
    }

}
