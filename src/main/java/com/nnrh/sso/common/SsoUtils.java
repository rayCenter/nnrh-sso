package com.nnrh.sso.common;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    public static String postFormData(String url, Map<String, String> bodyMap, Map<String, String> headerMap) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        EntityBuilder entityBuilder = EntityBuilder.create();
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        bodyMap.forEach((key, value) -> nameValuePairs.add(new BasicNameValuePair(key, value)));
        entityBuilder.setParameters(nameValuePairs);
        httpPost.setEntity(entityBuilder.build());
        headerMap.forEach(httpPost::setHeader);
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Accept", "application/json;charset=UTF-8");

        HttpResponse response = httpClient.execute(httpPost);
        return getResult(response);
    }

    public static String postJsonData(String url, Map<String, String> bodyMap, Map<String, String> headerMap) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        StringEntity newEntity = new StringEntity(JSON.toJSONString(bodyMap), StandardCharsets.UTF_8.name());
        httpPost.setEntity(newEntity);
        headerMap.forEach(httpPost::setHeader);
        httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpPost.setHeader("Accept", "application/json;charset=UTF-8");

        HttpResponse response = httpclient.execute(httpPost);
        return getResult(response);
    }

    public static String getJsonData(String url, Map<String, String> headerMap) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        headerMap.forEach(httpGet::setHeader);
        httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpGet.setHeader("Accept", "application/json;charset=UTF-8");

        HttpResponse response = httpclient.execute(httpGet);
        return getResult(response);
    }

    private static String getResult(HttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
            if (!SsoUtils.isEmpty(entity)) {
                String result = EntityUtils.toString(entity, StandardCharsets.UTF_8.name());
                EntityUtils.consume(entity);
                return result;
            }
        }
        return null;
    }
}
