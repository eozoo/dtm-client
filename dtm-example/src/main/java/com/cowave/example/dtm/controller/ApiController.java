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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    // saga
    @RequestMapping("TransOut")
    public DtmResult transOut() {
        return DtmResult.success();
    }

    // saga
    @RequestMapping("TransIn")
    public DtmResult transIn() {
        return DtmResult.success();
    }

    // saga
    @RequestMapping("TransOutCompensate")
    public DtmResult transOutCompensate() {
        return DtmResult.success();
    }

    // saga
    @RequestMapping("TransInCompensate")
    public DtmResult transInCompensate() {
        return DtmResult.success();
    }

    // tcc
    @RequestMapping("TransOutTry")
    public String transOutTry() {
        return "succ";
    }

    // tcc
    @RequestMapping("TransOutConfirm")
    public DtmResult transOutConfirm() {
        return DtmResult.success();
    }

    // tcc
    @RequestMapping("TransOutCancel")
    public DtmResult transOutCancel() {
        return DtmResult.success();
    }

    // tcc
    @RequestMapping("TransInTry")
    public DtmResult transInTry() {
        return DtmResult.success();
    }

    // tcc
    @RequestMapping("TransInConfirm")
    public DtmResult transInConfirm() {
        return DtmResult.success();
    }

    // tcc
    @RequestMapping("TransInCancel")
    public DtmResult transInCancel() {
        return DtmResult.success();
    }
}
