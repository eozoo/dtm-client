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
    public DtmResponse TransOut() {
        return DtmResponse.success();
    }

    // saga
    @RequestMapping("TransIn")
    public DtmResponse TransIn() {
        return DtmResponse.success();
    }

    // saga
    @RequestMapping("TransOutCompensate")
    public DtmResponse TransOutCompensate() {
        return DtmResponse.success();
    }

    // saga
    @RequestMapping("TransInCompensate")
    public DtmResponse TransInCompensate() {
        return DtmResponse.success();
    }

    // tcc
    @RequestMapping("TransOutTry")
    public String TransOutTry() {
        return "succ";
    }

    // tcc
    @RequestMapping("TransOutConfirm")
    public DtmResponse TransOutConfirm() {
        return DtmResponse.success();
    }

    // tcc
    @RequestMapping("TransOutCancel")
    public DtmResponse TransOutCancel() {
        return DtmResponse.success();
    }

    // tcc
    @RequestMapping("TransInTry")
    public DtmResponse TransInTry() {
        return DtmResponse.success();
    }

    // tcc
    @RequestMapping("TransInConfirm")
    public DtmResponse TransInConfirm() {
        return DtmResponse.success();
    }

    // tcc
    @RequestMapping("TransInCancel")
    public DtmResponse TransInCancel() {
        return DtmResponse.success();
    }
}
