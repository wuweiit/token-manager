package com.wuweibi.manager.token.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenInfo {

    /**
     * 用户Token
     */
    private String accessToken;

    /**
     * 刷新Token
     */
    private String refreshToken;

    /**
     * accessToken  生命周期 单位秒
     */
    private long expiresIn;

}
