package com.qihang.secKill.redis;

/**
 * Created by wsbty on 2019/6/29.
 */
public class SeckillKey extends BasePrefix {
    private SeckillKey(int expireSeconds,String prefix) {
        super(expireSeconds,prefix);
    }

    public static SeckillKey isGoodsOver = new SeckillKey(0,"goodOver");

    public static SeckillKey getPath = new SeckillKey(60,"path");

}