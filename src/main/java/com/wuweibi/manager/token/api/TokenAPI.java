package com.wuweibi.manager.token.api;

import com.wuweibi.manager.token.SecretConfig;
import com.wuweibi.manager.token.bean.TokenInfo;

import java.util.Map;


/**
 * ttokenapi通用接口
 *
 */
public interface TokenAPI {


    /**
     * 获取Token的通用接口
     * @param secretConfig 配置信息
     * @param params 参数
     * @return
     */
    TokenInfo getToken(SecretConfig secretConfig, Map<String, Object> params);

    /**
     * 刷新Token
     * @param secretConfig 配置信息
     * @param params 参数
     * @return
     */
    default TokenInfo refreshToken(SecretConfig secretConfig, Map<String, Object> params){return null;}


}
