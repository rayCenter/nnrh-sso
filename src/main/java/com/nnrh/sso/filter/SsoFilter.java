package com.nnrh.sso.filter;

import com.alibaba.fastjson.JSON;
import com.nnrh.sso.dto.SsoTokenInfoDto;
import com.nnrh.sso.dto.SsoUserInfoDto;
import com.nnrh.sso.exception.JsonException;
import com.nnrh.sso.properties.SsoProperties;
import com.nnrh.sso.common.SsoUtils;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SsoFilter implements Filter, ISsoHook {

    private final static AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) {
        String ssoPropertiesPath = filterConfig.getInitParameter("ssoPropertiesPath");
        new SsoProperties().readFile(ssoPropertiesPath).initConfigVar();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        if (checkAppIgnoreCertify() || checkAppLogout(request) || checkAppIgnoreCertifyPaths(request)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String authorizationCode = request.getParameter("code");
        if (SsoUtils.isBlank(authorizationCode)) {
            response.sendRedirect(getSsoCodeUrl(request));
            return;
        } else {
            SsoTokenInfoDto ssoTokenInfoDto = getSsoTokenInfo(authorizationCode, request);
            SsoUserInfoDto ssoUserInfoDto = getSsoUserInfo(ssoTokenInfoDto.getAccess_token());
            getSsoInfo(authorizationCode, ssoTokenInfoDto, ssoUserInfoDto);
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

    @Override
    public void destroy() {
    }

    private boolean checkAppIgnoreCertify() {
        return SsoProperties.APP_IGNORE_CERTIFY;
    }

    private SsoTokenInfoDto getSsoTokenInfo(String authorizationCode, HttpServletRequest request) {
        String httpUrl = String.format("%s%s", SsoProperties.SSO_CENTER_URL, SsoProperties.SSO_CENTER_GET_TOKEN_PATH);
        String clientId = SsoProperties.APP_ID;
        String clientSecret = SsoProperties.APP_CLIENT_SECRET;
        String redirectUrL = getRequestURL(request);
        String responseBody;
        try {
            responseBody = SsoUtils.postForm(httpUrl, new HashMap<String, String>() {{
                put("client_id", clientId);
                put("client_secret", clientSecret);
                put("grant_type", "authorization_code");
                put("redirect_uri", redirectUrL);
                put("code", authorizationCode);
            }});
        } catch (Exception e) {
            throw new JsonException("-1", "获取token，http请求失败", null);
        }
        if (SsoUtils.isNotBlank(responseBody)) {
            try {
                return JSON.parseObject(responseBody, SsoTokenInfoDto.class);
            } catch (Exception e) {
                throw new JsonException("-1", String.format("token信息错误：[%s]", responseBody), null);
            }
        }
        return null;
    }

    private SsoUserInfoDto getSsoUserInfo(String token) {
        String httpUrl = String.format("%s%s", SsoProperties.SSO_CENTER_URL, SsoProperties.SSO_CENTER_GET_USER_INFO_PATH);
        String responseBody;
        try {
            responseBody = SsoUtils.postForm(httpUrl, new HashMap<String, String>() {{
                put("access_token", token);
            }});
        } catch (Exception e) {
            throw new JsonException("-1", "获取用户信息，http请求失败", null);
        }
        if (SsoUtils.isNotBlank(responseBody)) {
            try {
                return JSON.parseObject(responseBody, SsoUserInfoDto.class);
            } catch (Exception e) {
                throw new JsonException("-1", String.format("token信息错误：[%s]", responseBody), null);
            }
        }
        return null;
    }

    private boolean checkAppLogout(HttpServletRequest request) {
        return ANT_PATH_MATCHER.match(SsoProperties.APP_LOGOUT_PATH, getRequestURI(request));
    }

    private boolean checkAppIgnoreCertifyPaths(HttpServletRequest request) {
        String requestURI = getRequestURI(request);
        for (String appIgnoreCertifyPath : SsoProperties.APP_IGNORE_CERTIFY_PATHS) {
            if (ANT_PATH_MATCHER.match(appIgnoreCertifyPath, requestURI)) {
                return true;
            }
        }
        return false;
    }

    private String getSsoCodeUrl(HttpServletRequest request) {
        return String.format("%s%s?%s",
                SsoProperties.SSO_CENTER_URL,
                SsoProperties.SSO_CENTER_GET_CODE_PATH,
                String.format("response_type=code&client_id=%s&redirect_uri=%s",
                        SsoProperties.APP_ID,
                        getRequestURL(request)));
    }

    private String getRequestURL(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }

    private String getRequestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @Override
    public void getSsoInfo(String authorizationCode, SsoTokenInfoDto ssoTokenInfoDto, SsoUserInfoDto ssoUserInfoDto) {
        System.out.println(0);
    }


}
