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

    /**
     * 根据K和V的类型，获取V的值
     * @param prefix
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix prefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            String str = jedis.get(realKey);
            T t = stringToBean(str, clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 向Redis中添加一个K-V对
     * @param prefix
     * @param key
     * @param val
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix prefix, String key, T val){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToString(val);
            if(str == null || str.length() <= 0) return false;
            String realKey = prefix.getPrefix()+key;
            int second = prefix.empireSecond();
            if(second <=0){
                jedis.set(realKey, str);
            }else {
                jedis.setex(realKey,second,str); //设置一定时间后该K-V对过期
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean delete(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            long res = jedis.del(realKey);
            return res > 0;
        }finally {
            returnToPool(jedis);
        }
    }


    /**
     * 检测提供的K是否存在
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> boolean exists(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     *将提供的K加1
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long incr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    /**
     * 将提供的K减1
     * @param prefix
     * @param key
     * @param <T>
     * @return
     */
    public <T> Long decr(KeyPrefix prefix, String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix()+key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    public static <T> String beanToString(T val) {
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

    public static <T> T stringToBean(String str, Class<T> clazz) {
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

    private void returnToPool(Jedis jedis) {
        if(jedis != null) {
            jedis.close();
        }
    }

}
