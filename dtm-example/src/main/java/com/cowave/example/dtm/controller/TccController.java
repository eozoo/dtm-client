/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.example.dtm.controller;

import com.cowave.commons.dtm.DtmClient;
import com.cowave.commons.dtm.impl.Tcc;
import com.cowave.commons.dtm.DtmResult;
import com.cowave.example.dtm.model.TransReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(("/api"))
@RestController
public class TccController {

    private final DtmClient dtmClient;

    @RequestMapping("/tcc")
    public DtmResult tcc() throws Exception {
        return dtmClient.tcc(UUID.randomUUID().toString(), this::tccBranch);
    }

    public void tccBranch(Tcc tcc) throws Exception {
        String outResponse = tcc.branch(
                "http://localhost:8081/api/TransOutTry",
                "http://localhost:8081/api/TransOutConfirm",
                "http://localhost:8081/api/TransOutCancel",
                "");
        log.info("tcc branch out: " + outResponse);

        String inResponse = tcc.branch(
                "http://localhost:8081/api/TransInTry",
                "http://localhost:8081/api/TransInConfirm",
                "http://localhost:8081/api/TransInCancel",
                "");
        log.info("tcc branch in: " + inResponse);
    }

    @RequestMapping("tcc/barrier")
    public DtmResult tccBarrier() throws Exception {
        return dtmClient.tcc(UUID.randomUUID().toString(), this::barrierBranch);
    }

    public void barrierBranch(Tcc tcc) throws Exception {
        String outResponse = tcc.branch(
                "http://localhost:8081/api/barrierTransOutTry",
                "http://localhost:8081/api/barrierTransOutConfirm",
                "http://localhost:8081/api/barrierTransOutCancel",
                new TransReq(1, -30));
        log.info("tcc branch out: " + outResponse);

        String inResponse = tcc.branch(
                "http://localhost:8081/api/barrierTransInTry",
                "http://localhost:8081/api/barrierTransInConfirm",
                "http://localhost:8081/api/barrierTransInCancel",
                new TransReq(2, 30));
        log.info("tcc branch in: " + inResponse);
    }

    @RequestMapping("tcc/barrier/error")
    public DtmResult tccBarrierError() throws Exception {
        return dtmClient.tcc(UUID.randomUUID().toString(), TccController::barrierBranchError);
    }

    public static void barrierBranchError(Tcc tcc) throws Exception {
        String outResponse = tcc.branch(
                "http://localhost:8081/api/barrierTransOutTry",
                "http://localhost:8081/api/barrierTransOutConfirm",
                "http://localhost:8081/api/barrierTransOutCancel",
                new TransReq(1, -100000));
        log.info("tcc branch out: " + outResponse);

        String inResponse = tcc.branch(
                "http://localhost:8081/api/barrierTransInTry",
                "http://localhost:8081/api/barrierTransInConfirm",
                "http://localhost:8081/api/barrierTransInCancel",
                new TransReq(2, 100000));
        log.info("tcc branch in: " + inResponse);
    }
}
