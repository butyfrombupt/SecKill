package com.qihang.secKill.validator;


import com.qihang.secKill.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by wsbty on 2019/6/17.
 */
public class IsMobileValidator implements ConstraintValidator<isMobile,String>{

    private boolean required = false;

    public void initialize(isMobile isMobile) {
        required = isMobile.required();
    }

    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            return ValidatorUtil.isMobile(s);
        }
        else{
            if(StringUtils.isEmpty(s)){
                return true;
            }
            else{
                return ValidatorUtil.isMobile(s);
            }
        }
    }
}
