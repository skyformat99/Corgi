package com.ibeiliao.deployment.common.util;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 功能: http 工具类
 * <p>
 * 详细:
 *
 * @author jingyesi   17/1/25
 */
public class HttpUtils {

    private static final Logger logger  = LoggerFactory.getLogger(HttpUtils.class);

    private static final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build();



    /**
     * post 发送请求, 若出现异常时直接返回 null
     *
     * @param url    url地址
     * @param params 参数
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        FormBody.Builder builder = new FormBody.Builder();

        if(params != null && !params.isEmpty()){
            for(Map.Entry<String,String> entry : params.entrySet()){
                String key = entry.getKey();
                String value = entry.getValue();
                builder.add(key, value);
            }
        }

        Call call = client.newCall(new Request.Builder().url(url).post(builder.build()).build());
        try {
            Response response = call.execute();
            if(response.isSuccessful()){
                return response.body().toString();
            }else{
                logger.error("请求url:{} 失败 | 返回码:{}, 返回信息:{}", url, response.code(), response.body().toString());
                return null;
            }
        }catch (IOException e){
            logger.error("请求url:{} 失败 | msg:{}", url, e.getMessage(), e);
            return null;
        }
    }


}
