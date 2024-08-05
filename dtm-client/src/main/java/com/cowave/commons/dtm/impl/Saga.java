/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm.impl;

import com.cowave.commons.dtm.DtmException;
import com.cowave.commons.dtm.DtmProperties;
import com.cowave.commons.dtm.model.DtmResponse;
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

    public Saga(String gid, DtmService dtmService, DtmProperties dtmProperties) {
        super(gid, Type.SAGA, dtmProperties, false);
        this.concurrent = false;
        this.dtmService = dtmService;
        this.orders = new HashMap<>();
    }

    /**
     * 添加step步骤
     */
    public Saga add(String action, String compensate, Object data) throws DtmException {
        try {
            payloads.add(toJson(data));
        } catch (Exception e) {
            throw new DtmException("DTM Saga add step failed", e);
        }
        steps.add(Map.of("action", action, "compensate", compensate));
        return this;
    }

    /**
     * 提交事务
     */
    public DtmResponse submit() throws DtmException {
        if (StringUtils.isEmpty(this.getGid())) {
            HttpResponse<DtmResponse> gidResponse = dtmService.newGid();
            if(gidResponse.isFailed()){
                throw new DtmException("DTM Saga acquire gid failed");
            }
            this.setGid(parseGid(Objects.requireNonNull(gidResponse.getBody())));
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

        HttpResponse<DtmResponse> submitResponse = dtmService.submit(sagaParam);
        if(submitResponse.isFailed()){
            throw new DtmException("DTM Saga " + this.getGid() + " submit failed, " + submitResponse.getMessage());
        }
        DtmResponse response = submitResponse.getBody();
        response.setGid(this.getGid());
        return response;
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

    private void addConcurrentContext() throws DtmException {
        if (concurrent) {
            Map<String, Object> data = Map.of(ORDERS, orders, CONCURRENT, true);
            try {
                this.customData = toJson(data);
            } catch (Exception e) {
                throw new DtmException("DTM Saga " + this.getGid() + " submit failed", e);
            }
        }
    }
}
