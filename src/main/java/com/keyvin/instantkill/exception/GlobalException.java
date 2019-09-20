package com.keyvin.instantkill.exception;

import com.keyvin.instantkill.util.CodeMsg;

/**
 * 全局业务异常
 * @author weiwh
 * @date 2019/8/13 11:20
 */
public class GlobalException extends RuntimeException {
    public CodeMsg cm;

    public GlobalException(CodeMsg cm){
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }

    public void setCm(CodeMsg cm) {
        this.cm = cm;
    }
}
