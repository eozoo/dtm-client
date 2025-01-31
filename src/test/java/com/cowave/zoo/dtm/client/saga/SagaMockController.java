package com.cowave.zoo.dtm.client.saga;

import com.cowave.zoo.dtm.client.DtmClient;
import com.cowave.zoo.dtm.client.DtmResponse;
import com.cowave.zoo.dtm.client.impl.Saga;
import com.cowave.zoo.http.client.response.HttpResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.cowave.zoo.http.client.constants.HttpHeader.X_Request_ID;

/**
 *
 * @author shanhuiming
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(("/api/saga"))
public class SagaMockController {

    private final DtmClient dtmClient;

    @RequestMapping("/mock1")
    public HttpResponse<DtmResponse> mock1() {
        SagaModel sagaModel = new SagaModel("shanhm", 1000);
        Saga saga = dtmClient.saga()
                .branchHeader(X_Request_ID, "T12345")
                .waitResult(true)
                .concurrent(false)
                .timeoutFail(8000)
                .requestTimeout(8000)
                .retryInterval(8000);
        saga.step("http://localhost:8081/api/saga/barrier/transOut", "", sagaModel);
        saga.step("http://localhost:8081/api/saga/barrier/transIn", "", sagaModel);
        return saga.submit();
    }

    @RequestMapping("/mock2")
    public HttpResponse<DtmResponse> mock2() {
        SagaModel sagaModel = new SagaModel("shanhm", 1000);
        Saga saga = dtmClient.saga(UUID.randomUUID().toString())
                .branchHeader(X_Request_ID, "T23456")
                .waitResult(true);
        saga.step("http://localhost:8081/api/saga/barrier/transOut", "", sagaModel);
        saga.step("http://localhost:8081/api/saga/barrier/transIn", "", sagaModel);
        return saga.submit();
    }

    @RequestMapping("/mock3")
    public DtmResponse mock3() {
        SagaModel sagaModel = new SagaModel("shanhm", 10000);
        Saga saga = dtmClient.saga(UUID.randomUUID().toString())
                .branchHeader(X_Request_ID, "T12345")
                .waitResult(true);
        saga.step("http://localhost:8081/api/saga/barrier/transOut",
                "http://localhost:8081/api/saga/barrier/transOutCompensate", sagaModel);
        saga.step("http://localhost:8081/api/saga/barrier/transIn",
                "http://localhost:8081/api/saga/barrier/transInCompensate", sagaModel);
        // Dtm的响应会嵌套，这里按照格式将原本响应拆出来
        return DtmResponse.parseDtmResponse(saga.submit(), DtmResponse.class);
    }
}
