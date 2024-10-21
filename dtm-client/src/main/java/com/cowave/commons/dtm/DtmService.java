/*
 * Copyright (c) 2017～2024 Cowave All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.cowave.commons.dtm;

import com.cowave.commons.dtm.impl.DtmParam;
import feign.*;
import org.springframework.feign.annotation.FeignClient;
import org.springframework.feign.annotation.Host;
import org.springframework.feign.codec.HttpResponse;

import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@FeignClient(url = "${spring.dtm.address}", readTimeoutMillisStr = "${spring.dtm.readTimeout:10000}", connectTimeoutMillisStr = "${spring.dtm.connectTimeout:60000}")
public interface DtmService {

    @RequestLine("GET /api/dtmsvr/newGid")
    HttpResponse<DtmResult> newGid();

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/dtmsvr/submit")
    HttpResponse<DtmResult> submit(DtmParam dtmParam);

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/dtmsvr/prepare")
    HttpResponse<DtmResult> prepare(DtmParam dtmParam);

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/dtmsvr/abort")
    HttpResponse<DtmResult> abort(DtmParam dtmParam);

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/dtmsvr/registerBranch")
    HttpResponse<DtmResult> registerBranch(DtmParam dtmParam);

    @Headers("Content-Type: application/json")
    @RequestLine("POST ")
    HttpResponse<String> businessPost(@Host String host, @QueryMap Map<String, String> queryMap, Object body);
}
