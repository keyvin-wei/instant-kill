package com.keyvin.instantkill.config;

import com.keyvin.instantkill.domain.TbUser;

/**
 * 多线程时保存，线程安全的
 * 跟当前线程绑定，当前线程放进来跟其他线程无关，一个线程一个ThreadLocal
 * @author weiwh
 * @date 2019/10/2 14:35
 */
public class UserContext {
    private static ThreadLocal<TbUser> userHolder = new ThreadLocal<>();

    public static void setUser(TbUser user){
        userHolder.set(user);
    }

    public static TbUser getUser(){
        return userHolder.get();
    }
}
