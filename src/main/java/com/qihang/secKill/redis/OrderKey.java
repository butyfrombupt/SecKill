package com.qihang.secKill.redis;

/**
 * Created by wsbty on 2019/6/21.
 */
public class OrderKey extends BasePrefix {

    public OrderKey(String prefix) {
        super(prefix);
    }
    public static OrderKey getSeckillOrderByUidGid = new OrderKey("seckillOrder");
}