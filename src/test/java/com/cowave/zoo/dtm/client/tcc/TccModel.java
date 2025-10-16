package com.cowave.zoo.dtm.client.tcc;

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
public class TccModel {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 转入/转出金额
     */
    private int amount;
}
