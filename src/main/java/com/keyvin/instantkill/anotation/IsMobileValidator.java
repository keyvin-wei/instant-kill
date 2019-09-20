package com.keyvin.instantkill.anotation;

import com.keyvin.instantkill.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 校验
 * @author weiwh
 * @date 2019/8/13 10:49
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    private boolean required = false;//是否可为空

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        //初始化方法，拿到注解
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        //s为实际的值
        if (required){
            return ValidatorUtil.isMobile(s);
        }else {
            if (StringUtils.isEmpty(s)){
                return true;
            }else {
                return ValidatorUtil.isMobile(s);
            }
        }
    }
}
