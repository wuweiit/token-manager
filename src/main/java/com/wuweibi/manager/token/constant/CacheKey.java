package com.wuweibi.manager.token.constant;


/**
 * CacheKey,存储到Redis中的key
 *
 * @author marker
 */
public interface CacheKey {


    /**
     * Token key
     */
    String TOKEN = "manager:token%s:%s:%s";


    /**
     * 刷新TokenKey
     */
    String REFRESH_TOKEN = "manager:refresh%s:%s:%s";


    /**
     * 针对应用的锁key
     */
    String MANAGER_LOCK = "manager:lock%s:%s";
}
