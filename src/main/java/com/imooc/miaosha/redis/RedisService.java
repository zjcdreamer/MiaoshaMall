package com.imooc.miaosha.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;


    public <T> T get(String key, Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = jedis.get(key);
            T t = stringToBean(str, clazz);
            return t;
        }finally {

        }
    }

    public <T> boolean set(String key, T val){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(val);
            if(str == null || str.length() <= 0) return false;
            jedis.set(key, str);
            return true;
        }finally {

        }
    }

    private <T> String beanToString(T val) {
        if(val == null) {
            return null;
        }
        Class<?> clazz = val.getClass();
        if(clazz == int.class || clazz == Integer.class) {
            return ""+val;
        }else if(clazz == String.class) {
            return (String)val;
        }else if(clazz == long.class || clazz == Long.class) {
            return ""+val;
        }else {
            return JSON.toJSONString(val);
        }
    }

    private <T> T stringToBean(String str, Class<T> clazz) {
        if(str == null || str.length() <= 0 || clazz == null) return null;
        if (clazz == Integer.class || clazz == int.class)
        {
            return (T) Integer.valueOf(str);
        }
        if (clazz == String.class){
            return (T)str;
        }
        if (clazz == Long.class){
            return (T)Long.valueOf(str);
        }
        else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

}
