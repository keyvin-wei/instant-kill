package com.keyvin.instantkill.vo;

import com.keyvin.instantkill.anotation.IsMobile;

import javax.validation.constraints.NotNull;

/**
 * @author weiwh
 * @date 2019/8/12 10:57
 */
public class LoginVo {
    @NotNull
    @IsMobile
    private String userId;
    private String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+":"+userId+","+password;
    }
}
