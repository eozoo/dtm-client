/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm.impl;

import com.cowave.commons.dtm.DtmOperator;
import com.cowave.commons.dtm.DtmResult;
import com.cowave.commons.tools.HttpException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;

import static com.cowave.commons.dtm.impl.DtmTransaction.Type.SAGA;
import static com.cowave.commons.dtm.impl.DtmTransaction.Type.TCC;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Barrier extends DtmTransaction {

    private static final String SQL = "insert ignore into dtm_barrier(trans_type, gid, branch_id, op, barrier_id, reason)values(?, ?, ?, ?, ?, ?)";

    private int barrierId;

    private String op;

    private String branchId;

    public Barrier(@Nonnull BarrierParam param) {
        this.setGid(param.getGid());
        this.setTransactionType(Type.parse(param.getTrans_type()));
        this.op = param.getOp();
        this.branchId = param.getBranch_id();
    }

    public void call(DtmOperator<Barrier> operator, DataSource dataSource) throws Exception {
        ++this.barrierId;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            boolean barrierResult = insertBarrier(connection, barrierId, getTransactionType().getValue(), getGid(), branchId, op);
            if (barrierResult) {
                if (!operator.accept(this)) {
                    throw new HttpException(DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Barrier operate failed");
                }
                connection.commit();
                connection.setAutoCommit(true);

            }
        } catch (HttpException e) {
            if (connection != null) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
            throw e;
        } catch (Exception e) {
            if (connection != null) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
            throw new HttpException(e, DtmResult.CODE_ERROR, DtmResult.ERROR, "DTM Barrier operate failed");
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    public void call(DtmOperator<Barrier> operator, Connection connection) {
        ++this.barrierId;
        try{
            boolean barrierResult = insertBarrier(connection, barrierId, getTransactionType().getValue(), getGid(), branchId, op);
            if (barrierResult) {
                if(!operator.accept(this)){
                    throw new HttpException(DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Barrier operate failed");
                }
            }
        } catch (HttpException e){
            throw e;
        } catch (Exception e) {
            throw new HttpException(e, DtmResult.CODE_ERROR, DtmResult.ERROR, "DTM Barrier operate failed");
        }
    }

    public void callAndClose(DtmOperator<Barrier> operator, Connection connection) throws Exception {
        ++this.barrierId;
        try{
            connection.setAutoCommit(false);
            boolean barrierResult = insertBarrier(connection, barrierId, getTransactionType().getValue(), getGid(), branchId, op);
            if (barrierResult) {
                if(!operator.accept(this)){
                    throw new HttpException(DtmResult.CODE_FAILURE, DtmResult.FAILURE, "DTM Barrier operate failed");
                }
                connection.commit();
                connection.setAutoCommit(true);
            }
        } catch (HttpException e){
            connection.rollback();
            connection.setAutoCommit(true);
            throw e;
        } catch (Exception e) {
            if(connection != null){
                connection.rollback();
                connection.setAutoCommit(true);
            }
            throw new HttpException(e, DtmResult.CODE_ERROR, DtmResult.ERROR, "DTM Barrier operate failed");
        } finally {
            if(connection != null){
                connection.close();
            }
        }
    }

    protected boolean insertBarrier(Connection connection, int barrierId, String type, String gid, String branchId, String op) throws Exception {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, type);
            preparedStatement.setString(2, gid);
            preparedStatement.setString(3, branchId);
            preparedStatement.setString(4, op);
            preparedStatement.setString(5, String.format("%02d", barrierId));
            preparedStatement.setString(6, op);
            // 过滤重复执行
            if (preparedStatement.executeUpdate() == 0) {
                return false;
            }

            // 执行cancel，必须要先执行过try
            if ("cancel".equals(op)) {
                if(transactionType.equals(TCC)){
                    preparedStatement.setString(4, "try");
                }else if(transactionType.equals(SAGA)){
                    preparedStatement.setString(4, "compensate");
                }else{
                    preparedStatement.setString(4, "rollback");
                }
                // 插入成功肯定未执行过
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
