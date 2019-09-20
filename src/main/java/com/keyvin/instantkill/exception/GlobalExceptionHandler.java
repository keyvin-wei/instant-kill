package com.keyvin.instantkill.exception;

import com.keyvin.instantkill.util.CodeMsg;
import com.keyvin.instantkill.util.Result;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常拦截
 * @author weiwh
 * @date 2019/8/13 11:10
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)//拦截所有异常，Exception
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e){
        e.printStackTrace();//打印信息
        if(e instanceof GlobalException){
            GlobalException ex = (GlobalException)e;
            return Result.error(ex.getCm());
        }else if (e instanceof BindException){
            BindException be = (BindException) e;
            List<ObjectError> errors = be.getAllErrors();
            ObjectError error = errors.get(0);
            String msg=error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
