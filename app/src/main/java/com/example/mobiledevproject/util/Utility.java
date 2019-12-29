package com.example.mobiledevproject.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.mobiledevproject.config.StorageConfig;
import com.example.mobiledevproject.model.GroupCreate;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class Utility {
    private static final String TAG = "Utility";

    public static GroupCreate setGroupInfo(JsonObject cur) {
        GroupCreate createdGroup = new GroupCreate();

        //  使用gson直接转，应该可以用，留待验证
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        createdGroup = gson.fromJson(cur, GroupCreate.class);

        createdGroup.setGroupName(cur.get("name").getAsString());
        createdGroup.setDescription(cur.get("desc").getAsString());
        createdGroup.setCheckRule(cur.get("checkRule").getAsString());
        createdGroup.setMasterId(cur.get("circleMasterId").getAsInt());
        createdGroup.setStartAt(cur.get("startAt").getAsString());
        createdGroup.setEndAt(cur.get("endAt").getAsString());
        createdGroup.setType(cur.get("type").getAsString());
        createdGroup.setGroupId(cur.get("id").getAsInt());
        return createdGroup;
    }

    public static boolean hasSpItem(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(StorageConfig.SP_NAME, Context.MODE_PRIVATE);
        if(sp.getString(key, null)!=null){
            return true;
        }
        return false;
    }

    public static void setData(Context context, String key, Object data){
        SharedPreferences sp = context.getSharedPreferences(StorageConfig.SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if(data instanceof String){
            editor.putString(key, (String)data);
        } else if(data instanceof Integer){
            editor.putInt(key, (int)data);
        }
        editor.commit();
    }


    public static String getData(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(StorageConfig.SP_NAME, Context.MODE_PRIVATE);
        if(!hasSpItem(context, key)){
            return null;
        }
        String data = sp.getString(key, null);
        return data;
    }

    public static int getIntData(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences(StorageConfig.SP_NAME, Context.MODE_PRIVATE);
        int data = sp.getInt(key, -1);
        return data;
    }

    //  利用泛型和json数据来存取数组
    public static<T> void setDataList(Context context, String key, List<T> dataList){
        if(null == dataList || dataList.size()<0){
            return;
        }

        Log.i(TAG, "setDataList: "+dataList.size());

        SharedPreferences sp = context.getSharedPreferences(StorageConfig.SP_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonInfo = gson.toJson(dataList);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, jsonInfo);
        editor.commit();
    }

    public static<T> List<T> getDataList(Context context, String key, Class<T> cls){
        if(!hasSpItem(context, key)){
            return null;
        }
        SharedPreferences sp = context.getSharedPreferences(StorageConfig.SP_NAME, Context.MODE_PRIVATE);
        List<T> dataList = new ArrayList<T>();
        String jsonInfo = sp.getString(key, null);

        Gson gson = new Gson();

        JsonArray jsonArray = new JsonParser().parse(jsonInfo).getAsJsonArray();
        for(JsonElement jsonElement: jsonArray){
            dataList.add(gson.fromJson(jsonElement, cls));
        }
        return dataList;

    }
}
