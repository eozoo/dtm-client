package com.cowave.zoo.dtm.client.impl;

import com.cowave.zoo.dtm.client.DtmResponse;
import com.cowave.zoo.dtm.client.DtmService;
import com.cowave.zoo.http.client.asserts.Asserts;
import com.cowave.zoo.http.client.response.HttpResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@NoArgsConstructor
public class Tcc extends Dtm {

    private static final int MAX_BRANCH_ID = 99;

    private String branchPrefix;

    private int subBranchId;

    private DtmService dtmService;

    public Tcc(String branchPrefix, String gid, DtmService dtmService) {
        super(gid, Type.TCC, false);
        this.dtmService = dtmService;
        this.branchPrefix = branchPrefix;
        if(!StringUtils.hasText(branchPrefix)){
            this.branchPrefix = "Tcc-branch-";
        }
    }

    /**
     * 注册TCC分支
     */
    public void branch(String tryUrl, String confirmUrl, String cancelUrl, Object requestBody) throws Exception {
        String branchId = genBranchId();
        TccParam operatorParam = new TccParam(
                this.gid,
                Type.TCC,
                branchId,
                confirmUrl,
                cancelUrl,
                toJson(requestBody),
                "prepared"
        );

        // register
        HttpResponse<DtmResponse> httpResponse = dtmService.registerBranch(operatorParam);
        Asserts.isTrue(httpResponse.isSuccess(),
                "Tcc-" + gid + " branch register failed, " + httpResponse.getMessage());

        DtmResponse dtmResponse = httpResponse.getBody();
        Asserts.isTrue(dtmResponse != null && dtmResponse.dtmSuccess(),
                "Tcc-" + gid + " branch register failed");

        // try
        Map<String, String> branchParam = new HashMap<>();
        branchParam.put("gid", this.gid);
        branchParam.put("branch_id", branchId);
        branchParam.put("op", "try");
        branchParam.put("trans_type", Type.TCC.getValue());
        HttpResponse<String> tryResponse = dtmService.businessPost(tryUrl, branchParam, requestBody);
        Asserts.isTrue(tryResponse.getStatusCodeValue() < 400,
                "Tcc-" + gid + " branch try failed, " + tryResponse.getMessage());
    }

    /**
     * 提交Tcc事务
     */
    public HttpResponse<DtmResponse> submit(TccOperator<Tcc> operator) throws Exception {
        // gid
        if (StringUtils.isEmpty(gid)) {
            HttpResponse<DtmGid> httpResponse = dtmService.newGid();
            Asserts.isTrue(httpResponse.isSuccess(), "Tcc acquire gid failed, " + httpResponse.getMessage());

            DtmGid dtmGid = httpResponse.getBody();
            Asserts.isTrue(dtmGid != null && dtmGid.dtmSuccess(), "DTM Tcc acquire gid failed");
            this.gid = dtmGid.getGid();
        }

        // prepare
        DtmParam tccParam = new DtmParam(gid, Type.TCC);
        HttpResponse<DtmResponse> prepareResponse = dtmService.prepare(tccParam);
        Asserts.isTrue(prepareResponse.isSuccess(),
                "Tcc-" + gid + " prepare failed, " + prepareResponse.getMessage());

        DtmResponse prepareResult = prepareResponse.getBody();
        Asserts.isTrue(prepareResult != null && prepareResult.dtmSuccess(),
                "Tcc-" + gid + " prepare failed");

        try{
            operator.accept(this);
            return dtmService.submit(tccParam);
        }catch (Exception e){
            log.error("Tcc-" + gid + " submit failed", e);
            return dtmService.abort(tccParam);
        }
    }

    private String genBranchId() throws Exception {
        if (this.subBranchId >= MAX_BRANCH_ID) {
            throw new Exception("branch id is larger than 99");
        }
        this.subBranchId++;
        return this.branchPrefix + String.format("%02d", this.subBranchId);
    }
}
