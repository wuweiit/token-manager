package com.wuweibi.manager.token.api.weixin.bean;

import com.wuweibi.manager.token.bean.TokenInfo;
import lombok.Data;

@Data
public class WxToken extends TokenInfo {
    private Integer errcode;
    private String errmsg;
}
