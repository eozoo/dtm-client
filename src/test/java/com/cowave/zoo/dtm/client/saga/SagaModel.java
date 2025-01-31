package com.cowave.zoo.dtm.client.saga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author shanhuiming
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SagaModel {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 转入/转出金额
     */
    private int amount;
}
