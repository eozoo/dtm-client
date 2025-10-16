package com.cowave.zoo.dtm.client.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@NoArgsConstructor
@AllArgsConstructor
public class Dtm {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    protected String gid;

    protected Type transactionType;

    protected boolean waitResult;

    public static <T> T parseJson(String json, Class<T> clzz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, clzz);
    }

    public static <T> T parseJson(byte[] bytes, Class<T> clzz) throws IOException {
        return OBJECT_MAPPER.readValue(bytes, clzz);
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public enum Type {

        /**
         * tcc
         */
        TCC("tcc"),

        /**
         * xa
         */
        XA("xa"),

        /**
         * msg
         */
        MSG("msg"),

        /**
         * saga
         */
        SAGA("saga");

        Type(String value) {
            this.value = value;
        }

        private final String value;

        public String getValue() {
            return this.value;
        }

        private static final Map<String, Type> EXIST = new HashMap<>();

        static {
            for (Type type : Type.values()) {
                EXIST.put(type.value, type);
            }
        }

        public static Type parse(String value) {
            return EXIST.get(value);
        }
    }
}
