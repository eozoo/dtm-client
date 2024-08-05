/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm;

import com.cowave.commons.dtm.impl.DtmTransaction;

/**
 *
 * @author shanhuiming
 *
 */
@FunctionalInterface
public interface DtmOperator<T extends DtmTransaction> {

    void accept(T t) throws Exception;
}
