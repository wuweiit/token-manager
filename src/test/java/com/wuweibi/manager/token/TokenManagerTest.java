package com.wuweibi.manager.token;

import com.wuweibi.manager.token.bean.TokenInfo;
import com.wuweibi.manager.token.config.properties.TokenManagerProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

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

    @Resource(name = "weixinTokenManager")
    private TokenManager weixinTokenManager;


    @Test
    public void test1(){
        TokenInfo token = weixinTokenManager.getToken("");
        Assert.notNull(token, "Token is null");
        weixinTokenManager.cleanToken("");
    }
    @Test
    public void test2(){
        Map<String, SecretConfig> configMap = tokenManagerProperties.getConfigMap();
        SecretConfig secretConfig = configMap.get("weixin");
        TokenManager tokenManager = new TokenManager(restTemplate, secretConfig, redisConnectionFactory);


        TokenInfo token = tokenManager.getToken("");
        Assert.notNull(token, "Token is null");
        tokenManager.cleanToken("");
    }
}

