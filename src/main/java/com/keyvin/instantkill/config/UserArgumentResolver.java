package com.keyvin.instantkill.config;

import com.keyvin.instantkill.domain.TbUser;
import com.keyvin.instantkill.redis.RedisService;
import com.keyvin.instantkill.redis.UserKey;
import com.keyvin.instantkill.service.TbUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author weiwh
 * @date 2019/8/14 0:39
 */
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private TbUserService tbUserService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == TbUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest webRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        //进方法之前参数校验
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        String cookieToken = request.getParameter(UserKey.COOKIE_NAME_TOKEN);
        String paramToken = getCookieValue(request, UserKey.COOKIE_NAME_TOKEN);
        if(StringUtils.isEmpty(cookieToken)&&StringUtils.isEmpty(paramToken)){
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken:paramToken;
        TbUser tbUser = tbUserService.getByToken(response, token);
        return tbUser;
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
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
