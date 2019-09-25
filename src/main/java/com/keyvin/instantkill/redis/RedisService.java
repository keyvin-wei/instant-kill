package com.keyvin.instantkill.redis;

/**
 * @author weiwh
 * @date 2019/8/11 16:47
 */

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {
    @Autowired
    private JedisPool jedisPool;

    /**
     * 获取单个对象
     */
    public <T> T get(BasePrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;  //生成真正的key
            String str = jedis.get(realKey);
            T t = stringToBean(str, clazz);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 设置对象
     */
    public <T> boolean set(BasePrefix prefix, String key, T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(value);
            if (str==null||str.length()<0){
                return false;
            }
            String realKey = prefix.getPrefix() + key;  //生成真正的key
            int sec = prefix.expireSeconds();
            if (sec<=0){
                jedis.set(realKey, str);
            }else {
                jedis.setex(realKey, sec, str);
            }
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 判断key是否存在
     */
    public <T> boolean exists(BasePrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;  //生成真正的key
            return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值，若key=100，则incr key返回101
     * <br>原子操作，快
     */
    public <T> Long incr(BasePrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;  //生成真正的key
            return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少值，若key=100，则decr key返回99
     */
    public <T> Long decr(BasePrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;  //生成真正的key
            return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 删除
     */
    public boolean delete(BasePrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;  //生成真正的key
            long ret = jedis.del(realKey);
            return ret>0;
        } finally {
            returnToPool(jedis);
        }
    }

    private <T> String beanToString(T value) {
        if (value ==null){
            return null;
        }
        Class<?> clazz = value.getClass();
        if (clazz ==int.class|| clazz == Integer.class){
            return ""+value;
        }else if(clazz ==String.class){
            return (String)value;
        }else if(clazz ==long.class|| clazz ==Long.class){
            return ""+value;
        }else {
            return JSON.toJSONString(value);
        }
    }

    private <T> T stringToBean(String str, Class<T> clazz) {
        if (str==null||str.length()<=0){
            return null;
        }else if (clazz ==int.class|| clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }else if(clazz ==String.class){
            return (T)str;
        }else if(clazz ==long.class|| clazz ==Long.class){
            return (T)Long.valueOf(str);
        }else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis!=null){
            jedis.close();
        }
    }


}
