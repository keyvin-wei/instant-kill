package com.keyvin.instantkill.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 两次MD5，页面转md5后传给后台，后台又将加密了的转一次md5
 * @author weiwh
 * @date 2019/8/12 0:12
 */
public class MD5Util {

    private static final String salt = "1a2b3c4d";

    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    public static String inputPassToFromPass(String inputPass){
        String str = ""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String formPassToDbPass(String formPass, String saltDB) {
        String str = ""+saltDB.charAt(0)+saltDB.charAt(2)+formPass+saltDB.charAt(5)+saltDB.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String input, String saltDB){
        String formPass = inputPassToFromPass(input);
        String dbPass = formPassToDbPass(formPass, saltDB);
        return dbPass;
    }


    public static void main(String[] args) {
        // System.out.println(inputPassToFromPass("123456"));  //d3b1294a61a07da9b49b6e22b2cbd7f9
        // System.out.println(formPassToDbPass(inputPassToFromPass("123456"), "1a2b3c4d"));//b7797cce01b4b131b433b6acf4add449
        // System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));

    }

}
