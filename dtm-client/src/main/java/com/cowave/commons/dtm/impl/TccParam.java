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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author shanhuiming
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TccParam extends DtmParam {

    /**
     * branch id
     */
    @JsonProperty("branch_id")
    private String branchId;

    /**
     * status
     */
    @JsonProperty("status")
    private String status;

    /**
     * data
     */
    @JsonProperty("data")
    private String data;

    /**
     * branch confirm uri
     */
    @JsonProperty("confirm")
    private String confirm;

    /**
     * branch cancel uri
     */
    @JsonProperty("cancel")
    private String cancel;


    public TccParam(String gid, DtmTransaction.Type type, String branchId,
                    String confirm, String cancel, String data, String status) {
        super(gid, type);
        setSubType(type.getValue());
        this.branchId = branchId;
        this.confirm = confirm;
        this.cancel = cancel;
        this.data = data;
        this.status = status;
    }
}
