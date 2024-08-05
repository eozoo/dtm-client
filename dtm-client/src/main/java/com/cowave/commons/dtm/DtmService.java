/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm;

import com.cowave.commons.dtm.impl.DtmParam;
import com.cowave.commons.dtm.model.DtmResponse;
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
@FeignClient(url = "${spring.application.dtm.address}")
public interface DtmService {

    @RequestLine("GET /api/dtmsvr/newGid")
    HttpResponse<DtmResponse> newGid();

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/dtmsvr/submit")
    HttpResponse<DtmResponse> submit(DtmParam dtmParam);

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/dtmsvr/prepare")
    HttpResponse<DtmResponse> prepare(DtmParam dtmParam);

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/dtmsvr/abort")
    HttpResponse<DtmResponse> abort(DtmParam dtmParam);

    @Headers("Content-Type: application/json")
    @RequestLine("POST /api/dtmsvr/registerBranch")
    HttpResponse<DtmResponse> registerBranch(DtmParam dtmParam);

    @Headers("Content-Type: application/json")
    @RequestLine("POST ")
    HttpResponse<String> businessPost(@Host String host, @QueryMap Map<String, String> queryMap, Object body);

    @RequestLine("GET {path}")
    Response busiPost(@Host String host, @Param("path") String path, @QueryMap Map<String, Object> queryMap, Object body);
}
