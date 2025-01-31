package com.cowave.zoo.dtm.client.impl.sql;

/**
 *
 * @author shanhuiming
 *
 */
public class MysqlProvider implements SqlProvider {

    @Override
    public String getSql() {
        return "insert ignore into dtm_barrier(trans_type, gid, branch_id, op, barrier_id, reason)values(?, ?, ?, ?, ?, ?)";
    }
}
