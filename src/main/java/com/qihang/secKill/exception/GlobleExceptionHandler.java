package com.qihang.secKill.exception;

import com.qihang.secKill.result.CodeMsg;
import com.qihang.secKill.result.Result;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by wsbty on 2019/6/17.
 */
@ControllerAdvice
@ResponseBody
public class GlobleExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request,Exception e){
        e.printStackTrace();
        if(e instanceof GlobleException){
            GlobleException ex = (GlobleException) e;
            return Result.error(ex.getCodeMsg());
        }
        else if(e instanceof BindException){
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        }
        else {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
