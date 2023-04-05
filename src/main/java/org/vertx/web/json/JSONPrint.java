package org.vertx.web.json;

import com.google.gson.Gson;

/**
 * @author yangcong
 *
 * JSON输出类
 */
public class JSONPrint {

    private static final Gson GSON = new Gson();

    /**
     * @param object
     * @return
     */
    public static String toJSON(Object object){
        return GSON.toJson(object);
    }

    /**
     *
     * @param json
     * @param tClass
     * @return
     * @param <T>
     */
    public static <T>T parseJSON(String json, Class<T> tClass){
        return GSON.fromJson(json, tClass);
    }
}
