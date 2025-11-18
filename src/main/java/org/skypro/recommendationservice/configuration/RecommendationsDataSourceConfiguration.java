package org.skypro.recommendationservice.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class RecommendationsDataSourceConfiguration {

    @Primary
    @Bean(name = "writeDataSource")
    public DataSource writeDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password
    ) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }

    @Primary
    @Bean(name = "writeJdbcTemplate")
    public JdbcTemplate writeJdbcTemplate(@Qualifier("writeDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
//
//    @Bean(name = "recommendationsDataSource")
//    public DataSource recommendationsDataSource(@Value("${application.recommendations-db.url}") String recommendationsUrl) {
//        var dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl(recommendationsUrl);
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setReadOnly(true);
//        return dataSource;
//    }
//
//    @Bean(name = "recommendationsJdbcTemplate")
//    public JdbcTemplate recommendationsJdbcTemplate(
//            @Qualifier("recommendationsDataSource") DataSource dataSource
//    ) {
//        return new JdbcTemplate(dataSource);
//    }

}
