package com.cowave.zoo.dtm.client;

import com.cowave.zoo.dtm.client.impl.sql.MysqlProvider;
import com.cowave.zoo.dtm.client.impl.sql.SqlProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

import javax.sql.DataSource;

/**
 *
 * @author shanhuiming
 *
 */
public class DtmConfiguration {

    @ConditionalOnMissingBean
    @Bean
    public SqlProvider sqlProvider(){
        return new MysqlProvider();
    }

    @Bean
    public DtmClient dtmClient(DtmService dtmService, SqlProvider sqlProvider, @Nullable DataSource dataSource){
        return new DtmClient(dtmService, dataSource, sqlProvider);
    }
}
