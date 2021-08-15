package com.wuweiit.manager.token.listener;


import com.wuweiit.manager.token.TokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class RedisTokenRefreshListener implements MessageListener {


    /**
     * 应用上下文对象
     */
    private ApplicationContext applicationContext;


    /**
     * 构造
     * @param applicationContext spring上下文
     */
    public RedisTokenRefreshListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 监听到指定数据库的数据的回调方法
     *
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("监听指定数据库的数据-{}" ,new String(message.getBody(), StandardCharsets.UTF_8));
        String expiredKey = message.toString();

        if (expiredKey.startsWith("manager:refresh")) { // Token 快过期了，需要刷新一下
            String[] keys = expiredKey.split(":");
            String type = keys[2];
            String identify = keys[3];

            List<TokenManager> tokenManagers = getTokenManagers();
            Optional<TokenManager> optional = tokenManagers.stream().filter(item -> item.getType().equals(type)).findFirst();
            if (optional.isPresent()) {
                optional.get().refreshToken(identify);
            }
        }


        log.info("message>>>  {}" , message);
        log.info("pattern>>>  {}" , new String(pattern));
        log.info("Redis的键：{}" , expiredKey);

    }


    /**
     * 获取Spring容器内部所有的TokenManager实例
     * @return
     */
    private List<TokenManager> getTokenManagers() {
        Map<String, TokenManager> tokenManagers = applicationContext.getBeansOfType(TokenManager.class);
        return tokenManagers.entrySet().stream().map(item->item.getValue()).collect(Collectors.toList());
    }

}
