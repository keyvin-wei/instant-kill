package com.keyvin.instantkill.util;

/**
 * @author weiwh
 * @date 2019/8/11 11:09
 */
public class CodeMsg {
    private int code;
    private String msg;

    //通用异常
    public static CodeMsg SUCCESS = new CodeMsg(200, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常！");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    public static CodeMsg SESSION_ERROR = new CodeMsg(500102, "登录有效期异常！");
    public static CodeMsg REQUEST_PATH_ILLEGAL = new CodeMsg(500103, "请求路径非法！");
    public static CodeMsg VERIFY_CODE_ERROR = new CodeMsg(500104, "验证码错误！");
    public static CodeMsg ACCESS_LIMIT = new CodeMsg(500105, "访问太频繁！");

    //登录异常 500200
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500200, "密码不能为空！");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500201, "手机号不能为空！");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500202, "手机号格式错误！");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500203, "手机号不存在！");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500204, "密码错误！");

    //商品模块
    public static CodeMsg INVENTORY_SHORTAGE = new CodeMsg(500301, "商品库存不足！");
    public static CodeMsg BUYOUT_REPEAT = new CodeMsg(500302, "不能重复秒杀下单！");
    public static CodeMsg ORDER_NOT_FOUND = new CodeMsg(500303, "订单不存在！");
    public static CodeMsg BUYOUT_FAIL = new CodeMsg(5003034, "秒杀失败！");

    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object...args){
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }
}
