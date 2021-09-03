package com.wuweibi.manager.token;


import com.wuweibi.manager.token.constant.ConfigType;
import lombok.Data;

import java.time.Duration;

/**
 * 三方的配置
 */
@Data
public class SecretConfig {

    private static final long DEFAULT_OFFSET_TIME = 60 * 60 * 24 + 180;
    private static final long DEFAULT_LIFECYCLE_TIME = 604800;

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
    private Duration offsetTime =  Duration.ofSeconds(DEFAULT_OFFSET_TIME);

    /**
     * 三方提供的Token生命周期 单位：秒 ，默认7天
     * 为了使用生命周期的Cache，必须配置的项
     */
    private Duration lifecycleTime = Duration.ofSeconds(DEFAULT_LIFECYCLE_TIME);


    /**
     * 获取程序记录Token的生命周期
     * @return
     */
    public long getLifecycleTimeOffset() {
        return lifecycleTime.plusSeconds( - offsetTime.getSeconds()).getSeconds();
    }
}
