/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author shanhuiming
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DtmResponse {

    public static final String SUCCESS = "SUCCESS";

    public static final String FAILURE = "FAILURE";

    /**
     * gid
     */
    @JsonProperty("gid")
    private String gid;

    /**
     * dtm_result
     */
    @JsonProperty("dtm_result")
    private String dtmResult;

    public static DtmResponse success(){
        return  new DtmResponse(null, SUCCESS);
    }

    public static DtmResponse success(String gid){
        return  new DtmResponse(gid, SUCCESS);
    }

    public static DtmResponse failure(){
        return  new DtmResponse(null, FAILURE);
    }

    public static DtmResponse failure(String gid){
        return  new DtmResponse(gid, FAILURE);
    }
}
