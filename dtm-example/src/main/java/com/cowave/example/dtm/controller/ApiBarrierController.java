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
import com.cowave.commons.dtm.model.DtmResponse;
import com.cowave.commons.tools.Asserts;
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
    public DtmResponse transOutTry(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transOutPrepare(transReq));
        return DtmResponse.success();
    }

    @RequestMapping("barrierTransOutConfirm")
    public Object transOutConfirm(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transOutSubmit(transReq));
        return DtmResponse.success();
    }

    @RequestMapping("barrierTransOutCancel")
    public Object transOutCancel(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transOutCancel(transReq));
        return DtmResponse.success();
    }

    @RequestMapping("barrierTransInTry")
    public Object transInTry(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transInPrepare(transReq));
        return DtmResponse.success();
    }

    @RequestMapping("barrierTransInConfirm")
    public HttpResponse<String> transInConfirm(BarrierParam barrierParam, @RequestBody TransReq transReq) {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        try{
            branchBarrier.call((barrier) -> this.transInSubmit(transReq));
        }catch (Exception e){
            return new HttpResponse<>(409, null, null);
        }
        return HttpResponse.success();
    }

    @RequestMapping("barrierTransInCancel")
    public Object transInCancel(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transInCancel(transReq));
        return DtmResponse.success();
    }

    private void transOutPrepare(TransReq transReq) {
        log.info("user[{}] 转出准备 {}", transReq.getUserId(), transReq.getAmount());
    }

    public void transOutSubmit(TransReq transReq) {
        log.info("user[{}] 转出提交 {}", transReq.getUserId(), transReq.getAmount());
    }

    public void transOutCancel(TransReq transReq) {
        log.info("user[{}] 转出回滚 {}", transReq.getUserId(), transReq.getAmount());
    }

    private void transInPrepare(TransReq transReq) {
        log.info("user[{}] 转入准备 {}", transReq.getUserId(), transReq.getAmount());
    }

    private void transInSubmit(TransReq transReq) {
        Asserts.isTrue(transReq.getAmount() <= 1000, "金额过大");
        log.info("user[{}] 转入提交 {}", transReq.getUserId(), transReq.getAmount());
    }

    private void transInCancel(TransReq transReq) {
        log.info("user[{}] 转入回滚 {}", transReq.getUserId(), transReq.getAmount());
    }
}
