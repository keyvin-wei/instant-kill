package com.keyvin.instantkill.service;

import com.keyvin.instantkill.dao.TbUserDao;
import com.keyvin.instantkill.domain.TbUser;
import com.keyvin.instantkill.exception.GlobalException;
import com.keyvin.instantkill.redis.RedisService;
import com.keyvin.instantkill.redis.UserKey;
import com.keyvin.instantkill.util.CodeMsg;
import com.keyvin.instantkill.util.MD5Util;
import com.keyvin.instantkill.util.Result;
import com.keyvin.instantkill.util.UUIDUtil;
import com.keyvin.instantkill.vo.LoginVo;
import com.sun.org.apache.bcel.internal.classfile.Code;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import static com.keyvin.instantkill.redis.UserKey.COOKIE_NAME_TOKEN;

/**
 * @author weiwh
 * @date 2019/8/11 12:20
 */
@Service
public class TbUserService {

    @Autowired
    private TbUserDao tbUserDao;
    @Autowired
    private RedisService redisService;

    public TbUser getByUserId(Long id){
        return tbUserDao.getByUserId(id);
    }

    public Boolean login(HttpServletResponse response, LoginVo loginVo) {
        if(loginVo==null){
            //抛异常然后全局拦截
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String userId = loginVo.getUserId();
        String password = loginVo.getPassword();
        //判断手机号存在
        TbUser tbUser = getByUserId(Long.parseLong(userId));
        if (tbUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = tbUser.getPassword();
        String dbSalt = tbUser.getSalt();
        String calcPass = MD5Util.formPassToDbPass(password, dbSalt);
        if (!calcPass.equals(dbPass)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        String token = UUIDUtil.uuid();
        addCookie(response, token, tbUser);

        return true;
    }

    public TbUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        TbUser user= redisService.get(UserKey.token, token, TbUser.class);
        //延长cookie有效期
        if(user!=null){
            addCookie(response, token, user);
        }
        return user;
    }

    private void addCookie(HttpServletResponse response, String token, TbUser user){
        //token生成cookie
        redisService.set(UserKey.token, token, user);//分布式session，将token和用户放到redis，使用时知道token就能拿到用户
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public int insert(TbUser user){
        return tbUserDao.insert(user);
    }
}
