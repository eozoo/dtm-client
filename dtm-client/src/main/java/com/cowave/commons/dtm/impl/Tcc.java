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

    public Tcc(String branchPrefix, String gid, DtmService dtmService) {
        super(gid, Type.TCC, false);
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
