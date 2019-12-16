package com.example.mobiledevproject.util;

import android.os.Handler;
import android.os.Message;

import com.example.mobiledevproject.config.API;
import com.example.mobiledevproject.config.WebConfig;
import com.example.mobiledevproject.model.UserCreate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    public static void getOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);

    }

    public static void postOkHttpRequest(String address, String jsonInfo, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType
                .parse("application/json; charset=utf-8"), jsonInfo);
        Request request = new Request.Builder().url(address)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void getRequestWithToken(String address, String token, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address)
                .addHeader(WebConfig.TOKEN_KEY, WebConfig.TOKEN_VALUE_PRE+token)
                .addHeader("Content-Type", "application/json")
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    public static void postRequestWithToken(String address, String token, String jsonInfo, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType
                .parse("application/json; charset=utf-8"), jsonInfo);
        Request request = new Request.Builder().url(address)
                .addHeader(WebConfig.TOKEN_KEY, WebConfig.TOKEN_VALUE_PRE + token)
                .addHeader("Content-Type", "application/json")
                .build();
        okHttpClient.newCall(request).enqueue(callback);

    }

    public static void postOkHttpRequestByForm(String address,String token, RequestBody requestBody, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address)
                .addHeader("Content-Type", "multipart/form-data")
                .addHeader(WebConfig.TOKEN_KEY, WebConfig.TOKEN_VALUE_PRE+token)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void postRequestWithToken2(String address, String token, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(null, "");
        Request request = new Request.Builder().url(address)
                .addHeader(WebConfig.TOKEN_KEY, WebConfig.TOKEN_VALUE_PRE+token)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }


    public static void getToken(UserCreate user, Handler handler){
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonInfo = gson.toJson(user);
        Message message = handler.obtainMessage();

        postOkHttpRequest(API.LOGIN, jsonInfo, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                message.obj = "网络请求错误，请重试";
                message.what = -1;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                JsonObject jsonObject;
                if((jsonObject=StatusCodeUtil.isNormalResponse(responseBody))==null){
                    message.obj = "网络请求错误，请重试";
                    message.what = -1;
                    handler.sendMessage(message);
                } else {
                    int status = jsonObject.get("status").getAsInt();
                    if(!StatusCodeUtil.isNormalStatus(status)){
                        message.obj = "返回信息错误，错误码"+status;
                        message.what = -1;
                        handler.sendMessage(message);
                    } else{
                        JsonObject data = jsonObject.get("data").getAsJsonObject();
                        String accessToken = data.get("accessToken").getAsString();
                        message.obj = accessToken;
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }
            }
        });
    }

}
