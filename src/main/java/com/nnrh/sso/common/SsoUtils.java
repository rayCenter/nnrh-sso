package com.nnrh.sso.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class SsoUtils {

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }

    public static boolean isEmpty(Object object) {
        return Objects.isNull(object);
    }

    public static String postForm(String httpUrl, Map<String, String> paramMap) throws Exception {
        StringBuilder paramBuilder = new StringBuilder();
        paramMap.forEach((key, value) -> paramBuilder.append(String.format("%s=%s&", key, value)));
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestMethod("POST");

        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "application/json");
        connection.connect();
        OutputStreamWriter output = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8.name());
        output.append(paramBuilder.toString());

        output.flush();
        output.close();

        if (connection.getResponseCode() == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String line;
            StringBuilder responseBodyBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseBodyBuilder.append(line);
            }
            connection.disconnect();
            return responseBodyBuilder.toString();
        }
        connection.disconnect();
        return null;
    }

}
