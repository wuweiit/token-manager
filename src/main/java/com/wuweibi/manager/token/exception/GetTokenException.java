package com.wuweibi.manager.token.exception;

/**
 * 获取Token失败异常
 *
 * @author marker
 */
public class GetTokenException extends RuntimeException {


    /**
     * 构造
     * @param msg 错误信息
     */
    public GetTokenException(String msg) {
        super("getToken faild reason:" + msg);
    }
}
