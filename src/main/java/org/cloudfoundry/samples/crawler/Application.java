package org.cloudfoundry.samples.crawler;

import org.cloudfoundry.samples.crawler.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@ComponentScan
@Configuration
public class Application {
	
	
	static Logger logger = LoggerFactory.getLogger(Application.class);
	
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(AppConfig.class);
		String activeProfile = getProfile();
		System.out.println("Starting application with profile " + activeProfile );
		application.setAdditionalProfiles(activeProfile);
		application.run(args);
	}
	
	private static String getProfile(){
		String profile = null;
		Cloud cloud = getCloud();
		if(cloud == null){
			profile = "local";
		}else{
			profile = "cloud";
		}
		return profile;
	}
	
	private static Cloud getCloud() {
        try {
            CloudFactory cloudFactory = new CloudFactory();
            return cloudFactory.getCloud();
        } catch (CloudException ce) {
            return null;
        }
    }
}
