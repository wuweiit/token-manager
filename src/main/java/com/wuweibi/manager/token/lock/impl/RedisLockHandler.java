package com.wuweibi.manager.token.lock.impl;


import com.wuweibi.manager.token.lock.LockHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * redis 分布式锁实现
 * （带有默认前缀）
 *
 * @author marker
 */
public class RedisLockHandler implements LockHandler {
    // 日志记录
    private static final Logger log = LoggerFactory.getLogger(RedisLockHandler.class);

    // 单个锁有效期（秒）
    private static final int DEFAULT_SINGLE_EXPIRE_TIME = 30;
    // 批量锁有效期（秒）
    private static final int DEFAULT_BATCH_EXPIRE_TIME = 60;

    // 构造时，注入
    private RedisConnectionFactory redisConnectionFactory;


    /**
     * 锁 Key 前缀
     */
    private String prefix = "lock:";


    /**
     * 构造
     *
     * @param redisConnectionFactory redis 链接工厂
     */
    public RedisLockHandler(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }


    /**
     * 获取锁 如果锁可用 立即返回true， 否则返回false，不等待
     *
     * @return boolean
     */
    public boolean tryLock(String key) {
        return tryLock(key, 0L, null);
    }

    /**
     * 锁在给定的等待时间内空闲，则获取锁成功 返回true， 否则返回false
     *
     * @param key     锁键
     * @param timeout 超时时间
     * @param unit    超时时间单位
     * @return boolean
     */
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        RedisConnection connection = redisConnectionFactory.getConnection();
        String lockKey = prefix.concat(key);
        try {
            //系统计时器的当前值，以毫微秒为单位。
            long nano = System.nanoTime();
            do {
                log.debug("try lock key: {}", lockKey);
                //将 key 的值设为 空 byte  成功 或者 失败 ()
                Boolean status = connection.setNX(lockKey.getBytes(), new byte[1]);
                if (status) {
                    connection.expire(lockKey.getBytes(), DEFAULT_SINGLE_EXPIRE_TIME);//设置过期时间
                    log.debug("get lock, key: {} , expire in {} seconds.", lockKey, DEFAULT_SINGLE_EXPIRE_TIME);
                    // 成功获取锁，返回true
                    return Boolean.TRUE;
                } else { // 存在锁,循环等待锁
                    log.debug("key: {} locked by another business：", lockKey);
                }
                if (timeout <= 0) {  // 没有设置超时时间，直接退出等待
                    break;
                }
                Thread.sleep(30);
            } while ((System.nanoTime() - nano) < unit.toNanos(timeout));
            return Boolean.FALSE;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            connection.close();//释放资源
        }
        return Boolean.FALSE;
    }

    /**
     * 如果锁空闲立即返回 获取失败 一直等待
     */
    public void lock(String key) {
        RedisConnection connection = redisConnectionFactory.getConnection();
        String lockKey = prefix.concat(key);
        try {
            do {
                log.debug("lock key: " + lockKey);
                Boolean status = connection.setNX(lockKey.getBytes(), lockKey.getBytes());
                if (status) {
                    connection.expire(lockKey.getBytes(), DEFAULT_SINGLE_EXPIRE_TIME);
                    log.debug("get lock, key: {} , expire in {} seconds.", lockKey, DEFAULT_SINGLE_EXPIRE_TIME);
                    return;
                } else {
                    log.debug("key: {} locked by another business：", lockKey);
                }
                Thread.sleep(300);
            } while (true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            connection.close();
        }
    }

    /**
     * 释放锁
     */
    public void unLock(String key) {
        if (null == key) {
            key = "null";
        }
        List<String> list = new ArrayList<>(1);
        list.add(key);
        unLock(list);
    }

    /**
     * 批量获取锁 如果全部获取 立即返回true, 部分获取失败 返回false
     *
     * @return boolean
     */
    public boolean tryLock(List<String> keyList) {
        return tryLock(keyList, 0L, null);
    }

    /**
     * 锁在给定的等待时间内空闲，则获取锁成功 返回true， 否则返回false
     *
     * @param timeout 超时时间
     * @param unit 单位
     * @return boolean
     */
    public boolean tryLock(List<String> keyList, long timeout, TimeUnit unit) {
        RedisConnection connnection = redisConnectionFactory.getConnection();
        try {
            //需要的锁
            List<String> needLocking = new CopyOnWriteArrayList<String>();
            //得到的锁
            List<byte[]> locked = new CopyOnWriteArrayList<>();

            long nano = System.nanoTime();
            do {
                // 构建pipeline，批量提交
                connnection.openPipeline();
                for (String key : keyList) {
                    String lockKey = prefix.concat(key);
                    needLocking.add(lockKey);
                    connnection.setNX(lockKey.getBytes(), lockKey.getBytes());
                }
                log.debug("try lock keys: " + needLocking);
                // 提交redis执行计数,批量处理完成返回
                List<Object> results = connnection.closePipeline();
                for (int i = 0; i < results.size(); ++i) {
                    Boolean result = (Boolean) results.get(i);
                    String key = needLocking.get(i);
                    if (result) { // setnx成功，获得锁
                        connnection.expire(key.getBytes(), DEFAULT_BATCH_EXPIRE_TIME);
                        locked.add(key.getBytes());
                    }
                }
                needLocking.removeAll(locked); // 已锁定资源去除

                if (needLocking.size() == 0) { //成功获取全部的锁
                    return true;
                } else {
                    // 部分资源未能锁住
                    log.debug("keys: {} locked by another business：", needLocking);
                }

                if (timeout == 0) {
                    break;
                }
                Thread.sleep(500);
            } while ((System.nanoTime() - nano) < unit.toNanos(timeout));

            // 得不到锁，释放锁定的部分对象，并返回失败
            if (locked.size() > 0) {
                connnection.del(locked.toArray(new byte[0][0]));
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            connnection.close();
        }
        return true;
    }

    /**
     * 批量释放锁
     */
    public void unLock(List<String> keyList) {
        List<byte[]> keys = new CopyOnWriteArrayList();
        String lockKey;
        for (String key : keyList) {
            lockKey = prefix.concat(key);
            keys.add(lockKey.getBytes());
        }
        RedisConnection connection = redisConnectionFactory.getConnection();
        try {
            if (keys.size() > 0) {
                connection.del(keys.toArray(new byte[0][]));
                log.debug("release lock, keys : {}", keys);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            connection.close();
        }
    }


}
