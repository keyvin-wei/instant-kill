package com.keyvin.instantkill.config;

import com.alibaba.fastjson.JSON;
import com.keyvin.instantkill.anotation.AccessLimit;
import com.keyvin.instantkill.domain.TbUser;
import com.keyvin.instantkill.redis.OrderKey;
import com.keyvin.instantkill.redis.RedisService;
import com.keyvin.instantkill.redis.UserKey;
import com.keyvin.instantkill.service.TbUserService;
import com.keyvin.instantkill.util.CodeMsg;
import com.keyvin.instantkill.util.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

/**
 * @author weiwh
 * @date 2019/10/2 14:23
 */
@Service
public class AccessLimitInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private TbUserService tbUserService;
    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            //取用户，放到ThreadLocal
            TbUser user = getUser(request,response);
            UserContext.setUser(user);

            HandlerMethod method = (HandlerMethod)handler;
            AccessLimit accessLimit = method.getMethodAnnotation(AccessLimit.class);
            if(accessLimit ==null){
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();
            if(needLogin && user == null){
                //失败信息写到页面
                render(response, CodeMsg.SESSION_ERROR);
                return false;
            }else{
                key = key+"_"+user.getId();
            }
            //限流，查询访问次数，重构-改善既有代码的设计
            OrderKey ak = OrderKey.withExpire(seconds);
            Integer count = redisService.get(ak, key, Integer.class);
            if(count == null){
                redisService.set(ak, key, 1);
            }else if(count < maxCount){
                redisService.incr(ak, key);
            }else {
                //访问seconds秒内超过count次返回失败
                render(response, CodeMsg.ACCESS_LIMIT);
                return false;
            }

        }

        return super.preHandle(request, response, handler);
    }

    private void render(HttpServletResponse response, CodeMsg cmg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str = JSON.toJSONString(Result.error(cmg));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private TbUser getUser(HttpServletRequest request, HttpServletResponse response){
        String cookieToken = request.getParameter(UserKey.COOKIE_NAME_TOKEN);
        String paramToken = getCookieValue(request, UserKey.COOKIE_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken:paramToken;
        TbUser tbUser = tbUserService.getByToken(response, token);
        return tbUser;
    }

    private static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if(cookies !=null && cookies.length>0){
            for(Cookie cookie: cookies){
                if(cookie.getName().equals(cookieName)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
