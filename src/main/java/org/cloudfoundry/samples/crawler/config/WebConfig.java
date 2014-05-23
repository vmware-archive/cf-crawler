package org.cloudfoundry.samples.crawler.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
@EnableWebMvc
@ComponentScan(basePackages="org.cloudfoundry.samples.crawler.controllers")
public class WebConfig extends WebMvcConfigurerAdapter{
	
	
		@Bean
		public FreeMarkerViewResolver viewResolver(FreeMarkerConfigurer config) {
			FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
			resolver.setCache(true);
			resolver.setSuffix(".html");
			resolver.setPrefix("");
			return resolver;
		}
	
		
		 @Override
		    public void addViewControllers(ViewControllerRegistry registry) {
		        registry.addViewController("/").setViewName("crawler");
		    }
	
	    @Override
	    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
	        configurer.enable();
	    }

		@Override
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/static/");
		}

		@Override
		public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			
			MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
			converter.setObjectMapper(mapper);
			converters.add(converter);
			super.configureMessageConverters(converters);
		}
		
		   
		
}
