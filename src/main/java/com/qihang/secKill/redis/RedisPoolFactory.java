package com.qihang.secKill.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by wsbty on 2019/6/14.
 */
@Service
public class RedisPoolFactory {

    @Autowired
    RedisConfig redisConfig;

    @Bean
    public JedisPool JedisPoolFactory(){
        JedisPoolConfig jpConfig = new JedisPoolConfig();
        jpConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        jpConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        jpConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait()*1000);
        JedisPool pool = new JedisPool(jpConfig,redisConfig.getHost(),redisConfig.getPort(),redisConfig.getTimeout()*1000,redisConfig.getPassword(),0);
        return pool;
    }


}
