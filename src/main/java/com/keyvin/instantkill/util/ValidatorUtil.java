package com.keyvin.instantkill.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author weiwh
 * @date 2019/8/12 12:03
 */
public class ValidatorUtil {
    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String str){
        if(StringUtils.isEmpty(str)){
            return false;
        }
        Matcher m = mobile_pattern.matcher(str);
        return m.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("12345"));
        System.out.println(isMobile("15512341234"));
    }

}
