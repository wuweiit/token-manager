package com.wuweibi.manager.token.api.adapter;

import org.springframework.web.client.RestTemplate;


/**
 * BaseAPI
 * @author marker
 */
public abstract class BaseAPI {

    protected RestTemplate restTemplate;

    /**
     * 构造
     * @param restTemplate restTemplate
     */
    public BaseAPI(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
}
