package com.wuweibi.manager.token;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wuweibi.manager.token.bean.TokenInfo;
import com.wuweibi.manager.token.config.properties.TokenManagerProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TokenManagerTest {

    @Resource
    private TokenManagerProperties tokenManagerProperties;


    /**
     * redis链接工厂
     */
    @Resource(name = "tokenManagerRedisConnectionFactory")
    private RedisConnectionFactory redisConnectionFactory;

    /**
     * tokenManagerRestTemplate
     */
    @Resource(name = "tokenManagerRestTemplate")
    private RestTemplate restTemplate;

    @Resource(name = "weixinMpTokenManager")
    private TokenManager tokenManager;


    @Test
    public void test1(){

//        Map<String, SecretConfig> configMap = tokenManagerProperties.getConfigMap();
//        SecretConfig secretConfig = configMap.get("weixinMp");
//        TokenManager tokenManager = okenManager(secretConfig, restTemplate, redisConnectionFactory);


        TokenInfo token = tokenManager.getToken("");



        TokenInfo token1 = tokenManager.getToken("");

//        boolean status = tokenManager.check(token.getAccessToken());

//        tokenManager.cleanToken("");

    }
}

