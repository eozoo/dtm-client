package com.cowave.zoo.dtm.client.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@Getter
public class SagaParam extends DtmParam {

    @JsonProperty("steps")
    private final List<Map<String, String>> steps;

    @JsonProperty("payloads")
    private final List<String> payloads;

    @JsonProperty("custom_data")
    private final String customData;

    @JsonProperty("wait_result")
    private final Boolean waitResult;

    @JsonProperty("timeout_to_fail")
    private final Long timeoutToFail;

    @JsonProperty("request_timeout")
    private final Long requestTimeout;

    @JsonProperty("retry_interval")
    private final Long retryInterval;

    @JsonProperty("passthrough_headers")
    private final List<String> passThroughHeaders;

    @JsonProperty("branch_headers")
    private final Map<String, String> branchHeaders;

    public SagaParam(String gid, Dtm.Type type, List<Map<String, String>> steps,
                     List<String> payloads, String customData,
                     Boolean waitResult, Long timeoutToFail, Long retryInterval, Long requestTimeout,
                     List<String> passThroughHeaders, Map<String, String> branchHeaders) {
        super(gid, type);
        setSubType(type.getValue());
        this.steps = steps;
        this.payloads = payloads;
        this.customData = customData;
        this.waitResult = waitResult;
        this.timeoutToFail = timeoutToFail;
        this.retryInterval = retryInterval;
        this.requestTimeout = requestTimeout;
        this.passThroughHeaders = passThroughHeaders;
        this.branchHeaders = branchHeaders;
    }
}
