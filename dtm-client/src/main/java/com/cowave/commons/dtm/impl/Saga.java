/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm.impl;

import com.cowave.commons.dtm.DtmResult;
import com.cowave.commons.tools.HttpAsserts;
import com.cowave.commons.tools.HttpException;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import com.cowave.commons.dtm.DtmService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.feign.codec.HttpResponse;

import java.util.*;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Saga extends DtmTransaction {

    private static final String ORDERS = "orders";

    private static final String CONCURRENT = "concurrent";

    private List<Map<String, String>> steps = new ArrayList<>();

    private List<String> payloads = new ArrayList<>();

    private Map<String, String> branchHeaders = new HashMap<>();

    private List<String> passthroughHeaders = new ArrayList<>();

    private Map<String, List<Integer>> orders;

    private long timeoutToFail;

    private long retryInterval;

    private boolean concurrent;

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
    public Saga step(String action, String compensate, Object data) {
        try {
            payloads.add(toJson(data));
        } catch (Exception e) {
            throw new HttpException(e, DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Saga add step failed");
        }
        steps.add(Map.of("action", action, "compensate", compensate));
        return this;
    }

    /**
     * 提交事务
     */
    public DtmResult submit() {
        if (StringUtils.isEmpty(this.getGid())) {
            HttpResponse<DtmResult> gidResponse = dtmService.newGid();
            HttpAsserts.isTrue(gidResponse.isSuccess(),
                    gidResponse.getStatusCodeValue(), DtmResult.ERROR, "DTM Saga acquire gid failed, " + gidResponse.getMessage());

            DtmResult gidResult = gidResponse.getBody();
            HttpAsserts.isTrue(gidResult != null && gidResult.dtmSuccess(),
                    DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Saga acquire gid failed");

            this.setGid(gidResult.getGid());
        }

        addConcurrentContext();
        SagaParam sagaParam = new SagaParam(
                this.getGid(),
                Type.SAGA,
                this.getSteps(),
                this.getPayloads(),
                this.getCustomData(),
                this.isWaitResult(),
                this.getTimeoutToFail(),
                this.getRetryInterval(),
                this.getPassthroughHeaders(),
                this.getBranchHeaders()
        );

        HttpResponse<DtmResult> submitResponse = dtmService.submit(sagaParam);
        HttpAsserts.isTrue(submitResponse.isSuccess(),
                submitResponse.getStatusCodeValue(), DtmResult.ERROR, "DTM Saga " + this.getGid() + " submit failed, " + submitResponse.getMessage());

        DtmResult submitResult = submitResponse.getBody();
        HttpAsserts.isTrue(submitResult != null && submitResult.dtmSuccess(),
                DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Saga " + this.getGid() + " submit failed");

        submitResult.setGid(this.getGid());
        return submitResult;
    }

    public Saga addBranchOrder(Integer branch, List<Integer> preBranches) {
        orders.put(branch.toString(), preBranches);
        return this;
    }

    public Saga enableConcurrent() {
        concurrent = true;
        return this;
    }

    public Saga enableWaitResult() {
        this.setWaitResult(true);
        return this;
    }

    public Saga setTimeoutToFail(long timeoutToFail) {
        this.timeoutToFail = timeoutToFail;
        return this;
    }

    public Saga setRetryInterval(long retryInterval) {
        this.retryInterval = retryInterval;
        return this;
    }

    public Saga setBranchHeaders(Map<String, String> branchHeaders) {
        this.branchHeaders = branchHeaders;
        return this;
    }

    public Saga setPassthroughHeaders(ArrayList<String> passthroughHeaders) {
        this.passthroughHeaders = passthroughHeaders;
        return this;
    }

    private void addConcurrentContext() {
        if (concurrent) {
            Map<String, Object> data = Map.of(ORDERS, orders, CONCURRENT, true);
            try {
                this.customData = toJson(data);
            } catch (Exception e) {
                throw new HttpException(e, DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Saga " + this.getGid() + " submit failed");
            }
        }
    }
}
