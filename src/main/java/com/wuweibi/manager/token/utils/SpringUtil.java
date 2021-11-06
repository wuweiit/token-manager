
package com.wuweibi.manager.token.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;

/**
 * spring  工具类
 *
 * @author marker
 */
public final class SpringUtil implements ApplicationContextAware {

    /** Spring上下文 */
    private static ApplicationContext applicationContext;


    private SpringUtil(){}

    @Override
    public void setApplicationContext(ApplicationContext arg0)
            throws BeansException {
        SpringUtil.applicationContext = arg0;
    }

    /**
     * 根据类型获取bean
     * @param clazz 类
     * @param <T> T
     * @return T
     */
    public static <T> T getBean(Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 根据beanname获取bean
     *
     * @param name beanName
     * @param <T> T
     * @return T
     */
    public static <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }


    /**
     * 根据beanname获取bean和类型获取
     * @param name beanName
     * @param clazz clazz
     * @param <T> T
     * @return T
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }


    /**
     * 是否为生产环境
     * @return boolean
     */
    public static boolean isProduction(){
        Environment env = getBean(Environment.class);
        String active = env.getProperty("spring.profiles.active");
        if("prod".equals(active)){
            return true;
        }
        return false;
    }

    /**
     * 是否为生产环境
     * @return String
     */
    public static String getProfilesActive(){
        Environment env = getBean(Environment.class);
        String active = env.getProperty("spring.profiles.active");
        return active;
    }

}
