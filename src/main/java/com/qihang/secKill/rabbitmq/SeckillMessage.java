package com.qihang.secKill.rabbitmq;

import com.qihang.secKill.domain.User;

/**
 * Created by wsbty on 2019/6/29.
 */
public class SeckillMessage {
    private User user;
    private long goodsId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
