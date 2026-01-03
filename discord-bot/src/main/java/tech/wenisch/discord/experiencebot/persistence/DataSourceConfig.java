package tech.wenisch.discord.experiencebot.persistence;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@ComponentScan("tech.wenisch.discord.experiencebot.persistence")

public class DataSourceConfig {

    // Let Spring Boot auto-configure the DataSource from application.properties/environment.
    // Provide a JdbcTemplate bean that uses the auto-configured DataSource.
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}