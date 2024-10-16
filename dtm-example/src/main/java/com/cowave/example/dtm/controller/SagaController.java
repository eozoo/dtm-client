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
import com.cowave.commons.dtm.impl.Saga;
import com.cowave.commons.dtm.DtmResult;
import com.cowave.commons.tools.HttpException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(("/api"))
public class SagaController {

    private final DtmClient dtmClient;

    @RequestMapping("/saga")
    public DtmResult saga() throws HttpException {
        Saga saga = dtmClient.saga(UUID.randomUUID().toString());
        saga.step("http://localhost:8081/api/TransOut", "http://localhost:8081/api/TransOutCompensate", "");
        saga.step("http://localhost:8081/api/TransIn", "http://localhost:8081/api/TransInCompensate", "");
        saga.enableWaitResult();
        return saga.submit();
    }
}
