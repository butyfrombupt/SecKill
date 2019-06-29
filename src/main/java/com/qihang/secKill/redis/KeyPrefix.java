package com.qihang.secKill.redis;

/**
 * Created by wsbty on 2019/6/14.
 */
public interface KeyPrefix {

    public int expireSeconds();

    public String getPrefix();
}
