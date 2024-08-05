/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author shanhuiming
 *
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TccParam extends DtmParam {

    /**
     * branch id
     */
    @JsonProperty("branch_id")
    private String branchId;

    /**
     * status
     */
    @JsonProperty("status")
    private String status;

    /**
     * data
     */
    @JsonProperty("data")
    private String data;

    /**
     * branch confirm uri
     */
    @JsonProperty("confirm")
    private String confirm;

    /**
     * branch cancel uri
     */
    @JsonProperty("cancel")
    private String cancel;


    public TccParam(String gid, DtmTransaction.Type type, String branchId,
                    String confirm, String cancel, String data, String status) {
        super(gid, type);
        setSubType(type.getValue());
        this.branchId = branchId;
        this.confirm = confirm;
        this.cancel = cancel;
        this.data = data;
        this.status = status;
    }
}
