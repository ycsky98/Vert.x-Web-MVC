package org.vertx.web.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author yangcong
 *
 * JSON输出类
 */
public class JSONPrint {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true);

    /**
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String toJSON(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    /**
     *
     * @param json
     * @param tClass
     * @return
     * @param <T>
     * @throws JsonProcessingException
     */
    public static <T>T parseJSON(String json, Class<T> tClass) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, tClass);
    }
}
