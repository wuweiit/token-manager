package com.wuweiit.manager.token.listener;


import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@Slf4j
public class RedisTokenRefreshListener implements MessageListener {


    /**
     * 监听到指定数据库的数据的回调方法
     *
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
//        log.info("监听指定数据库的数据-{}" ,new String(message.getBody(), StandardCharsets.UTF_8));
//        String expiredKey = message.toString();
//
//
//        if (expiredKey.startsWith("manager:refresh")) { // Token 快过期了，需要刷新一下
//            String[] keys = expiredKey.split(":");
//            String type = keys[2];
//            String identify = keys[3];
//
//            List<TokenManager> tokenManagers = SpringUtil.getBeans(TokenManager.class);
//            Optional<TokenManager> optional = tokenManagers.stream().filter(item -> item.getType().equals(type)).findFirst();
//            if (optional.isPresent()) {
//                optional.get().refreshToken(identify);
//            }
//        }
//
//
//        log.info("message>>>  {}" , message);
//        log.info("pattern>>>  {}" , new String(pattern));
//        log.info("Redis的键：{}" , expiredKey);

    }

}
