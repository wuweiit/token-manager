package com.wuweibi.manager.token.autoconfigure;


import com.wuweibi.manager.token.config.properties.TokenManagerProperties;
import com.wuweibi.manager.token.listener.RedisTokenRefreshListener;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import javax.annotation.Resource;


/**
 * 自动装配TokenManager
 *
 * @author marker
 */
@Configuration
@ConditionalOnProperty(prefix = TokenManagerProperties.PREFIX, value = "enabled", havingValue = "true")
public class TokenManagerRedisAutoConfiguration {


    @Resource
    private TokenManagerProperties tokenManagerProperties;

    @Resource
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnProperty(prefix = TokenManagerProperties.PREFIX, value = "autoRefresh", havingValue = "true")
    public RedisMessageListenerContainer newRedisMessageListenerContainer() {
        int db = tokenManagerProperties.getDatabase();
        RedisMessageListenerContainer listenerContainer = new RedisMessageListenerContainer();
        listenerContainer.setConnectionFactory(tokenManagerRedisConnectionFactory());
        //监听指定dbindex的事件
        String topic = String.format("__keyevent@%d__:expired", db);
        listenerContainer.addMessageListener(new RedisTokenRefreshListener(applicationContext), new PatternTopic(topic));
        return listenerContainer;
    }


    /**
     * Redis链接工厂
     *
     * @return
     */
    @Bean(name = "tokenManagerRedisConnectionFactory")
    public RedisConnectionFactory tokenManagerRedisConnectionFactory() {
        return createRedisConnectionFactory(tokenManagerProperties.getDatabase());
    }


    /**
     * 创建redis连接工厂
     */
    public LettuceConnectionFactory createRedisConnectionFactory(int dbIndex) {
        String host = tokenManagerProperties.getHost();
        int port = tokenManagerProperties.getPort();
        ClientOptions clientOptions = ClientOptions.builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .autoReconnect(true)

                .socketOptions(SocketOptions.builder().keepAlive(true).build())
                .build();

        RedisProperties.Lettuce lettuce = tokenManagerProperties.getLettuce();


        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(lettuce.getPool().getMaxIdle());
        genericObjectPoolConfig.setMinIdle(lettuce.getPool().getMinIdle());
        genericObjectPoolConfig.setMaxTotal(lettuce.getPool().getMaxActive());
        genericObjectPoolConfig.setMaxWaitMillis(lettuce.getPool().getMaxWait().toMillis());
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(lettuce.getShutdownTimeout().toMillis());

        LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .clientOptions(clientOptions)
                .poolConfig(genericObjectPoolConfig)
                .build();

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        redisStandaloneConfiguration.setDatabase(dbIndex);
        redisStandaloneConfiguration.setPassword(tokenManagerProperties.getPassword());
        return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
    }


}
