package com.wuweiit.manager.token.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;


/**
 * 日志打印拦截器
 *
 * 用于打印请求三方的相关日志信息，便于日志分析问题
 *
 * @author marker
 */
@Slf4j
public class LogClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {


        log.info("{} {}", httpRequest.getMethod(), httpRequest.getURI());
        long startTime =System.currentTimeMillis();
        ClientHttpResponse clientHttpResponse = clientHttpRequestExecution.execute(httpRequest,bytes);
        long etime = System.currentTimeMillis() - startTime;



        log.info("req time={} resp: {}", etime, "");

        return clientHttpResponse;
    }
}
