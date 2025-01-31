package com.cowave.zoo.dtm.client.impl;

import com.cowave.zoo.dtm.client.DtmResponse;
import com.cowave.zoo.dtm.client.impl.sql.SqlProvider;
import com.cowave.zoo.http.client.asserts.HttpException;
import com.cowave.zoo.http.client.asserts.I18Messages;
import com.cowave.zoo.http.client.response.HttpResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@Getter
public class Barrier extends Dtm {

    private final SqlProvider sqlProvider;

    private int barrierId;

    private final String branchId;

    private final String op;

    private final String reason;

    public Barrier(BarrierParam param, SqlProvider sqlProvider) {
        this.sqlProvider = sqlProvider;
        this.gid = param.getGid();
        this.op = param.getOp();
        this.reason = param.getReason();
        this.branchId = param.getBranch_id();
        this.transactionType = Type.parse(param.getTrans_type());
    }

    public HttpResponse<DtmResponse> call(BarrierOperator<Barrier> operator, DataSource dataSource) throws Exception {
        ++this.barrierId;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            boolean barrierResult = insertBarrier(connection, barrierId, transactionType.getValue(), gid, branchId, op);
            if (barrierResult) {
                if (!operator.accept(this)) {
                    // op failed
                    return DtmResponse.failure("op failed");
                }
                connection.commit();
                connection.setAutoCommit(true);
                // op success
                return DtmResponse.success("op success");
            }else{
                // op skipped
                return DtmResponse.success("op skipped");
            }
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
            // op error
            log.error("Barrier-" + gid, e);
            if(e instanceof HttpException){
                HttpException httpException = (HttpException) e;
                return new HttpResponse<>(httpException.getStatus(),
                        null, new DtmResponse("ERROR", I18Messages.translateIfNeed(e.getMessage())));
            }else{
                return DtmResponse.error(I18Messages.translateIfNeed(e.getMessage()));
            }
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public HttpResponse<DtmResponse> call(BarrierOperator<Barrier> operator, Connection connection) throws Exception {
        ++this.barrierId;
        try{
            connection.setAutoCommit(false);
            boolean barrierResult = insertBarrier(connection, barrierId, transactionType.getValue(), gid, branchId, op);
            if (barrierResult) {
                if(!operator.accept(this)){
                    // op failed
                    return DtmResponse.failure("op failed");
                }
                connection.commit();
                connection.setAutoCommit(true);
                // op success
                return DtmResponse.success("op success");
            }else{
                // op skipped
                return DtmResponse.success("op success");
            }
        } catch (Exception e) {
            if(connection != null){
                connection.rollback();
                connection.setAutoCommit(true);
            }
            // op error
            log.error("Barrier-" + gid, e);
            if(e instanceof HttpException){
                HttpException httpException = (HttpException) e;
                return new HttpResponse<>(httpException.getStatus(),
                        null, new DtmResponse("ERROR", I18Messages.translateIfNeed(e.getMessage())));
            }else{
                return DtmResponse.error(I18Messages.translateIfNeed(e.getMessage()));
            }
        } finally {
            if(connection != null){
                connection.close();
            }
        }
    }

    public HttpResponse<DtmResponse> callInTransaction(BarrierOperator<Barrier> operator, Connection connection) {
        ++this.barrierId;
        try{
            boolean barrierResult = insertBarrier(connection, barrierId, transactionType.getValue(), gid, branchId, op);
            if (barrierResult) {
                if(!operator.accept(this)){
                    // op failed
                    return DtmResponse.failure("op failed");
                }
                // op success
                return DtmResponse.success("op success");
            }else{
                // op skipped
                return DtmResponse.success("op success");
            }
        } catch (Exception e) {
            // op error
            log.error("Barrier-" + gid, e);
            if(e instanceof HttpException){
                HttpException httpException = (HttpException) e;
                return new HttpResponse<>(httpException.getStatus(),
                        null, new DtmResponse("ERROR", I18Messages.translateIfNeed(e.getMessage())));
            }else{
                return DtmResponse.error(I18Messages.translateIfNeed(e.getMessage()));
            }
        }
    }

    protected boolean insertBarrier(Connection connection, int barrierId, String type, String gid, String branchId, String op) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sqlProvider.getSql());
            preparedStatement.setString(1, type);
            preparedStatement.setString(2, gid);
            preparedStatement.setString(3, branchId);
            preparedStatement.setString(4, op);
            preparedStatement.setString(5, String.format("%02d", barrierId));
            preparedStatement.setString(6, reason);
            // 插入失败，不应该重复执行
            if (preparedStatement.executeUpdate() == 0) {
                return false;
            }

            // SAGA补偿操作
            if(transactionType.equals(Type.SAGA) && "compensate".equals(op)){
                preparedStatement.setString(4, "action");
                // 插入成功，表示未执行过，不应该支持补偿操作
                if (preparedStatement.executeUpdate() > 0) {
                    return false;
                }
            }

            // TCC补偿操作
            if(transactionType.equals(Type.TCC) && "cancel".equals(op)){
                preparedStatement.setString(4, "try");
                // 插入成功，表示未执行过，不应该支持补偿操作
                if (preparedStatement.executeUpdate() > 0) {
                    return false;
                }
            }
        } finally {
            if (Objects.nonNull(preparedStatement)) {
                preparedStatement.close();
            }
        }
        return true;
    }
}
