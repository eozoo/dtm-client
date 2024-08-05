/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
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
