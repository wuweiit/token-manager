package com.wuweibi.manager.token.exception;

/**
 * 获取Token失败异常
 *
 * @author marker
 */
public class GetTokenException extends RuntimeException {


    /**
     * 构造
     * @param message 错误信息
     */
    public GetTokenException(String message) {
        super("TokenManager getToken failed reason:" + message);
    }

    /**
     * 构造2
     * @param message 消息内容
     * @param cause 原因
     */
    public GetTokenException(String message, Throwable cause) {
        super("TokenManager getToken failed reason:" + message, cause);
    }
}
