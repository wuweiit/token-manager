package com.wuweiit.manager.token.api.waibao;

import com.wuweiit.manager.token.SecretConfig;
import com.wuweiit.manager.token.api.TokenAPI;
import com.wuweiit.manager.token.api.adapter.BaseAPI;
import com.wuweiit.manager.token.bean.TokenInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


/**
 * 外包API接口
 */
@Slf4j
public class WaibaoTokenAPI extends BaseAPI implements TokenAPI {


    /**
     * 外包项目地址合作伙伴token
     */
    String JIAYU_OLD_PARTNER_TOKEN_URL = "/member/partner/token";

    public WaibaoTokenAPI(RestTemplate restTemplate) {
        super(restTemplate);
    }


    @Override
    public TokenInfo getToken(SecretConfig secretConfig, Map<String, Object> params) {
        String uri = secretConfig.getApiHost() + JIAYU_OLD_PARTNER_TOKEN_URL;

//        HttpUriRequest httpUriRequest = RequestBuilder.post().setHeader(jsonHeader)
//                .setUri(uri)
//                .addParameter("mobile", (String) params.get("params"))
//                .setEntity(new StringEntity("", Charset.forName("utf-8")))
//                .build();

//        HttpUriResponse response = super.execute(httpUriRequest);
//
//        WaibaoToken waibaoToken = JsonUtil.parseObject(response.getBody(), WaibaoToken.class);
//
//        if (waibaoToken.getError() != null) {
//            String message = waibaoToken.getMessage();
//            log.error("调用外包接口异常： {}", message);
//            throw new RuntimeException("调用三方接口异常, 原因" + message);
//        }
        return null;
    }


}
