package org.cloudfoundry.samples.crawler.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.cloudfoundry.samples.crawler.domain.Constants;
import org.cloudfoundry.samples.crawler.domain.Page;
import org.cloudfoundry.samples.crawler.messaging.AmqpMessageListener;
import org.cloudfoundry.samples.crawler.messaging.AmqpPageDispatcher;
import org.cloudfoundry.samples.crawler.messaging.MessageListener;
import org.cloudfoundry.samples.crawler.messaging.PageDispatcher;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.Environment;
import reactor.core.Reactor;
import reactor.core.spec.Reactors;
import reactor.spring.context.config.EnableReactor;

@Configuration
@EnableReactor
@EnableAutoConfiguration(exclude={RabbitAutoConfiguration.class})
@EnableAspectJAutoProxy(proxyTargetClass=true)
@ComponentScan(basePackages = { "org.cloudfoundry.samples.crawler" })
public class AppConfig {
	
	@Autowired
	private org.springframework.core.env.Environment environment;
	
	@Bean
	Environment env() {
		return new Environment();

	}

	@Bean
	public ObjectMapper customMapper(){
		ObjectMapper mapper =  new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		return mapper;
	}
	
	@Bean
	Reactor rootReactor(Environment env) {
		return Reactors.reactor().env(env).dispatcher(Environment.THREAD_POOL).get();
	}

	@Bean
	public MessageListener<Page> listener(Reactor reactor) {
		return new AmqpMessageListener(reactor);
	}

	@Bean
	public RabbitAdmin admin(ConnectionFactory connection) {
		return new RabbitAdmin(connection);
	}

	@Bean
	@Autowired
	public RabbitTemplate template(ConnectionFactory cf) {
		return new RabbitTemplate(cf);
	}

	@Bean(name = "messageContainer")
	@DependsOn(value = "channelConfigurer")
	public SimpleMessageListenerContainer messageContainer(MessageListener<Page> listener, RabbitAdmin admin, ConnectionFactory connection) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connection);
		container.setMessageListener(new MessageListenerAdapter(listener, "onMessage"));
		container.setQueueNames(Constants.PAGE_WORKER_QUEUE);
		return container;
	}

	@Bean
	public HttpClient client() {
		return HttpClients.createDefault();
	}

}
