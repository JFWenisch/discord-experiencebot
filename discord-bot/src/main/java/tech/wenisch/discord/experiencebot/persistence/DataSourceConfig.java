package tech.wenisch.discord.experiencebot.persistence;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ComponentScan("tech.wenisch.discord.experiencebot.persistence")

public class DataSourceConfig {


	@Bean
	DataSource dataSource() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setUrl("jdbc:postgresql://localhost:5432/experience");
		driverManagerDataSource.setUsername("user");
		driverManagerDataSource.setPassword("topsecret");

		return driverManagerDataSource;
	}
	
	  @Bean
	   public JdbcTemplate jdbcTemplate() {
	     return new JdbcTemplate(this.dataSource());
	   }
}