package com.cowave.zoo.dtm.client;

import com.cowave.zoo.dtm.client.impl.DtmGid;
import com.cowave.zoo.dtm.client.impl.DtmParam;
import com.cowave.zoo.http.client.annotation.*;
import com.cowave.zoo.http.client.response.HttpResponse;

import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
@HttpClient(url = "${spring.dtm.address}", readTimeoutStr = "${spring.dtm.readTimeout:10000}", connectTimeoutStr = "${spring.dtm.connectTimeout:60000}")
public interface DtmService {

    @HttpLine("GET /api/dtmsvr/newGid")
    HttpResponse<DtmGid> newGid();

    @HttpHeaders("Content-Type: application/json")
    @HttpLine("POST /api/dtmsvr/submit")
    HttpResponse<DtmResponse> submit(DtmParam dtmParam);

    @HttpHeaders("Content-Type: application/json")
    @HttpLine("POST /api/dtmsvr/prepare")
    HttpResponse<DtmResponse> prepare(DtmParam dtmParam);

    @HttpHeaders("Content-Type: application/json")
    @HttpLine("POST /api/dtmsvr/abort")
    HttpResponse<DtmResponse> abort(DtmParam dtmParam);

    @HttpHeaders("Content-Type: application/json")
    @HttpLine("POST /api/dtmsvr/registerBranch")
    HttpResponse<DtmResponse> registerBranch(DtmParam dtmParam);

    @HttpHeaders("Content-Type: application/json")
    @HttpLine("POST ")
    HttpResponse<String> businessPost(@HttpHost String host, @HttpParamMap Map<String, String> paramMap, Object body);
}
