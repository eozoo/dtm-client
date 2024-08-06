/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.example.dtm.controller;

import com.cowave.commons.dtm.impl.Barrier;
import com.cowave.commons.dtm.impl.BarrierParam;
import com.cowave.commons.dtm.DtmResult;
import com.cowave.example.dtm.model.TransReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.feign.codec.HttpResponse;
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
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transOutPrepare(transReq));
        return DtmResult.httpSuccess();
    }

    @RequestMapping("barrierTransOutConfirm")
    public HttpResponse<DtmResult> transOutConfirm(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transOutSubmit(transReq));
        return DtmResult.httpSuccess();
    }

    @RequestMapping("barrierTransOutCancel")
    public HttpResponse<DtmResult> transOutCancel(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transOutCancel(transReq));
        return DtmResult.httpSuccess();
    }

    @RequestMapping("barrierTransInTry")
    public HttpResponse<DtmResult> transInTry(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transInPrepare(transReq));
        return DtmResult.httpSuccess();
    }

    @RequestMapping("barrierTransInConfirm")
    public HttpResponse<DtmResult> transInConfirm(BarrierParam barrierParam, @RequestBody TransReq transReq) {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        try{
            branchBarrier.call((barrier) -> this.transInSubmit(transReq));
        }catch (Exception e){
            return DtmResult.httpFailure(); // 不再重试
        }
        return DtmResult.httpSuccess();
    }

    @RequestMapping("barrierTransInCancel")
    public HttpResponse<DtmResult> transInCancel(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transInCancel(transReq));
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
