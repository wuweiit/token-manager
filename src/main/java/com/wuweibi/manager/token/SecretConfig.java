package com.wuweibi.manager.token;


import com.wuweibi.manager.token.constant.ConfigType;
import lombok.Data;

import java.time.Duration;

/**
 * 三方的配置
 */
@Data
public class SecretConfig {

    /**
     * 默认偏移时间量 秒
     */
    private static final Duration DEFAULT_OFFSET_TIME = Duration.ofSeconds(120);

    /**
     * 默认的Token生命周期7天
     */
    private static final Duration DEFAULT_LIFECYCLE_TIME = Duration.ofSeconds(604800);



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
    private String appSecret;

    /**
     * 配置Token类型 例如：WEIXIN
     */
    private String type;

    /**
     * 是否启用Token缓存
     */
    private boolean cacheEnable = false;

    /**
     * 启用刷新Token
     */
    private boolean enableRefreshToken = false;

    /**
     * 多环境 支持 默认 false
     */
    private boolean multiEnv = false;

    /**
     * 偏移时间 默认120秒 单位秒
     * 用于防止Token过期问题
     */
    private Duration offsetTime = DEFAULT_OFFSET_TIME;

    /**
     * 三方提供的Token生命周期 单位：秒 ，默认7天
     * 为了使用生命周期的Cache，必须配置的项
     */
    private Duration lifecycleTime = DEFAULT_LIFECYCLE_TIME;


    /**
     * 获取程序记录Token的生命周期
     * @return
     */
    public long getLifecycleTimeOffset() {
        return lifecycleTime.plusSeconds( - offsetTime.getSeconds()).getSeconds();
    }
}
