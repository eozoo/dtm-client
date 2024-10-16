/*
 * Copyright (c) 2017～2024 Cowave All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
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
