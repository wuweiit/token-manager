package com.wuweiit.manager.token.api.waibao.bean;

import com.wuweiit.manager.token.bean.TokenInfo;
import lombok.Data;

@Data
public class WaibaoToken {
    private String error;
    private String message;
    private TokenInfo data;
}
