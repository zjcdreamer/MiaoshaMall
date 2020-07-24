package com.imooc.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    private static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassToFormPass(String pass){
        String str = salt.charAt(0)+salt.charAt(4)+salt.charAt(5)+ pass + salt.charAt(1);
        return md5(str);
    }

    public static String formPassToDBPass(String pass, String salt){
        String str = salt.charAt(0)+salt.charAt(4)+salt.charAt(5)+ pass + salt.charAt(1);
        return md5(str);
    }

    public static String inputPassToDBPass(String pass, String salt){
        String str = inputPassToFormPass(pass);
        return formPassToDBPass(str,salt);
    }
    public static void main(String[] args) {
        System.out.println(inputPassToFormPass("123456"));
        System.out.println(formPassToDBPass(inputPassToFormPass("123456"), "1a2b3c4d"));
        System.out.println(inputPassToDBPass("123456", "1a2b3c4d"));
    }
}
