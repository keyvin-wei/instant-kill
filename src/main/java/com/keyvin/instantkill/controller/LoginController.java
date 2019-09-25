package com.keyvin.instantkill.controller;

import com.keyvin.instantkill.domain.TbUser;
import com.keyvin.instantkill.redis.RedisService;
import com.keyvin.instantkill.service.TbUserService;
import com.keyvin.instantkill.util.CodeMsg;
import com.keyvin.instantkill.util.MD5Util;
import com.keyvin.instantkill.util.Result;
import com.keyvin.instantkill.util.ValidatorUtil;
import com.keyvin.instantkill.vo.GoodsVo;
import com.keyvin.instantkill.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;

/**
 * @author weiwh
 * @date 2019/8/12 10:20
 */
@Controller
public class LoginController {
    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private RedisService redisService;
    @Autowired
    private TbUserService tbUserService;

    @RequestMapping("/login")
    public String login(){
        return "login";
    }

    @RequestMapping("/doLogin")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletRequest request,
                                   HttpServletResponse response, @Valid LoginVo loginVo){
        log.info(loginVo.toString());
        //参数校验，由全局拦截实现了，注解
        // if(StringUtils.isEmpty(password)){
        //     return Result.error(CodeMsg.PASSWORD_EMPTY);
        // }

        //登录
        tbUserService.login(response, loginVo);

        //跨域
        // 允许跨域访问的域名：若有端口需写全（协议+域名+端口），若没有端口末尾不用加'/'
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        // 允许前端带认证cookie：启用此项后，上面的域名不能为'*'，必须指定具体的域名，否则浏览器会提示
        response.setHeader("Access-Control-Allow-Credentials", "true");

        return Result.success(true);
    }

    /**
     * 压测测试：QPS：966
     */
    @ResponseBody
    @RequestMapping("/test-info")
    public Result<TbUser> list(Model model, TbUser tbUser){
        System.out.println(tbUser.getUserId());
        return Result.success(tbUser);
    }

    @ResponseBody
    @RequestMapping("/add-user")
    public String addUser(){
        System.out.println("add-user");
        for(int i=1; i<=1000; i++){
            TbUser user = new TbUser();
            user.setUserId(15500000000L + i);
            user.setNickname("keyvin" + i);
            user.setPassword("b7797cce01b4b131b433b6acf4add449");
            user.setSalt("1a2b3c4d");
            user.setHead("/head.jpg");
            user.setRegisterDate(new Date());
            user.setLastLoginDate(new Date());
            tbUserService.insert(user);
            log.info("添加用户成功：" + "keyvin" + i);
        }
        return "add success";
    }

    @ResponseBody
    @RequestMapping("/gener-token")
    public String generToken(HttpServletResponse response){
        try {
            File file = new File("C:\\java\\apache-jmeter-3.3\\计划\\token.txt");
            if(file.exists())file.delete();
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            file.createNewFile();
            raf.seek(0);
            List<TbUser> users = tbUserService.getAllUsers(1, 1000);
            System.out.println("generToken:" + users.size());
            for (TbUser user: users){
                LoginVo vo = new LoginVo();
                vo.setUserId("" + user.getUserId());
                vo.setPassword(MD5Util.inputPassToFromPass("123456"));
                String token = tbUserService.login(response, vo);
                raf.writeBytes(vo.getUserId());
                raf.writeBytes(",");
                raf.writeBytes(token);
                raf.writeBytes("\n");
                System.out.println("写入成功："+vo.getUserId());
            }
            raf.close();
        }catch (Exception e){
            e.printStackTrace();
            return "gener-token server error";
        }
        return "gener-token success";
    }

    @ResponseBody
    @RequestMapping("/gener-token8")
    public String generToken5(HttpServletResponse response){
        try {
            File file = new File("C:\\java\\apache-jmeter-3.3\\计划\\token8.txt");
            if(file.exists())file.delete();
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            file.createNewFile();
            raf.seek(0);
            List<TbUser> users = tbUserService.getAllUsers(1, 8);
            System.out.println("generToken:" + users.size());
            for (TbUser user: users){
                LoginVo vo = new LoginVo();
                vo.setUserId("" + user.getUserId());
                vo.setPassword(MD5Util.inputPassToFromPass("123456"));
                String token = tbUserService.login(response, vo);
                raf.writeBytes(vo.getUserId());
                raf.writeBytes(",");
                raf.writeBytes(token);
                raf.writeBytes("\n");
                System.out.println("写入成功："+vo.getUserId());
            }
            raf.close();
        }catch (Exception e){
            e.printStackTrace();
            return "gener-token server error";
        }
        return "gener-token success";
    }

}
