/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm.impl;

import com.cowave.commons.dtm.DtmException;
import com.cowave.commons.dtm.DtmOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
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
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Barrier extends DtmTransaction {

    private static final String SQL = "insert ignore into dtm_barrier(trans_type, gid, branch_id, op, barrier_id, reason)values(?, ?, ?, ?, ?, ?)";

    private int barrierId;

    private String op;

    private String branchId;

    private DataSource dataSource;

    public Barrier(@Nonnull BarrierParam param, @Nonnull DataSource dataSource) {
        this.setGid(param.getGid());
        this.setTransactionType(Type.parse(param.getTrans_type()));
        this.op = param.getOp();
        this.branchId = param.getBranch_id();
        this.dataSource = dataSource;
    }

    public void call(DtmOperator<Barrier> operator) throws Exception {
        ++this.barrierId;
        Connection connection = null;
        try{
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            boolean barrierResult = insertBarrier(connection, barrierId, getTransactionType().getValue(), getGid(), branchId, op);
            if (barrierResult) {
                operator.accept(this);
                connection.commit();
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            if(connection != null){
                connection.rollback();
                connection.setAutoCommit(true);
            }
            throw new DtmException("DTM Barrier operate failed", e);
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
                preparedStatement.setString(4, "try");
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
