/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm;

import com.cowave.commons.dtm.impl.Saga;
import com.cowave.commons.dtm.impl.Tcc;
import com.cowave.commons.dtm.model.DtmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@RequiredArgsConstructor
public class DtmClient {

    private final DtmService dtmService;

    private final DtmProperties dtmProperties;

    /**
     * 创建saga
     */
    public Saga saga() {
        return new Saga(null, dtmService, dtmProperties);
    }

    /**
     * 创建saga，并指定gid
     */
    public Saga saga(String gid) {
        return new Saga(gid, dtmService, dtmProperties);
    }

    /**
     * 创建tcc
     */
    public DtmResponse tcc(String branchPrefix, DtmOperator<Tcc> function) throws Exception {
        Tcc tcc = new Tcc(branchPrefix,null, dtmService, dtmProperties);
        return tcc.prepare(function);
    }

    /**
     * 创建tcc，并指定gid
     */
    public DtmResponse tcc(String branchPrefix, String gid, DtmOperator<Tcc> function) throws Exception {
        Tcc tcc = new Tcc(branchPrefix, gid, dtmService, dtmProperties);
        return tcc.prepare(function);
    }
}
