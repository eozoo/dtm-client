/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.feign.codec.HttpResponse;

/**
 *
 * @author shanhuiming
 *
 */
@Data
@NoArgsConstructor
public class DtmResult {

    public static final String SUCCESS = "SUCCESS";

    public static final String FAILURE = "FAILURE";

    public static final String ONGOING = "ONGOING";

    public static final String ERROR = "ERROR";

    /**
     * dtm_result
     */
    @JsonProperty("dtm_result")
    private String dtmResult;

    /**
     * gid
     */
    @JsonProperty("gid")
    private String gid;

    /**
     * detail
     */
    @JsonProperty("detail")
    private String detail;

    public DtmResult(String dtmResult, String detail){
        this.dtmResult = dtmResult;
        this.detail = detail;
    }

    public boolean dtmSuccess(){
        return SUCCESS.equals(dtmResult);
    }

    public static DtmResult success(){
        return new DtmResult(SUCCESS, null);
    }

    public static DtmResult success(String detail){
        return  new DtmResult(SUCCESS, detail);
    }

    public static HttpResponse<DtmResult> httpSuccess(){
        return new HttpResponse<>(200, null, success());
    }

    public static HttpResponse<DtmResult> httpSuccess(String detail){
        return new HttpResponse<>(200, null, success(detail), detail);
    }

    public static DtmResult failure(){
        return  new DtmResult(FAILURE, null);
    }

    public static DtmResult failure(String detail){
        return  new DtmResult(FAILURE, detail);
    }

    public static HttpResponse<DtmResult> httpFailure(){
        return new HttpResponse<>(409, null, failure());
    }

    public static HttpResponse<DtmResult> httpFailure(String detail){
        return new HttpResponse<>(409, null, failure(detail), detail);
    }

    public static DtmResult ongoing(){
        return new DtmResult(ONGOING, null);
    }

    public static DtmResult ongoing(String detail){
        return new DtmResult(ONGOING, detail);
    }

    public static HttpResponse<DtmResult> httpOngoing(){
        return new HttpResponse<>(425, null, ongoing());
    }

    public static HttpResponse<DtmResult> httpOngoing(String detail){
        return new HttpResponse<>(425, null, ongoing(detail), detail);
    }

    public static DtmResult error(){
        return new DtmResult(ERROR, null);
    }

    public static DtmResult error(String detail){
        return new DtmResult(ERROR, detail);
    }

    public static HttpResponse<DtmResult> httpError(){
        return new HttpResponse<>(500, null, error());
    }

    public static HttpResponse<DtmResult> httpError(String detail){
        return new HttpResponse<>(500, null, error(detail), detail);
    }
}
