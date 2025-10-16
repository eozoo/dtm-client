package com.cowave.zoo.dtm.client.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 *
 * @author shanhuiming
 *
 */
@Getter
public class TccParam extends DtmParam {

    /**
     * branch id
     */
    @JsonProperty("branch_id")
    private final String branchId;

    /**
     * status
     */
    @JsonProperty("status")
    private final String status;

    /**
     * data
     */
    @JsonProperty("data")
    private final String data;

    /**
     * branch confirm uri
     */
    @JsonProperty("confirm")
    private final String confirm;

    /**
     * branch cancel uri
     */
    @JsonProperty("cancel")
    private final String cancel;

    public TccParam(String gid, Dtm.Type type, String branchId,
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
