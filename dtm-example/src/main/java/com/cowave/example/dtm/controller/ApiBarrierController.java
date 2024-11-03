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

import com.cowave.commons.dtm.impl.Barrier;
import com.cowave.commons.dtm.impl.BarrierParam;
import com.cowave.commons.dtm.DtmResult;
import com.cowave.commons.response.HttpResponse;
import com.cowave.example.dtm.model.TransReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiBarrierController {

    private final DataSource dataSource;

    @RequestMapping("barrierTransOutTry")
    public HttpResponse<DtmResult> transOutTry(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        new Barrier(barrierParam).call((barrier) -> this.transOutPrepare(transReq), dataSource);
        return DtmResult.httpSuccess();
    }

    @RequestMapping("barrierTransOutConfirm")
    public HttpResponse<DtmResult> transOutConfirm(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        new Barrier(barrierParam).call((barrier) -> this.transOutSubmit(transReq), dataSource);
        return DtmResult.httpSuccess();
    }

    @RequestMapping("barrierTransOutCancel")
    public HttpResponse<DtmResult> transOutCancel(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        new Barrier(barrierParam).call((barrier) -> this.transOutCancel(transReq), dataSource);
        return DtmResult.httpSuccess();
    }

    @RequestMapping("barrierTransInTry")
    public HttpResponse<DtmResult> transInTry(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam);
        branchBarrier.call((barrier) -> this.transInPrepare(transReq), dataSource);
        return DtmResult.httpSuccess();
    }

    @RequestMapping("barrierTransInConfirm")
    public HttpResponse<DtmResult> transInConfirm(BarrierParam barrierParam, @RequestBody TransReq transReq) {
        try{
            new Barrier(barrierParam).call((barrier) -> this.transInSubmit(transReq), dataSource);
        }catch (Exception e){
            return DtmResult.httpFailure(); // 不再重试
        }
        return DtmResult.httpSuccess();
    }

    @RequestMapping("barrierTransInCancel")
    public HttpResponse<DtmResult> transInCancel(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        new Barrier(barrierParam).call((barrier) -> this.transInCancel(transReq), dataSource);
        return DtmResult.httpSuccess();
    }

    private boolean transOutPrepare(TransReq transReq) {
        log.info("user[{}] 转出准备 {}", transReq.getUserId(), transReq.getAmount());
        return true;
    }

    public boolean transOutSubmit(TransReq transReq) {
        log.info("user[{}] 转出提交 {}", transReq.getUserId(), transReq.getAmount());
        return true;
    }

    public boolean transOutCancel(TransReq transReq) {
        log.info("user[{}] 转出回滚 {}", transReq.getUserId(), transReq.getAmount());
        return true;
    }

    private boolean transInPrepare(TransReq transReq) {
        log.info("user[{}] 转入准备 {}", transReq.getUserId(), transReq.getAmount());
        return true;
    }

    private boolean transInSubmit(TransReq transReq) {
        log.info("user[{}] 转入提交 {}", transReq.getUserId(), transReq.getAmount());
        if(transReq.getAmount() > 1000){
            return false;
        }
        return true;
    }

    private boolean transInCancel(TransReq transReq) {
        log.info("user[{}] 转入回滚 {}", transReq.getUserId(), transReq.getAmount());
        return true;
    }
}
