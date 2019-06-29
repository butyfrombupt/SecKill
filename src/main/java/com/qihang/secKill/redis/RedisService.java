package com.qihang.secKill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by wsbty on 2019/6/14.
 */
@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;



    public <T> T get (KeyPrefix prefix,String key,Class<T> clazz){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = strToBean(str,clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean set (KeyPrefix prefix,String key,T value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = beanToStr(value);
            if(str ==null ||str.length()<=0){
                return false;
            }
            String realKey = prefix.getPrefix() + key;
            int seconds = prefix.expireSeconds();
            if(seconds <= 0){
                jedis.set(realKey,str);
            }
            else {
                jedis.setex(realKey,seconds,str);
            }
            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean exists (KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }
    /*
    增加值
     */
    public <T> Long incr (KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.incr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }
    /*
        减少值
    */
    public <T> Long decr (KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    public boolean delete (KeyPrefix prefix,String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = prefix.getPrefix() + key;
            long ret = jedis.del(realKey);
            return ret >0;
        }finally {
            returnToPool(jedis);
        }
    }

    public static  <T> String beanToStr(T value) {
        if(value == null){
            return null;
        }
        Class <?> clazz = value.getClass();
        if(clazz == int.class ||clazz == Integer.class){
            return ""+value;
        }
        else if(clazz == String.class){
            return (String)value;
        }
        else if(clazz == Long.class || clazz == long.class){
            return ""+value;
        }
        else {
            return JSON.toJSONString(value);
        }
    }

    public static  <T> T strToBean(String str,Class<T> clazz) {
        if(str == null ||str.length()<=0 || clazz==null){
            return null;
        }
        if(clazz == int.class ||clazz == Integer.class){
            return (T)Integer.valueOf(str);
        }
        else if(clazz == String.class){
            return (T)str;
        }
        else if(clazz == Long.class || clazz == long.class){
            return (T)Long.valueOf(str);
        }
        else {
            return JSON.toJavaObject(JSON.parseObject(str),clazz);
        }
    }

    private void returnToPool(Jedis jedis) {
        if(jedis !=null){
            jedis.close();
        }
    }



}
