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

public class SsoFilter implements Filter {

    private final static AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private static final String SSO_CODE = "ssoCode";
    private static final String SSO_TOKEN = "ssoToken";
    private static final String SSO_USER_INFO = "ssoUserInfo";

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

        if (checkAppIgnoreCertify()) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (checkAppLogout(request)) {
            response.sendRedirect(getLogoutUrl(request));
            return;
        }

        if (checkAppIgnoreCertifyPaths(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (checkSsoCode(request) && checkSsoToken(request) && checkSsoUserInfo(request)) {
            if (!checkAppLogin(request)) {
                filterChain.doFilter(request, response);
            } else {
                request.getRequestDispatcher(SsoProperties.APP_CERTIFY_CHECK_PASSED_PATH).forward(request, response);
            }
        } else {
            response.sendRedirect(getSsoCodeUrl(request));
        }
    }

    @Override
    public void destroy() {
    }

    private boolean checkAppIgnoreCertify() {
        return SsoProperties.APP_IGNORE_CERTIFY;
    }

    private boolean checkAppLogout(HttpServletRequest request) {
        if (ANT_PATH_MATCHER.match(SsoProperties.APP_LOGOUT_PATH, getRequestURI(request))) {
            request.getSession().removeAttribute(SsoFilter.SSO_CODE);
            request.getSession().removeAttribute(SsoFilter.SSO_TOKEN);
            request.getSession().removeAttribute(SsoFilter.SSO_USER_INFO);
            return true;
        }
        return false;
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

    private boolean checkSsoCode(HttpServletRequest request) {
        Object ssoCodeVal = request.getSession().getAttribute(SsoFilter.SSO_CODE);
        String code = request.getParameter("code");
        if (SsoUtils.isNotEmpty(ssoCodeVal)) {
            if (SsoUtils.isNotBlank(code)) {
                request.getSession().setAttribute(SsoFilter.SSO_CODE, code);
            }
        } else {
            if (SsoUtils.isBlank(code)) {
                return false;
            }
            request.getSession().setAttribute(SsoFilter.SSO_CODE, code);
        }
        return true;
    }

    private boolean checkSsoToken(HttpServletRequest request) {
        if (SsoUtils.isNotEmpty(request.getSession().getAttribute(SsoFilter.SSO_TOKEN))) {
            return true;
        }
        String httpUrl = String.format("%s%s", SsoProperties.SSO_CENTER_URL, SsoProperties.SSO_CENTER_GET_TOKEN_PATH);
        String clientId = SsoProperties.APP_ID;
        String clientSecret = SsoProperties.APP_CLIENT_SECRET;
        String redirectUrL = getRequestURL(request);
        String code = request.getSession().getAttribute(SsoFilter.SSO_CODE).toString();
        String responseBody;
        try {
            responseBody = SsoUtils.postForm(httpUrl, new HashMap<String, String>() {{
                put("client_id", clientId);
                put("client_secret", clientSecret);
                put("grant_type", "authorization_code");
                put("redirect_uri", redirectUrL);
                put("code", code);
            }});

        } catch (Exception e) {
            throw new JsonException("-1", "获取token，http请求失败", null);
        }
        if (SsoUtils.isNotBlank(responseBody)) {
            try {
                SsoTokenInfoDto ssoTokenInfoDto = JSON.parseObject(responseBody, SsoTokenInfoDto.class);
                request.getSession().setAttribute(SsoFilter.SSO_TOKEN, ssoTokenInfoDto);
            } catch (Exception e) {
                throw new JsonException("-1", String.format("token信息错误：[%s]", responseBody), null);
            }
            return true;
        }
        throw new JsonException("-1", "获取token为空", null);
    }

    private boolean checkSsoUserInfo(HttpServletRequest request) {
        SsoUserInfoDto ssoUserInfoDto = (SsoUserInfoDto) request.getSession().getAttribute(SsoFilter.SSO_USER_INFO);
        if (SsoUtils.isNotEmpty(ssoUserInfoDto)) {
            return true;
        }
        String httpUrl = String.format("%s%s", SsoProperties.SSO_CENTER_URL, SsoProperties.SSO_CENTER_GET_USER_INFO_PATH);
        SsoTokenInfoDto ssoTokenInfoDto = (SsoTokenInfoDto) request.getSession().getAttribute(SsoFilter.SSO_TOKEN);
        String token = ssoTokenInfoDto.getAccess_token();
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
                ssoUserInfoDto = JSON.parseObject(responseBody, SsoUserInfoDto.class);
                request.getSession().setAttribute(SsoFilter.SSO_USER_INFO, ssoUserInfoDto);
            } catch (Exception e) {
                throw new JsonException("-1", String.format("token信息错误：[%s]", responseBody), null);
            }
            return true;
        }
        throw new JsonException("-1", "获取用户信息为空", null);
    }

    private boolean checkAppLogin(HttpServletRequest request) {
        String requestURI = getRequestURI(request);
        for (String appLoginPath : SsoProperties.APP_LOGIN_PATHS) {
            if (ANT_PATH_MATCHER.match(appLoginPath, requestURI)) {
                return true;
            }
        }
        return false;
    }

    private String getLogoutUrl(HttpServletRequest request) {
        return String.format("%s%s?redirectUrl=%s",
                SsoProperties.SSO_CENTER_URL,
                SsoProperties.SSO_CENTER_LOGOUT_PATH,
                getRequestURL(request));
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

}
