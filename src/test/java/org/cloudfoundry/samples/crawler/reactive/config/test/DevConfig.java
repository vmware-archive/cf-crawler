package org.cloudfoundry.samples.crawler.reactive.config.test;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.cloudfoundry.samples.crawler.domain.Page;
import org.cloudfoundry.samples.crawler.messaging.AmqpMessageListener;
import org.cloudfoundry.samples.crawler.messaging.AmqpPageDispatcher;
import org.cloudfoundry.samples.crawler.messaging.MessageListener;
import org.cloudfoundry.samples.crawler.messaging.PageDispatcher;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import reactor.core.Reactor;

@Configuration
@Profile("dev")
@ComponentScan(basePackages = { "org.cloudfoundry.samples.crawler" })
public class DevConfig {

	@Bean
	public ConnectionFactory connection() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		return connectionFactory;
	}

	@Bean
	public RabbitAdmin admin() {
		return new RabbitAdmin(connection());
	}

	@Bean
	@Autowired
	public RabbitTemplate template(ConnectionFactory cf) {
		return new RabbitTemplate(cf);
	}

	@Bean
	public MessageListener<Page> listener(Reactor reactor) {
		return new AmqpMessageListener(reactor);
	}

	@Bean
	public PageDispatcher dispatcher() {
		return new AmqpPageDispatcher(template(connection()));
	}

	@Bean
	public SimpleMessageListenerContainer messageContainer(MessageListener<Page> listener) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connection());
		container.setMessageListener(new MessageListenerAdapter(listener, "onMessage"));
		container.setQueueNames("page_worker");
		return container;
	}

	@Bean
	public HttpClient client() {
		return HttpClients.createDefault();
	}

}
