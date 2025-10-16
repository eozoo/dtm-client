package com.cowave.zoo.dtm.client.saga;

import com.cowave.zoo.dtm.client.DtmClient;
import com.cowave.zoo.dtm.client.DtmResponse;
import com.cowave.zoo.dtm.client.impl.BarrierParam;
import com.cowave.zoo.http.client.asserts.Asserts;
import com.cowave.zoo.http.client.response.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

import static com.cowave.zoo.http.client.constants.HttpHeader.X_Request_ID;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/saga/barrier")
public class SagaBarrierController {

    private final DtmClient dtmClient;

    private final DataSource dataSource;

    @RequestMapping("/transOut")
    public HttpResponse<DtmResponse> transOut(
            @RequestHeader(X_Request_ID) String requestId,
            @RequestBody SagaModel sagaModel, BarrierParam barrierParam) throws Exception {
        Thread.sleep(4500);
        return dtmClient.barrier(barrierParam, op -> this.barrierTransOut(requestId, sagaModel));
    }

    @RequestMapping("/transIn")
    public HttpResponse<DtmResponse> transIn(
            @RequestHeader(X_Request_ID) String requestId,
            @RequestBody SagaModel sagaModel, BarrierParam barrierParam) throws Exception {
        return dtmClient.barrier(barrierParam, op -> this.barrierTransIn(requestId, sagaModel), dataSource.getConnection());
    }

    @RequestMapping("/transOutCompensate")
    public HttpResponse<DtmResponse> transOutCompensate(
            @RequestHeader(X_Request_ID) String requestId,
            @RequestBody SagaModel sagaModel, BarrierParam barrierParam) throws Exception {
        return dtmClient.barrier(barrierParam, op -> this.barrierTransOutCompensate(requestId, sagaModel));
    }

    @RequestMapping("/transInCompensate")
    public HttpResponse<DtmResponse> transInCompensate(
            @RequestHeader(X_Request_ID) String requestId,
            @RequestBody SagaModel sagaModel, BarrierParam barrierParam) throws Exception {
        return dtmClient.barrier(barrierParam, op -> this.barrierTransInCompensate(requestId, sagaModel));
    }

    private boolean barrierTransOut(String requestId, SagaModel sagaModel){
        log.info(requestId + " " + sagaModel.getUserId() + " transOut " + sagaModel.getAmount());
        return true;
    }

    private boolean barrierTransIn(String requestId, SagaModel sagaModel){
        log.info(requestId + " " + sagaModel.getUserId() + " transIn " + sagaModel.getAmount());
        // requestId不对，返回5xx，进行重试
        Asserts.equals("T12345", requestId, "requestId not correct");
        // 金额超过1000，直接409失败并执行补偿操作，不再重试
        return sagaModel.getAmount() <= 1000;
    }

    private boolean barrierTransOutCompensate(String requestId, SagaModel sagaModel){
        log.info(requestId + " " + sagaModel.getUserId() + " transOut compensate " + sagaModel.getAmount());
        return true;
    }

    private boolean barrierTransInCompensate(String requestId, SagaModel sagaModel){
        log.info(requestId + " " + sagaModel.getUserId() + " transIn compensate " + sagaModel.getAmount());
        return true;
    }
}
