package com.keyvin.instantkill.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * mq交换机四种模式：direct、topic、fanout、headers
 * @author weiwh
 * @date 2019/9/26 22:33
 */
@Configuration
public class MQConfig {
    public static final String QUEUE = "queue";
    public static final String TOPIC_QUEUE1 = "topic_queue1";
    public static final String TOPIC_QUEUE2 = "topic_queue2";
    public static final String TOPIC_EXCHANGE = "topic_exchange";
    public static final String ROUTING_KEY1 = "topic_key1";
    public static final String ROUTING_KEY2 = "topic.#";
    public static final String FANOUT_EXCHANGE = "fanout_exchange";
    public static final String HEADERS_EXCHANGE = "headers_exchange";
    public static final String HEADERS_QUEUE1 = "headers_queue1";
    public static final String BUYOUT_QUEUE = "buyout_queue";

    /**
     * Direct模式，交换机Exchange
     */
    @Bean
    public Queue queue(){
        return new Queue(QUEUE, true);
    }

    /**
     * topic模式，交换机Exchange
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1, true);
    }
    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2, true);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY1);
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUTING_KEY2);
    }

    /**
     * fanout模式，广播，交换机Exchange
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding fanoutBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    /**
     * headers模式，交换机Exchange
     */
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);//交换机
    }
    @Bean
    public Queue headersQueue(){
        return new Queue(HEADERS_QUEUE1, true);//队列
    }
    @Bean
    public Binding headersBinding(){
        Map<String, Object> map = new HashMap<>();
        map.put("headers1", "value1");
        map.put("headers2", "value2");
        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(map).match();
    }

}
