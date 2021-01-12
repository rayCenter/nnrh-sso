package com.nnrh.sso.dto;

public class SsoInfoContext {

    private String authorizationCode;

    private SsoTokenInfo ssoTokenInfo;

    private SsoUserInfo ssoUserInfo;

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    public SsoTokenInfo getSsoTokenInfo() {
        return ssoTokenInfo;
    }

    public void setSsoTokenInfo(SsoTokenInfo ssoTokenInfo) {
        this.ssoTokenInfo = ssoTokenInfo;
    }

    public SsoUserInfo getSsoUserInfo() {
        return ssoUserInfo;
    }

    public void setSsoUserInfo(SsoUserInfo ssoUserInfo) {
        this.ssoUserInfo = ssoUserInfo;
    }
}
