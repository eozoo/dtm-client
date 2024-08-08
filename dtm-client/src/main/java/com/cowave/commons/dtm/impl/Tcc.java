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
import com.cowave.commons.dtm.DtmResult;
import com.cowave.commons.tools.HttpAsserts;
import com.cowave.commons.tools.HttpException;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.feign.codec.HttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@NoArgsConstructor
public class Tcc extends DtmTransaction {

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
    public DtmResult prepare(DtmOperator<Tcc> operator) throws Exception {
        // gid
        if (StringUtils.isEmpty(this.getGid())) {
            HttpResponse<DtmResult> gidResponse = dtmService.newGid();
            HttpAsserts.isTrue(gidResponse.isSuccess(),
                    gidResponse.getStatusCodeValue(), DtmResult.ERROR, "DTM Tcc acquire gid failed, " + gidResponse.getMessage());

            DtmResult gidResult = gidResponse.getBody();
            HttpAsserts.isTrue(gidResult != null && gidResult.dtmSuccess(),
                    DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Tcc acquire gid failed");

            this.setGid(gidResult.getGid());
        }

        // prepare
        DtmParam tccParam = new DtmParam(this.getGid(), Type.TCC);
        HttpResponse<DtmResult> prepareResponse = dtmService.prepare(tccParam);
        HttpAsserts.isTrue(prepareResponse.isSuccess(),
                prepareResponse.getStatusCodeValue(), DtmResult.ERROR, "DTM Tcc " + this.getGid() + " prepare failed, " + prepareResponse.getMessage());

        DtmResult prepareResult = prepareResponse.getBody();
        HttpAsserts.isTrue(prepareResult != null && prepareResult.dtmSuccess(),
                DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Tcc " + this.getGid() + " prepare failed");

        // operate
        if(!operator.accept(this)){
            throw new HttpException(DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Tcc " + this.getGid() + " register failed");
        }
        log.info("DTM Tcc " + this.getGid() + " register transaction");

        // submit
        HttpResponse<DtmResult> submitResponse = dtmService.submit(tccParam);
        if(submitResponse.isFailed()){
            dtmService.abort(tccParam);
            throw new HttpException(submitResponse.getStatusCodeValue(), DtmResult.ERROR, "DTM Tcc " + this.getGid() + " submit failed, " + submitResponse.getMessage());
        }

        DtmResult submitResult = submitResponse.getBody();
        HttpAsserts.isTrue(submitResult != null && submitResult.dtmSuccess(),
                DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Tcc " + this.getGid() + " submit failed");

        submitResult.setGid(this.getGid());
        return submitResult;
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

        // register
        HttpResponse<DtmResult> registerResponse = dtmService.registerBranch(operatorParam);
        HttpAsserts.isTrue(registerResponse.isSuccess(),
                registerResponse.getStatusCodeValue(), DtmResult.ERROR, "DTM Tcc " + this.getGid() + " branch register failed, " + registerResponse.getMessage());

        DtmResult registerResult = registerResponse.getBody();
        HttpAsserts.isTrue(registerResult != null && registerResult.dtmSuccess(),
                DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Tcc " + this.getGid() + " branch register failed");

        // try
        Map<String, String> branchParam = new HashMap<>();
        branchParam.put("gid", this.getGid());
        branchParam.put("branch_id", branchId);
        branchParam.put("op", "try");
        branchParam.put("trans_type", Type.TCC.getValue());
        HttpResponse<String> httpResponse = dtmService.businessPost(tryUrl, branchParam, requestBody);

        HttpAsserts.isTrue(httpResponse.getStatusCodeValue() < 400,
                httpResponse.getStatusCodeValue(), DtmResult.ERROR, "DTM Tcc " + this.getGid() + " branch try failed, " + httpResponse.getMessage());
        return httpResponse.getBody();
    }

    private String genBranchId() throws Exception {
        if (this.subBranchId >= MAX_BRANCH_ID) {
            throw new Exception("branch id is larger than 99");
        }
        this.subBranchId++;
        return this.branchPrefix + String.format("%02d", this.subBranchId);
    }
}
