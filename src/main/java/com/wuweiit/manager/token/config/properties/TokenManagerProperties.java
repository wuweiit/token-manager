package com.wuweiit.manager.token.config.properties;

import com.wuweiit.manager.token.SecretConfig;
import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.wuweiit.manager.token.config.properties.TokenManagerProperties.PREFIX;

/**
 * 配置更新后自动刷新到配置对象
 * <p>
 * Created by marker on 2021/07/12.
 */
@Data
@RefreshScope
@Configuration
@SpringBootConfiguration
@ConfigurationProperties(prefix = PREFIX)
public class TokenManagerProperties {

    public static final String PREFIX = "jiayu.token-manager";

    private String host = "localhost";
    private String password;
    private int database = 14;
    private int port = 3306;


    private String codec = "org.redisson.codec.JsonJacksonCodec";

    /**
     * 配置Map
     */
    private Map<String, SecretConfig> configMap;


    public TokenManagerProperties(){
        configMap  = new LinkedHashMap();
    }

}