package com.cowave.zoo.dtm.client;

import com.cowave.zoo.dtm.client.impl.*;
import com.cowave.zoo.dtm.client.impl.sql.SqlProvider;
import com.cowave.zoo.http.client.response.HttpResponse;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 *
 * @author shanhuiming
 *
 */
@RequiredArgsConstructor
public class DtmClient {
    private final DtmService dtmService;
    private final DataSource dataSource;
    private final SqlProvider sqlProvider;

    /**
     * saga
     */
    public Saga saga() {
        return new Saga(null, dtmService);
    }

    /**
     * 创建saga，指定gid
     */
    public Saga saga(String gid) {
        return new Saga(gid, dtmService);
    }

    /**
     * 创建tcc
     */
    public HttpResponse<DtmResponse> tcc(TccOperator<Tcc> operator) throws Exception {
        return new Tcc("",null, dtmService).submit(operator);
    }

    /**
     * 创建tcc，指定gid
     */
    public HttpResponse<DtmResponse> tcc(String gid, TccOperator<Tcc> operator) throws Exception {
        return new Tcc("", gid, dtmService).submit(operator);
    }

    /**
     * 创建tcc
     */
    public HttpResponse<DtmResponse> tcc(String gid, TccOperator<Tcc> operator, String branchPrefix) throws Exception {
        return new Tcc(branchPrefix,null, dtmService).submit(operator);
    }

    /**
     * barrier操作
     */
    public HttpResponse<DtmResponse> barrier(BarrierParam barrierParam, BarrierOperator<Barrier> operator) throws Exception {
        return new Barrier(barrierParam, sqlProvider).call(operator, dataSource);
    }

    /**
     * barrier操作，指定DataSource
     */
    public HttpResponse<DtmResponse> barrier(BarrierParam barrierParam, BarrierOperator<Barrier> operator, DataSource dataSource) throws Exception {
        return new Barrier(barrierParam, sqlProvider).call(operator, dataSource);
    }

    /**
     * barrier操作，指定Connection
     */
    public HttpResponse<DtmResponse> barrier(BarrierParam barrierParam, BarrierOperator<Barrier> operator, Connection connection) throws Exception {
        return new Barrier(barrierParam, sqlProvider).call(operator, connection);
    }

    /**
     * barrier操作，在已有事务中执行
     */
    public HttpResponse<DtmResponse> barrierInTransaction(BarrierParam barrierParam, BarrierOperator<Barrier> operator, Connection connection) {
        return new Barrier(barrierParam, sqlProvider).callInTransaction(operator, connection);
    }
}
