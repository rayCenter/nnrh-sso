package com.nnrh.sso.properties;

import com.nnrh.sso.common.SsoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class SsoProperties {

    public static final Properties PROPERTIES = new Properties();

    /* ------------------------------- 单点中心配置 ------------------------------- */
    public static final String SSO_CENTER_URL_KEY = "sso.center.url";
    public static String SSO_CENTER_URL;

    public static final String SSO_CENTER_GET_CODE_PATH_KEY = "sso.center.get.code.path";
    public static String SSO_CENTER_GET_CODE_PATH;

    public static final String SSO_CENTER_GET_TOKEN_PATH_KEY = "sso.center.get.token.path";
    public static String SSO_CENTER_GET_TOKEN_PATH;

    public static final String SSO_CENTER_GET_USER_INFO_PATH_KEY = "sso.center.get.user.info.path";
    public static String SSO_CENTER_GET_USER_INFO_PATH;

    public static final String SSO_CENTER_LOGOUT_PATH_KEY = "sso.center.logout.path";
    public static String SSO_CENTER_LOGOUT_PATH;

    /* ------------------------------- 应用配置 ------------------------------- */
    public static final String APP_IGNORE_CERTIFY_KEY = "app.ignore.certify";
    public static boolean APP_IGNORE_CERTIFY;

    public static final String APP_ID_KEY = "app.id";
    public static String APP_ID;

    public static final String APP_CLIENT_SECRET_KEY = "app.client.secret";
    public static String APP_CLIENT_SECRET;

    public static final String APP_CERTIFY_CHECK_PASSED_PATH_KEY = "app.certify.check.passed.path";
    public static String APP_CERTIFY_CHECK_PASSED_PATH;

    public static final String APP_IGNORE_CERTIFY_PATHS_KEY = "app.ignore.certify.paths";
    public static String[] APP_IGNORE_CERTIFY_PATHS;

    public static final String APP_LOGIN_PATHS_KEY = "app.login.paths";
    public static String[] APP_LOGIN_PATHS;

    public static final String APP_LOGOUT_PATH_KEY = "app.logout.path";
    public static String APP_LOGOUT_PATH;

    /* ------------------------------- 配置初始化 ------------------------------- */
    public SsoProperties readFile(String ssoPropertiesPath) {
        InputStream resourceIs = SsoProperties.class.getResourceAsStream(ssoPropertiesPath);
        try {
            PROPERTIES.load(resourceIs);
        } catch (IOException e) {
            throw new Error("加载单点配置文件失败");
        }
        return this;
    }

    public void initConfigVar() {
        /* ------------------------------- 单点中心配置 ------------------------------- */
        Object ssoCenterUrlVal = PROPERTIES.get(SSO_CENTER_URL_KEY);
        if (SsoUtils.isEmpty(ssoCenterUrlVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", SSO_CENTER_URL_KEY));
        } else {
            String value = ssoCenterUrlVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", SSO_CENTER_URL_KEY));
            } else {
                SSO_CENTER_URL = value;
            }
        }

        Object ssoCenterGetCodePathVal = PROPERTIES.get(SSO_CENTER_GET_CODE_PATH_KEY);
        if (SsoUtils.isEmpty(ssoCenterGetCodePathVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", SSO_CENTER_GET_CODE_PATH_KEY));
        } else {
            String value = ssoCenterGetCodePathVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", SSO_CENTER_GET_CODE_PATH_KEY));
            } else {
                SSO_CENTER_GET_CODE_PATH = value;
            }
        }

        Object ssoCenterGetTokenPathVal = PROPERTIES.get(SSO_CENTER_GET_TOKEN_PATH_KEY);
        if (SsoUtils.isEmpty(ssoCenterGetTokenPathVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", SSO_CENTER_GET_TOKEN_PATH_KEY));
        } else {
            String value = ssoCenterGetTokenPathVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", SSO_CENTER_GET_TOKEN_PATH_KEY));
            } else {
                SSO_CENTER_GET_TOKEN_PATH = value;
            }
        }

        Object ssoCenterGetUserInfoPathVal = PROPERTIES.get(SSO_CENTER_GET_USER_INFO_PATH_KEY);
        if (SsoUtils.isEmpty(ssoCenterGetUserInfoPathVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", SSO_CENTER_GET_USER_INFO_PATH_KEY));
        } else {
            String value = ssoCenterGetUserInfoPathVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", SSO_CENTER_GET_USER_INFO_PATH_KEY));
            } else {
                SSO_CENTER_GET_USER_INFO_PATH = value;
            }
        }

        Object ssoCenterLogoutPathVal = PROPERTIES.get(SSO_CENTER_LOGOUT_PATH_KEY);
        if (SsoUtils.isEmpty(ssoCenterLogoutPathVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", SSO_CENTER_LOGOUT_PATH_KEY));
        } else {
            String value = ssoCenterLogoutPathVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", SSO_CENTER_LOGOUT_PATH_KEY));
            } else {
                SSO_CENTER_LOGOUT_PATH = value;
            }
        }

        /* ------------------------------- 应用配置 ------------------------------- */
        Object appIgnoreCertifyVal = PROPERTIES.get(APP_IGNORE_CERTIFY_KEY);
        if (SsoUtils.isEmpty(appIgnoreCertifyVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", APP_IGNORE_CERTIFY_KEY));
        } else {
            String value = appIgnoreCertifyVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", APP_IGNORE_CERTIFY_KEY));
            } else {
                APP_IGNORE_CERTIFY = Boolean.parseBoolean(value);
            }
        }

        Object appIdVal = PROPERTIES.get(APP_ID_KEY);
        if (SsoUtils.isEmpty(appIdVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", APP_ID_KEY));
        } else {
            String value = appIdVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", APP_ID_KEY));
            } else {
                APP_ID = value;
            }
        }

        Object appClientSecretVal = PROPERTIES.get(APP_CLIENT_SECRET_KEY);
        if (SsoUtils.isEmpty(appClientSecretVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", APP_CLIENT_SECRET_KEY));
        } else {
            String value = appClientSecretVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", APP_CLIENT_SECRET_KEY));
            } else {
                APP_CLIENT_SECRET = value;
            }
        }

        Object appCertifyCheckPassedPathVal = PROPERTIES.get(APP_CERTIFY_CHECK_PASSED_PATH_KEY);
        if (SsoUtils.isEmpty(appCertifyCheckPassedPathVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", APP_CERTIFY_CHECK_PASSED_PATH_KEY));
        } else {
            String value = appCertifyCheckPassedPathVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", APP_CERTIFY_CHECK_PASSED_PATH_KEY));
            } else {
                APP_CERTIFY_CHECK_PASSED_PATH = value;
            }
        }

        Object appIgnoreCertifyPathsVal = PROPERTIES.get(APP_IGNORE_CERTIFY_PATHS_KEY);
        if (SsoUtils.isEmpty(appIgnoreCertifyPathsVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", APP_IGNORE_CERTIFY_PATHS_KEY));
        } else {
            String value = appIgnoreCertifyPathsVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", APP_IGNORE_CERTIFY_PATHS_KEY));
            } else {
                APP_IGNORE_CERTIFY_PATHS = value.split(",");
            }
        }

        Object appLoginPathsVal = PROPERTIES.get(APP_LOGIN_PATHS_KEY);
        if (SsoUtils.isEmpty(appLoginPathsVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", APP_LOGIN_PATHS_KEY));
        } else {
            String value = appLoginPathsVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", APP_LOGIN_PATHS_KEY));
            } else {
                APP_LOGIN_PATHS = value.split(",");
            }
        }

        Object appLogoutPathVal = PROPERTIES.get(APP_LOGOUT_PATH_KEY);
        if (SsoUtils.isEmpty(appLogoutPathVal)) {
            throw new Error(String.format("单点配置缺失 [%s] 配置", APP_LOGOUT_PATH_KEY));
        } else {
            String value = appLogoutPathVal.toString();
            if (SsoUtils.isBlank(value)) {
                throw new Error(String.format("单点配置 [%s] 配置，空", APP_LOGOUT_PATH_KEY));
            } else {
                APP_LOGOUT_PATH = value;
            }
        }
    }
}
