package com.wuweiit.manager.token.lock;

/**
 * Created by marker on 2018/7/23.
 */


import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 分布式锁
 *
 * 支持Redis 和 Zookeeper
 *
 *
 * @author marker
 * @create 2018-07-23 15:32
 **/
public interface LockHandler {



    /**
     * 获取锁 如果锁可用 立即返回true， 否则返回false，不等待
     *
     * @param key 锁
     * @return
     */
    boolean tryLock(String key);



    /**
     * 锁在给定的等待时间内空闲，则获取锁成功 返回true， 否则返回false
     *
     * @param key     锁键
     * @param timeout 超时时间
     * @param unit    超时时间单位
     * @return
     */
    boolean tryLock(String key, long timeout, TimeUnit unit);



    /**
     * 如果锁空闲立即返回 获取失败 一直等待
     * @param key 锁
     */
    void lock(String key);



    /**
     * 释放锁
     * @param key 锁
     */
    void unLock(String key);



    /**
     * 批量获取锁 如果全部获取 立即返回true, 部分获取失败 返回false
     * @param keyList 锁集合
     * @return
     */
    boolean tryLock(List<String> keyList);



    /**
     * 锁在给定的等待时间内空闲，则获取锁成功 返回true， 否则返回false
     * @param keyList 锁集合
     * @param timeout 超时时间
     * @param unit 超时单位
     * @return
     */
    boolean tryLock(List<String> keyList, long timeout, TimeUnit unit);



    /**
     * 批量释放锁
     */
    void unLock(List<String> keyList);
}
