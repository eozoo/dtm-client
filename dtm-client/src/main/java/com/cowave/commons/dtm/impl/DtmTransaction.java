/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtmTransaction {

    private static final ObjectMapper objectMapper =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    protected String gid;

    protected Type transactionType;

    protected boolean waitResult;

    public static <T> T parseJson(String json, Class<T> clzz) throws JsonProcessingException {
        return objectMapper.readValue(json, clzz);
    }

    public static <T> T parseJson(byte[] bytes, Class<T> clzz) throws IOException {
        return objectMapper.readValue(bytes, clzz);
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
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
