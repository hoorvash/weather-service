package com.nik.weather.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonUtil {

    private static Gson getGsonInstance(){
        return new Gson();
    }

    public static String serialize(Object src){
        return getGsonInstance().toJson(src);
    }

    public static <T> T deserialize(String response,Class<T> cls){
        return getGsonInstance().fromJson(response, cls);
    }

    public static  <T> T deserialize(String json, Type typeOfT){
        return getGsonInstance().fromJson(json, typeOfT);
    }
}
