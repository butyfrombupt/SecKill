package com.qihang.secKill.exception;

import com.qihang.secKill.result.CodeMsg;

/**
 * Created by wsbty on 2019/6/17.
 */
public class GlobleException extends RuntimeException {

    private static final long serialVersionUID = 1l;

    private CodeMsg codeMsg;

    public GlobleException(CodeMsg codeMsg){
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }

    public void setCodeMsg(CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
    }
}
