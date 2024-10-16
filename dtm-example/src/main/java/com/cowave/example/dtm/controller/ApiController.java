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
