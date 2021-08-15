package com.wuweibi.manager.token.api.adapter;

import org.springframework.web.client.RestTemplate;


public abstract class BaseAPI {

    protected RestTemplate restTemplate;

    public BaseAPI(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
}
