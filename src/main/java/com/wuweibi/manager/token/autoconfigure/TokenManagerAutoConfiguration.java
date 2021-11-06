package com.wuweibi.manager.token.autoconfigure;


import com.wuweibi.manager.token.SecretConfig;
import com.wuweibi.manager.token.TokenManager;
import com.wuweibi.manager.token.config.properties.TokenManagerProperties;
import com.wuweibi.manager.token.handler.LogClientHttpRequestInterceptor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * TokenManager 自动装配配置类
 *
 * @author marker
 */
@Configuration
@ConditionalOnProperty(prefix = TokenManagerProperties.PREFIX, value = "enabled", havingValue = "true")
@Import(TokenManagerRedisAutoConfiguration.class)
@EnableConfigurationProperties(TokenManagerProperties.class)
public class TokenManagerAutoConfiguration {


    @Resource
    private TokenManagerProperties tokenManagerProperties;

    /**
     * redis链接工厂
     */
    @Resource(name = "tokenManagerRedisConnectionFactory")
    private RedisConnectionFactory redisConnectionFactory;


    /**
     * 这个方法返回Runnable只是一个幌子，最重要的是执行方法里面的代码
     * @param applicationContext applicationContext
     * @return Runnable
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public Runnable dynamicConfiguration(ApplicationContext applicationContext) throws Exception {
        ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) context.getBeanFactory();


        Map<String, SecretConfig> configMap = tokenManagerProperties.getConfigMap();

        for (Map.Entry<String, SecretConfig> config : configMap.entrySet()) {
            String key = config.getKey();

            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(TokenManager.class);

//            beanDefinitionBuilder.addDependsOn("tokenManagerRestTemplate");
//            beanDefinitionBuilder.addConstructorArgValue(restTemplate());
//            beanDefinitionBuilder.addConstructorArgReference("tokenManagerRestTemplate");
            beanDefinitionBuilder.addConstructorArgValue(restTemplate());
            beanDefinitionBuilder.addConstructorArgValue(config.getValue());
            beanDefinitionBuilder.addConstructorArgValue(redisConnectionFactory);
            // 设置属性
            beanDefinitionBuilder.addPropertyValue("secretConfig", config.getValue());
            beanDefinitionBuilder.addPropertyValue("redisConnectionFactory", redisConnectionFactory);

            // 注册到spring容器中
            String beanName = String.format("%sTokenManager", key);
            beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());

        }
        return null;
    }


    /**
     * 外包 Token管理器 自动装配
     *
     * @return RestTemplate
     */
    @Bean("tokenManagerRestTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LogClientHttpRequestInterceptor());

        restTemplate.setInterceptors(interceptors);

        return restTemplate;
    }



}
