/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm.impl;

import com.cowave.commons.dtm.*;
import com.cowave.commons.dtm.model.DtmResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.feign.codec.HttpResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@NoArgsConstructor
public class Tcc extends DtmTransaction {

    private static final String OP = "try";

    private static final int MAX_BRANCH_ID = 99;

    private String branchPrefix;

    private int subBranchId;

    private DtmService dtmService;

    public Tcc(String branchPrefix, String gid, DtmService dtmService, DtmProperties dtmProperties) {
        super(gid, Type.TCC, dtmProperties, false);
        this.dtmService = dtmService;
        this.branchPrefix = branchPrefix;
        if(StringUtils.isBlank(branchPrefix)){
            this.branchPrefix = "Tcc-branch-";
        }
    }

    /**
     * 开启Tcc事务
     */
    public DtmResponse prepare(DtmOperator<Tcc> operator) throws Exception {
        // 检查设置Gid
        if (StringUtils.isEmpty(this.getGid())) {
            HttpResponse<DtmResponse> httpResponse = dtmService.newGid();
            if(httpResponse.isFailed()){
                throw new DtmException("DTM Tcc, acquire gid failed");
            }
            this.setGid(parseGid(Objects.requireNonNull(httpResponse.getBody())));
        }

        // prepare
        DtmParam tccParam = new DtmParam(this.getGid(), Type.TCC);
        HttpResponse<DtmResponse> prepareResponse = dtmService.prepare(tccParam);
        if(prepareResponse.isFailed()){
            throw new DtmException("DTM Tcc " + this.getGid() + " prepare failed, " + prepareResponse.getMessage());
        }

        DtmResponse prepareDtm = prepareResponse.getBody();
        if(!DtmResponse.SUCCESS.equals(prepareDtm.getDtmResult())){
            throw new DtmException("DTM Tcc " + this.getGid() + " submit failed");
        }

        log.info("DTM Tcc " + this.getGid() + " start transaction");
        operator.accept(this);

        HttpResponse<DtmResponse> submitResponse = dtmService.submit(tccParam);
        if(submitResponse.isFailed()){
            dtmService.abort(tccParam);
            throw new DtmException("DTM Tcc " + this.getGid() + " submit failed, " + submitResponse.getMessage());
        }

        DtmResponse response = submitResponse.getBody();
        response.setGid(this.getGid());
        return response;
    }

    public String branch(String tryUrl, String confirmUrl, String cancelUrl, Object requestBody) throws Exception {
        String branchId = genBranchId();
        TccParam operatorParam = new TccParam(
                this.getGid(),
                Type.TCC,
                branchId,
                confirmUrl,
                cancelUrl,
                toJson(requestBody),
                "prepared"
        );

        HttpResponse<DtmResponse> registerResponse = dtmService.registerBranch(operatorParam);
        if(registerResponse.isFailed()){
            throw new DtmException("DTM Tcc " + this.getGid() + " branch failed, " + registerResponse.getMessage());
        }

        DtmResponse dtmResponse = registerResponse.getBody();
        if (!DtmResponse.SUCCESS.equals(dtmResponse.getDtmResult())) {
            throw new DtmException("DTM Tcc " + this.getGid() + " branch failed");
        }

        Map<String, String> branchParam = new HashMap<>();
        branchParam.put("gid", this.getGid());
        branchParam.put("branch_id", branchId);
        branchParam.put("op", OP);
        branchParam.put("trans_type", Type.TCC.getValue());
        HttpResponse<String> branchResponse = dtmService.businessPost(tryUrl, branchParam, requestBody);
        if(branchResponse.getStatusCodeValue() >= 400){ // 200 ~ 300的状态都是有意义的
            throw new DtmException("DTM Tcc " + this.getGid() + " branch failed, " + branchResponse.getMessage());
        }
        return branchResponse.getBody();
    }

    private String genBranchId() throws Exception {
        if (this.subBranchId >= MAX_BRANCH_ID) {
            throw new Exception("branch id is larger than 99");
        }
        this.subBranchId++;
        return this.branchPrefix + String.format("%02d", this.subBranchId);
    }
}
