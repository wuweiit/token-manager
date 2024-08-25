package com.wuweibi.manager.token.config.properties;

import com.wuweibi.manager.token.SecretConfig;
import lombok.Data;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.wuweibi.manager.token.config.properties.TokenManagerProperties.PREFIX;

/**
 * 配置更新后自动刷新到配置对象
 * <p>
 * @author marker
 * Created by marker on 2021/07/12.
 */
@Data
@ConfigurationProperties(prefix = PREFIX)
public class TokenManagerProperties {

    /**
     * 配置前缀
     */
    public static final String PREFIX = "spring.token-manager";

    /**
     * redis服务器IP地址
     */
    private String host = "localhost";

    /**
     * Redis 密码
     */
    private String password;

    /**
     * 数据库
     */
    private int database = 14;

    /**
     * Redis端口
     */
    private int port = 3306;


    private String codec = "org.redisson.codec.JsonJacksonCodec";

    /**
     * 连接池配置
     */
    private RedisProperties.Lettuce lettuce = new RedisProperties.Lettuce();


    /**
     * 配置Map
     */
    private Map<String, SecretConfig> configMap = new LinkedHashMap();


    /**
     * 构造
     */
    public TokenManagerProperties() {

    }

}
