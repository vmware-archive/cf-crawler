package org.cloudfoundry.samples.crawler.config;

import org.cloudfoundry.samples.crawler.domain.StompConfig;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Configuration
@Profile("local")
public class LocalConfig {

	
	@Bean
	public ConnectionFactory connection() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		return connectionFactory;
	}
	
	@Bean
	public StompConfig stompConfig(){
		StompConfig config = new StompConfig();
		config.setHost("localhost");
		config.setPassword("guest");
		config.setUsername("guest");
		config.setPort(61613);
		return config;
	}

	

	

}
