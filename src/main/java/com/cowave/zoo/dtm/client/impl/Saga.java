package com.cowave.zoo.dtm.client.impl;

import com.cowave.zoo.dtm.client.DtmResponse;
import com.cowave.zoo.dtm.client.DtmService;
import com.cowave.zoo.http.client.asserts.HttpAsserts;
import com.cowave.zoo.http.client.asserts.HttpException;
import com.cowave.zoo.http.client.response.HttpResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@NoArgsConstructor
public class Saga extends Dtm {
    private static final String ORDERS = "orders";
    private static final String CONCURRENT = "concurrent";

    private final List<String> passthroughHeaders = new ArrayList<>();

    private final Map<String, String> branchHeaders = new HashMap<>();

    private final List<Map<String, String>> steps = new ArrayList<>();

    private final List<String> payloads = new ArrayList<>();

    private Map<String, List<Integer>> orders;

    private Long timeoutToFail;

    private Long retryInterval;

    private Long requestTimeout;

    private Boolean concurrent;

    private String customData;

    private DtmService dtmService;

    public Saga(String gid, DtmService dtmService) {
        super(gid, Type.SAGA, false);
        this.concurrent = false;
        this.dtmService = dtmService;
        this.orders = new HashMap<>();
    }

    /**
     * 添加step步骤
     */
    public void step(String action, String compensate, Object data) {
        try {
            payloads.add(toJson(data));
        } catch (Exception e) {
            throw new HttpException(e, DtmResponse.HTTP_FAILURE, DtmResponse.DTM_FAILURE, "Saga step failed");
        }
        Map<String, String> stepMap = new HashMap<>();
        stepMap.put("action", action);
        stepMap.put("compensate", compensate);
        steps.add(stepMap);
    }

    /**
     * 提交事务
     */
    public HttpResponse<DtmResponse> submit() {
        if (!StringUtils.hasText(gid)) {
            HttpResponse<DtmGid> httpResponse = dtmService.newGid();
            HttpAsserts.isTrue(httpResponse.isSuccess(),
                    httpResponse.getStatusCodeValue(), DtmResponse.DTM_ERROR, "Saga acquire gid failed, " + httpResponse.getMessage());

            DtmGid dtmGid = httpResponse.getBody();
            HttpAsserts.isTrue(dtmGid != null && dtmGid.dtmSuccess(),
                    DtmResponse.HTTP_FAILURE, DtmResponse.DTM_FAILURE, "Saga acquire gid failed");
            this.gid = dtmGid.getGid();
        }

        addConcurrentContext();
        SagaParam sagaParam = new SagaParam(
                this.gid,
                Type.SAGA,
                this.steps,
                this.payloads,
                this.customData,
                this.waitResult,
                this.timeoutToFail,
                this.retryInterval,
                this.requestTimeout,
                this.passthroughHeaders,
                this.branchHeaders
        );
        return dtmService.submit(sagaParam);
    }

    private void addConcurrentContext() {
        if (concurrent) {
            Map<String, Object> data = new HashMap<>();
            data.put(ORDERS, orders);
            data.put(CONCURRENT, true);
            try {
                this.customData = toJson(data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Saga concurrent(boolean concurrent) {
        this.concurrent = concurrent;
        return this;
    }

    public Saga waitResult(boolean waitResult) {
        this.waitResult = waitResult;
        return this;
    }

    public Saga timeoutFail(long timeoutFail) {
        this.timeoutToFail = timeoutFail;
        return this;
    }

    public Saga requestTimeout(long requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }

    public Saga retryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
        return this;
    }

    public Saga branchOrder(Integer branch, List<Integer> preBranches) {
        this.orders.put(branch.toString(), preBranches);
        return this;
    }

    public Saga branchHeader(String headerName, String headerValue) {
        this.branchHeaders.put(headerName, headerValue);
        return this;
    }

    public Saga passThroughHeader(String headerName) {
        this.passthroughHeaders.add(headerName);
        return this;
    }
}
