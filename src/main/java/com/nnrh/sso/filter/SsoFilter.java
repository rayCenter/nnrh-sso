package com.nnrh.sso.filter;

import com.alibaba.fastjson.JSON;
import com.nnrh.sso.dto.SsoInfoContext;
import com.nnrh.sso.dto.SsoTokenInfo;
import com.nnrh.sso.dto.SsoUserInfo;
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

    public final static String SSO_INFO_CONTEXT = "SSO_INFO_CONTEXT";

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
            if (checkAppLogin(request)) {
                request.getRequestDispatcher(SsoProperties.APP_CERTIFY_CHECK_PASSED_PATH).forward(request, response);
            } else {
                filterChain.doFilter(request, response);
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
            request.getSession().removeAttribute(SsoFilter.SSO_INFO_CONTEXT);
            return true;
        }
        return false;
//        return ANT_PATH_MATCHER.match(SsoProperties.APP_LOGOUT_PATH, getRequestURI(request));
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
        SsoInfoContext ssoInfoContext = getSsoInfoContext(request);
        String authorizationCode = request.getParameter("code");
        if (SsoUtils.isNotEmpty(ssoInfoContext)) {
            if (SsoUtils.isNotBlank(authorizationCode)) {
                ssoInfoContext.setAuthorizationCode(authorizationCode);
                request.getSession().setAttribute(SsoFilter.SSO_INFO_CONTEXT, ssoInfoContext);
            }
        } else {
            if (SsoUtils.isBlank(authorizationCode)) {
                return false;
            }
            ssoInfoContext = new SsoInfoContext();
            ssoInfoContext.setAuthorizationCode(authorizationCode);
            request.getSession().setAttribute(SsoFilter.SSO_INFO_CONTEXT, ssoInfoContext);
        }
        return true;
    }

    private boolean checkSsoToken(HttpServletRequest request) {
        SsoInfoContext ssoInfoContext = getSsoInfoContext(request);
        SsoTokenInfo ssoTokenInfo = ssoInfoContext.getSsoTokenInfo();
        if (SsoUtils.isNotEmpty(ssoTokenInfo)) {
            return true;
        }
        String httpUrl = String.format("%s%s", SsoProperties.SSO_CENTER_URL, SsoProperties.SSO_CENTER_GET_TOKEN_PATH);
        String clientId = SsoProperties.APP_ID;
        String clientSecret = SsoProperties.APP_CLIENT_SECRET;
        String redirectUrL = getRequestURL(request);
        String authorizationCode = ssoInfoContext.getAuthorizationCode();
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
//            throw new JsonException("-1", "获取token，http请求失败", null);
            return false;
        }
        if (SsoUtils.isBlank(responseBody)) {
//        throw new JsonException("-1", "获取token为空", null);
            return false;
        }
        try {
            ssoTokenInfo = JSON.parseObject(responseBody, SsoTokenInfo.class);
            ssoInfoContext.setSsoTokenInfo(ssoTokenInfo);
            request.getSession().setAttribute(SsoFilter.SSO_INFO_CONTEXT, ssoInfoContext);
        } catch (Exception e) {
//                throw new JsonException("-1", String.format("token信息错误：[%s]", responseBody), null);
            return false;
        }
        return true;
    }

    private boolean checkSsoUserInfo(HttpServletRequest request) {
        SsoInfoContext ssoInfoContext = getSsoInfoContext(request);
        SsoTokenInfo ssoTokenInfo = ssoInfoContext.getSsoTokenInfo();
        SsoUserInfo ssoUserInfo = ssoInfoContext.getSsoUserInfo();
        if (SsoUtils.isNotEmpty(ssoUserInfo)) {
            return true;
        }
        String httpUrl = String.format("%s%s", SsoProperties.SSO_CENTER_URL, SsoProperties.SSO_CENTER_GET_USER_INFO_PATH);
        String token = ssoTokenInfo.getAccess_token();
        String responseBody;
        try {
            responseBody = SsoUtils.postForm(httpUrl, new HashMap<String, String>() {{
                put("access_token", token);
            }});
        } catch (Exception e) {
//            throw new JsonException("-1", "获取用户信息，http请求失败", null);
            return false;
        }
        if (SsoUtils.isBlank(responseBody)) {
//            throw new JsonException("-1", "获取用户信息为空", null);
            return false;
        }
        try {
            ssoUserInfo = JSON.parseObject(responseBody, SsoUserInfo.class);
            ssoInfoContext.setSsoUserInfo(ssoUserInfo);
            request.getSession().setAttribute(SsoFilter.SSO_INFO_CONTEXT, ssoInfoContext);
        } catch (Exception e) {
//                throw new JsonException("-1", String.format("token信息错误：[%s]", responseBody), null);
            return false;
        }
        return true;
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

    private SsoInfoContext getSsoInfoContext(HttpServletRequest request) {
        return (SsoInfoContext) request.getSession().getAttribute(SsoFilter.SSO_INFO_CONTEXT);
    }
}
