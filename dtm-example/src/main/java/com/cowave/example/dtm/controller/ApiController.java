/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.example.dtm.controller;

import com.cowave.commons.dtm.DtmResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.feign.codec.HttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    // saga
    @RequestMapping("TransOut")
    public HttpResponse<DtmResult> transOut() {
        return DtmResult.httpSuccess();
    }

    // saga
    @RequestMapping("TransIn")
    public HttpResponse<DtmResult> transIn() {
        return DtmResult.httpSuccess();
    }

    // saga
    @RequestMapping("TransOutCompensate")
    public HttpResponse<DtmResult> transOutCompensate() {
        return DtmResult.httpSuccess();
    }

    // saga
    @RequestMapping("TransInCompensate")
    public HttpResponse<DtmResult> transInCompensate() {
        return DtmResult.httpSuccess();
    }

    // tcc
    @RequestMapping("TransOutTry")
    public String transOutTry() {
        return "succ";
    }

    // tcc
    @RequestMapping("TransOutConfirm")
    public HttpResponse<DtmResult> transOutConfirm() {
        return DtmResult.httpSuccess();
    }

    // tcc
    @RequestMapping("TransOutCancel")
    public HttpResponse<DtmResult> transOutCancel() {
        return DtmResult.httpSuccess();
    }

    // tcc
    @RequestMapping("TransInTry")
    public HttpResponse<DtmResult> transInTry() {
        return DtmResult.httpSuccess();
    }

    // tcc
    @RequestMapping("TransInConfirm")
    public HttpResponse<DtmResult> transInConfirm() {
        return DtmResult.httpSuccess();
    }

    // tcc
    @RequestMapping("TransInCancel")
    public HttpResponse<DtmResult> transInCancel() {
        return DtmResult.httpSuccess();
    }
}
