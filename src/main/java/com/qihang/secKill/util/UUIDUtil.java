package com.qihang.secKill.util;

import java.util.UUID;

/**
 * Created by wsbty on 2019/6/17.
 */
public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
