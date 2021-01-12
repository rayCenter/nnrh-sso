package com.nnrh.sso.filter;

import com.nnrh.sso.dto.SsoTokenInfoDto;
import com.nnrh.sso.dto.SsoUserInfoDto;

public interface ISsoHook {

    public void getSsoInfo(String authorizationCode, SsoTokenInfoDto ssoTokenInfoDto, SsoUserInfoDto ssoUserInfoDto);

}
