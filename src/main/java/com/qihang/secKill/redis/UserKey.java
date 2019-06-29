package com.qihang.secKill.redis;

/**
 * Created by wsbty on 2019/6/14.
 */
public class UserKey extends BasePrefix {

    public UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey getById = new UserKey("id");

    public static UserKey getByName = new UserKey("name");

    public static final int TOKEN_EXPIRE = 3600*24*2;

    public static UserKey token = new UserKey(TOKEN_EXPIRE,"token");


}
