package com.wuweiit.manager.token;


import com.wuweiit.manager.token.constant.ConfigType;
import lombok.Data;

/**
 * 三方的配置
 */
@Data
public class SecretConfig {

    /**
     * 调用接口域名地址
     */
    private String apiHost = "";

    /**
     * 三方的APPID（clientID appkey等）
     */
    private String appid;

    /**
     * 秘钥
     */
    private String appsecret;

    /**
     * 配置Token类型
     */
    private ConfigType type;

    /**
     * 是否启用Token缓存
     */
    private boolean cacheEnable = false;

    /**
     * 启用刷新Token
     */
    private boolean enableRefreshToken = false;

    /**
     * 偏移时间 默认1天 单位秒
     * 用于防止Token过期问题
     */
    private long offsetTime = 60 * 60 * 24 + 180;
}
