/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.example.dtm.controller;

import com.cowave.commons.dtm.model.DtmResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    // saga
    @RequestMapping("TransOut")
    public DtmResponse transOut() {
        return DtmResponse.success();
    }

    // saga
    @RequestMapping("TransIn")
    public DtmResponse transIn() {
        return DtmResponse.success();
    }

    // saga
    @RequestMapping("TransOutCompensate")
    public DtmResponse transOutCompensate() {
        return DtmResponse.success();
    }

    // saga
    @RequestMapping("TransInCompensate")
    public DtmResponse transInCompensate() {
        return DtmResponse.success();
    }

    // tcc
    @RequestMapping("TransOutTry")
    public String transOutTry() {
        return "succ";
    }

    // tcc
    @RequestMapping("TransOutConfirm")
    public DtmResponse transOutConfirm() {
        return DtmResponse.success();
    }

    // tcc
    @RequestMapping("TransOutCancel")
    public DtmResponse transOutCancel() {
        return DtmResponse.success();
    }

    // tcc
    @RequestMapping("TransInTry")
    public DtmResponse transInTry() {
        return DtmResponse.success();
    }

    // tcc
    @RequestMapping("TransInConfirm")
    public DtmResponse transInConfirm() {
        return DtmResponse.success();
    }

    // tcc
    @RequestMapping("TransInCancel")
    public DtmResponse transInCancel() {
        return DtmResponse.success();
    }
}
