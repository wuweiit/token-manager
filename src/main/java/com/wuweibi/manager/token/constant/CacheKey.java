package com.wuweibi.manager.token.constant;


/**
 * CacheKey
 *
 * @author marker
 */
public interface CacheKey {
    String TOKEN = "manager:token:%s:%s";
    String REFRESH_TOKEN = "manager:refresh:%s:%s";
    String MANAGER_LOCK = "manager:lock:%s";
}
