package org.cloudfoundry.samples.crawler.config;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.cloudfoundry.samples.crawler.domain.StompConfig;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
@Profile("cloud")
public class CloudConfig extends AbstractCloudConfig{
	
	
	private ObjectMapper customMapper;
	
	@Autowired
	private Environment environment;
	
	@Bean
	public ConnectionFactory connection(){
		return connectionFactory().rabbitConnectionFactory();
	}
	
	@Bean 
	public StompConfig stompConfig(){
		StompConfig stompConfig = null;
		try {
			Map<String,Object> jsonMap = customMapper.readValue(environment.getProperty("VCAP_SERVICES"), Map.class);
			for(Entry<String, Object> entry : jsonMap.entrySet()){
				List<Map<String,Object>> services = (List<Map<String,Object>>) entry.getValue();
				for(Map<String,Object> serviceMap : services){
					if(serviceMap.get("tags") != null){
						List<String> tags = (List<String>) serviceMap.get("tags");
						if(tags.contains("rabbitmq")){
							Map<String,Object> credentials = (Map<String, Object>) serviceMap.get("credentials");
							stompConfig = customMapper.convertValue((Map<String, Object>) ((Map<String,Object>)(credentials.get("protocols"))).get("stomp"), StompConfig.class);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return stompConfig;
	}

	
	@PostConstruct
	public void setup(){
		this.customMapper = new ObjectMapper();
		customMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
}
