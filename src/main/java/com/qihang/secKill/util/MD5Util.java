package com.qihang.secKill.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by wsbty on 2019/6/16.
 */
public class MD5Util {

    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    private static final  String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String input){
        String str = "" + salt.charAt(0) + salt.charAt(2) + input +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDbPass(String formPass,String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass +salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String input,String saltDB){
        String formPass = inputPassToFormPass(input);
        String dbPass = formPassToDbPass(formPass,saltDB);
        return dbPass;
    }
    public static void main(String[] args) {
        System.out.println(inputPassToDbPass("123456","1a2b3c4d"));
    }
}
