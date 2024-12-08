package com.nbloi.cqrses.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.store.JdbcChannelMessageStore;
import org.springframework.integration.jdbc.store.channel.MySqlChannelMessageStoreQueryProvider;
import org.springframework.integration.transaction.TransactionInterceptorBuilder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
public class SpringIntegrationConfiguration {

    private static final String CONCURRENT_METADATA_STORE_PREFIX = "_spring_integration_";

    @Bean
    public TransactionInterceptor springIntegrationTransactionInterceptor() {
        return new TransactionInterceptorBuilder()
                .isolation(Isolation.READ_COMMITTED)
                .build();
    }

    @Bean
    JdbcChannelMessageStore jdbcChannelMessageStore(DataSource dataSource) {
        JdbcChannelMessageStore jdbcChannelMessageStore = new JdbcChannelMessageStore(dataSource);
        jdbcChannelMessageStore.setTablePrefix(CONCURRENT_METADATA_STORE_PREFIX);
        jdbcChannelMessageStore.setChannelMessageStoreQueryProvider(
                new MySqlChannelMessageStoreQueryProvider());
        return jdbcChannelMessageStore;
    }
}

