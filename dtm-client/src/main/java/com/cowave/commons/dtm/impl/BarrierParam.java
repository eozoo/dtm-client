/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author shanhuiming
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarrierParam {

    /**
     * gid
     */
    private String gid;

    /**
     * branch id
     */
    private String branch_id;

    /**
     * trans type
     */
    private String trans_type;

    /**
     * operator
     */
    private String op;
}
