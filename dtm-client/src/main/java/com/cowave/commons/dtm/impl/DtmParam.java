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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author shanhuiming
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "subType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TccParam.class, name = "tcc"),
        @JsonSubTypes.Type(value = SagaParam.class, name = "saga")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtmParam {

    /**
     * gid
     */
    @JsonProperty("gid")
    private String gid;

    /**
     * trans type string value
     */
    @JsonProperty("trans_type")
    private String transType;

    /**
     * this attribute just for mark different sub class for encode/decode.
     */
    @JsonProperty("subType")
    private String subType;

    public DtmParam(String gid, DtmTransaction.Type type) {
        this.gid = gid;
        this.transType = type.getValue();
    }
}
