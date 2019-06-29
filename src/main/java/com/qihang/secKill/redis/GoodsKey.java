package com.qihang.secKill.redis;

/**
 * Created by wsbty on 2019/6/21.
 */
public class GoodsKey extends  BasePrefix{
    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey getGoodsList = new GoodsKey(60, "goodList");
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "goodDetail");
    public static GoodsKey getGoodsStock = new GoodsKey(0, "goodStock");
}
