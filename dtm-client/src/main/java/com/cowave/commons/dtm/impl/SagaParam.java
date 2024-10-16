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

import java.util.List;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SagaParam extends DtmParam {

    @JsonProperty("steps")
    private List<Map<String, String>> steps;

    @JsonProperty("payloads")
    private List<String> payloads;

    @JsonProperty("custom_data")
    private String customData;

    @JsonProperty("wait_result")
    private boolean waitResult;

    @JsonProperty("timeout_to_fail")
    private long timeoutToFail;

    @JsonProperty("retry_interval")
    private long retryInterval;

    @JsonProperty("passthrough_headers")
    private List<String> passThroughHeaders;

    @JsonProperty("branch_headers")
    private Map<String, String> branchHeaders;

    public SagaParam(String gid, DtmTransaction.Type type, List<Map<String, String>> steps,
                     List<String> payloads, String customData,
                     boolean waitResult, long timeoutToFail, long retryInterval,
                     List<String> passThroughHeaders, Map<String, String> branchHeaders) {
        super(gid, type);
        setSubType(type.getValue());
        this.steps = steps;
        this.payloads = payloads;
        this.customData = customData;
        this.waitResult = waitResult;
        this.timeoutToFail = timeoutToFail;
        this.retryInterval = retryInterval;
        this.passThroughHeaders = passThroughHeaders;
        this.branchHeaders = branchHeaders;
    }
}
