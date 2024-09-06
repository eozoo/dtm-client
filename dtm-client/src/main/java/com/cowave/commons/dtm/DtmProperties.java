/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author shanhuiming
 *
 */
@Data
@ConfigurationProperties(prefix = "spring.dtm")
public class DtmProperties {

    private String address;

    private String name;
}
