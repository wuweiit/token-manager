package com.wuweibi.manager.token;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 测试使用的启动器
 * @author marker
 */
@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
