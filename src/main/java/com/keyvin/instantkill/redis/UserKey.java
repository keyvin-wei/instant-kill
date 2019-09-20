package com.keyvin.instantkill.redis;

/**
 * @author weiwh
 * @date 2019/8/11 17:32
 */
public class UserKey extends BasePrefix {
    public static final String COOKIE_NAME_TOKEN = "token";
    public static final int TOKEN_EXPIRE = 3600*2;  //两小时

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
    public static UserKey token = new UserKey(TOKEN_EXPIRE, "tk");

    public UserKey(String prefix) {
        super(prefix);
    }

    public UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
