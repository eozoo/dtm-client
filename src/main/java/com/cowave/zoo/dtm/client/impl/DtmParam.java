package com.cowave.zoo.dtm.client.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author shanhuiming
 *
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "subType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TccParam.class, name = "tcc"),
        @JsonSubTypes.Type(value = SagaParam.class, name = "saga")
})
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DtmParam {

    /**
     * gid
     */
    @JsonProperty("gid")
    private String gid;

    /**
     * trans type string value
     */
    @JsonProperty("trans_type")
    private String transType;

    /**
     * this attribute just for mark different sub class for encode/decode.
     */
    @JsonProperty("subType")
    private String subType;

    public DtmParam(String gid, Dtm.Type type) {
        this.gid = gid;
        this.transType = type.getValue();
    }
}
