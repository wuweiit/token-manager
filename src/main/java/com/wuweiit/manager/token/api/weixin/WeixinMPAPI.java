package com.wuweiit.manager.token.api.weixin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wuweiit.manager.token.SecretConfig;
import com.wuweiit.manager.token.api.TokenAPI;
import com.wuweiit.manager.token.api.adapter.BaseAPI;
import com.wuweiit.manager.token.bean.TokenInfo;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

public class WeixinMPAPI extends BaseAPI implements TokenAPI {


    public WeixinMPAPI(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @SneakyThrows
    @Override
    public TokenInfo getToken(SecretConfig secretConfig, Map<String, Object> params) {
        String appid = secretConfig.getAppid();
        String secret = secretConfig.getAppsecret();

        URI uri = new URIBuilder("https://api.weixin.qq.com/cgi-bin/token")
                .addParameter("grant_type", "client_credential")
                .addParameter("appid", appid)
                .addParameter("secret", secret)
                .build();

        // build http headers
        HttpHeaders headers = new HttpHeaders();
//        headers.add("x-auth-token","123");

        HttpEntity httpEntity = new HttpEntity<String>(headers);

        // send request and get response
        String result = restTemplate.getForObject(uri, String.class);

//{"access_token":" -hZ9B-zxQrBkqolf5O- ","expires_in":7200}
        JSONObject jsonObject = JSON.parseObject(result);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setAccessToken(jsonObject.getString("access_token"));
        tokenInfo.setExpiresIn(jsonObject.getLong("expires_in"));

        return tokenInfo;
    }
}
