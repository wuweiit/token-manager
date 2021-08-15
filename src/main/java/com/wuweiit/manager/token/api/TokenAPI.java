package com.wuweiit.manager.token.api;

import com.wuweiit.manager.token.SecretConfig;
import com.wuweiit.manager.token.bean.TokenInfo;

import java.util.Map;


/**
 * ttokenapi通用接口
 *
 */
public interface TokenAPI {


    /**
     * 获取Token的通用接口
     * @param secretConfig
     * @param params
     * @return
     */
    TokenInfo getToken(SecretConfig secretConfig, Map<String, Object> params);

    /**
     * 刷新Token
     * @param secretConfig
     * @param params
     * @return
     */
    default TokenInfo refreshToken(SecretConfig secretConfig, Map<String, Object> params){return null;}


}
