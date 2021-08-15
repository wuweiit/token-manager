package com.wuweiit.manager.token;


import com.alibaba.fastjson.JSON;
import com.wuweiit.manager.token.api.TokenAPI;
import com.wuweiit.manager.token.api.waibao.WaibaoTokenAPI;
import com.wuweiit.manager.token.api.weixin.WeixinMPAPI;
import com.wuweiit.manager.token.bean.TokenInfo;
import com.wuweiit.manager.token.constant.CacheKey;
import com.wuweiit.manager.token.constant.ConfigType;
import com.wuweiit.manager.token.lock.LockHandler;
import com.wuweiit.manager.token.lock.impl.RedisLockHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 三方 Token管理器
 *
 * @author marker
 */
@Slf4j
@Data
public class TokenManager {


    /**
     * 配置信息
     */
    private SecretConfig secretConfig;


    /**
     * TokenAPI
     */
    private TokenAPI tokenAPI;


    /**
     * RedisConnectionFactory
     */
    private RedisConnectionFactory redisConnectionFactory;


    /**
     * 分布式锁
     */
    private LockHandler lockHandler;


    private TokenManager( ){

    }



    /**
     * 创建TokenManager
     *
     * @param secretConfig
     * @param redisConnectionFactory
     */
    public TokenManager(@Autowired @Qualifier("tokenManagerRestTemplate") RestTemplate restTemplate,
                        SecretConfig secretConfig, RedisConnectionFactory redisConnectionFactory) {
        this.secretConfig = secretConfig;
        this.redisConnectionFactory = redisConnectionFactory;
        this.lockHandler = new RedisLockHandler(redisConnectionFactory);

        // 选择API实现
        if (ConfigType.DEMO.equals(secretConfig.getType())) {
            tokenAPI = new WaibaoTokenAPI(restTemplate) ;
        } else if (ConfigType.WEIXIN.equals(secretConfig.getType())) {
            tokenAPI = new WeixinMPAPI(restTemplate) ;
        }

    }



    public TokenInfo getToken() {
        return this.getToken("");
    }

    /**
     * 获取Token的简化
     *
     * @param params
     * @return
     */
    public TokenInfo getToken(String params) {
        Map<String, Object> paramsMap = new HashMap<>(2);
        paramsMap.put("params", params);
        paramsMap.put("tag", params);
        return this.getToken(paramsMap);
    }


    /**
     * 目前还 存在线程安全问题
     *
     * @param params
     * @return
     */
    public TokenInfo getToken(Map<String, Object> params) {
        String identify = (String) params.get("tag");
        if (identify == null) {
            throw new RuntimeException("token cache tag not null");
        }
        if (secretConfig.getAppid() != null) {
            identify = secretConfig.getAppid() + (("".equals(identify)) ? "" : "" + identify);
        }
        String key = String.format(CacheKey.TOKEN, secretConfig.getType().name(), identify);
        String key2 = String.format(CacheKey.REFRESH_TOKEN, secretConfig.getType().name(), identify); // token过期key
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] key2Bytes = key2.getBytes(StandardCharsets.UTF_8);

        RedisConnection redisConnection = redisConnectionFactory.getConnection();

        // 先取一次数据，如果有就返回
        if (secretConfig.isCacheEnable()) {
            TokenInfo tokenInfo = getTokenInfo(keyBytes, redisConnection);
            if (tokenInfo != null) return tokenInfo;
        }

        // 增加锁控制并发
        String lockey = String.format(CacheKey.MANAGER_LOCK, identify);
        boolean status = lockHandler.tryLock(lockey, 10, TimeUnit.SECONDS);
        if (!status) {
            throw new RuntimeException("服务器繁忙");
        }

        try {
            // 因为前面的请求可能会等待情况，所以需要再次检查是否有缓存
            if (secretConfig.isCacheEnable()) {
                TokenInfo tokenInfo = getTokenInfo(keyBytes, redisConnection);
                if (tokenInfo != null) return tokenInfo;
            }

            // 确实没有缓存的情况才调用
            TokenInfo tokenInfo = tokenAPI.getToken(secretConfig, params);
            if (secretConfig.isCacheEnable()) {
                // 预检查
                if (tokenInfo.getExpiresIn() <= secretConfig.getOffsetTime()) {
                    throw new RuntimeException("Token生命周期小于偏移时间");
                }
                // 存储Redis 并记录生命周期
                byte[] bytes = JSON.toJSONString(tokenInfo).getBytes(StandardCharsets.UTF_8);
                redisConnection.set(keyBytes, bytes);
                redisConnection.expire(keyBytes, tokenInfo.getExpiresIn() - secretConfig.getOffsetTime());

                // 刷新Token的机制key 快60秒销毁，销毁会被监听并刷新Token
                redisConnection.set(key2Bytes, bytes);
                redisConnection.expire(key2Bytes, tokenInfo.getExpiresIn() - secretConfig.getOffsetTime() - 60);
            }
            return tokenInfo;
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        } finally {
            lockHandler.unLock(lockey);
            redisConnection.close();
        }

    }


    /**
     * 获取缓存中的TokenInfo数据
     *
     * @param keyBytes
     * @param redisConnection
     * @return
     */
    private TokenInfo getTokenInfo(byte[] keyBytes, RedisConnection redisConnection) {
        byte[] bytes = redisConnection.get(keyBytes);
        if (bytes != null) {
            TokenInfo tokenInfo = JSON.parseObject(new String(bytes), TokenInfo.class);
            long expires = redisConnection.ttl(keyBytes, TimeUnit.SECONDS);
            tokenInfo.setExpiresIn(expires);
            return tokenInfo;
        }
        return null;
    }


    /**
     * 设置TokenAPi实现
     * 这里是在需要自己实现TokenAPI时使用
     * @param tokenAPI
     */
    public void setTokenAPI(TokenAPI tokenAPI) {
        if(this.tokenAPI != null){
            log.warn("TokenManager has tokenAPi impl！！！use custom tokenAPI.");
        }
        this.tokenAPI = tokenAPI;
    }


    public String getType() {
        return this.secretConfig.getType().name();
    }


    /**
     * 更新Token
     * 暂时未完全实现自动刷新
     * @param identify
     */
    public void refreshToken(String identify) {

        if(!secretConfig.isEnableRefreshToken()){
            log.info("updateToken() {} not open RefreshToken",this.secretConfig.getType());
            return;
        }
//        this.getToken(identify);
    }


    /**
     * 清理Token，
     * 如果是单个认证信息的可使用
     */
    public void cleanToken() {
        cleanToken(this.secretConfig.getAppid());
    }

    /**
     * 多认证token 清除Token
     * @param identify
     */
    public void cleanToken(String identify) {
        String key = String.format(CacheKey.TOKEN, secretConfig.getType().name(), identify);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        RedisConnection redisConnection = redisConnectionFactory.getConnection();


        // 增加锁控制并发
        String lockey = String.format(CacheKey.MANAGER_LOCK, identify);
        boolean status = lockHandler.tryLock(lockey, 10, TimeUnit.SECONDS);
        if (!status) {
            throw new RuntimeException("服务器繁忙");
        }

        try {
            // 清理redis
            redisConnection.del(keyBytes);
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e.getMessage());
        } finally {
            lockHandler.unLock(lockey);
            redisConnection.close();
        }

    }
}
