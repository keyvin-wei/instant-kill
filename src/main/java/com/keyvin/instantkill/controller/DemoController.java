package com.keyvin.instantkill.controller;

import com.keyvin.instantkill.domain.User;
import com.keyvin.instantkill.redis.RedisService;
import com.keyvin.instantkill.redis.UserKey;
import com.keyvin.instantkill.service.UserService;
import com.keyvin.instantkill.util.CodeMsg;
import com.keyvin.instantkill.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author weiwh
 * @date 2019/8/11 11:00
 */
@Controller
@RequestMapping("/test")
public class DemoController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    @ResponseBody
    @RequestMapping("/hello")
    public String demoHome(){
        return "hello 秒杀系统";
    }

    @ResponseBody
    @RequestMapping("/success")
    public Result<String> hello(){
        return Result.success("hello keyvin");
    }

    @ResponseBody
    @RequestMapping("/error")
    public Result<String> error(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name", "keyvin-韦文汉");
        return "test";
    }

    @ResponseBody
    @RequestMapping("/db-get")
    public Result<User> dbGet(){
        User user = userService.getById(2);
        return Result.success(user);
    }

    @ResponseBody
    @RequestMapping("/db-tx")
    public boolean dbTx(){
        userService.tx();
        return true;
    }

    @ResponseBody
    @RequestMapping("/redis-get")
    public Result<User> redisGet(){
        User v1 = redisService.get(UserKey.getById, "uk", User.class);
        redisService.incr(UserKey.getById, "jk");
        return Result.success(v1);
    }

    @ResponseBody
    @RequestMapping("/redis-set")
    public Result<Boolean> redisSet(){
        User user = new User(1,"1111");
        redisService.set(UserKey.getById, "uk", user);
        return Result.success(true);
    }

}
