package com.wuweibi.manager.token.exception;

/**
 * 获取Token失败异常
 *
 * @author marker
 */
public class GetTokenException extends RuntimeException {

    public GetTokenException(String errmsg) {
        super("getToken faild reason:" + errmsg);
    }
}
