package com.vnsolution.virtualassistant.util;

import com.google.gson.Gson;

public class SerialUtils {
    public static String serializeObject(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    public static Object unserializeObject(String s, Object o) {
        Gson gson = new Gson();
        return gson.fromJson(s, o.getClass());
    }

    public static Object cloneObject(Object o) {
        String s = serializeObject(o);
        Object object = unserializeObject(s, o);
        return object;
    }
}
