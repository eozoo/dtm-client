/*
 * Copyright (c) 2017～2024 Cowave All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
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

    public boolean tccBranch(Tcc tcc) throws Exception {
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
        return false;
    }

    @RequestMapping("tcc/barrier")
    public DtmResult tccBarrier() throws Exception {
        return dtmClient.tcc(UUID.randomUUID().toString(), this::barrierBranch);
    }

    public boolean barrierBranch(Tcc tcc) throws Exception {
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
        return true;
    }

    @RequestMapping("tcc/barrier/error")
    public DtmResult tccBarrierError() throws Exception {
        return dtmClient.tcc(UUID.randomUUID().toString(), this::barrierBranchError);
    }

    public boolean barrierBranchError(Tcc tcc) throws Exception {
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
        return true;
    }
}
