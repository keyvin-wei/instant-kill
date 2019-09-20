package com.keyvin.instantkill.util;

import java.util.UUID;

/**
 * @author weiwh
 * @date 2019/8/13 11:28
 */
public class UUIDUtil {

    public static String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }

}
