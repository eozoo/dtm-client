/*
 * Copyright (c) 2017～2099 Cowave All Rights Reserved.
 *
 * For licensing information, please contact: https://www.cowave.com.
 *
 * This code is proprietary and confidential.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.cowave.commons.dtm;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 默认了package都以com.cowave为根
 *
 * @author shanhuiming
 */
public class DtmConfiguration {

    @Bean
    public DtmClient dtmClient(DtmService dtmService){
        return new DtmClient(dtmService);
    }
}
