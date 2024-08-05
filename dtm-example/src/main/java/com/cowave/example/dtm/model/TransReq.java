/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.example.dtm.model;

import lombok.Data;

/**
 * @author lixiaoshuang
 */
@Data
public class TransReq {

    /**
     * 用户id
     */
    private int userId;

    /**
     * 转入/转出金额
     */
    private int amount;

    /**
     * jackson 必须使用无参构造
     */
    public TransReq(){
    }

    public TransReq(int userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
