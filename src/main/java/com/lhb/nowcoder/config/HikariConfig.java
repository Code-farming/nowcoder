package com.lhb.nowcoder.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class HikariConfig {
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource getDataSource() {
        return new HikariDataSource();
    }

}
