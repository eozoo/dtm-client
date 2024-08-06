/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm;

import lombok.Getter;

/**
 *
 * @author shanhuiming
 *
 */
@Getter
public class DtmException extends Exception {

    private final int status;

    public DtmException(int status, String message) {
        super(message);
        this.status = status;
    }

    public DtmException(int status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}
