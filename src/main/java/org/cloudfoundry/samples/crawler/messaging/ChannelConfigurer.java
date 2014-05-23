package org.cloudfoundry.samples.crawler.messaging;

import javax.annotation.PostConstruct;

import org.cloudfoundry.samples.crawler.domain.Constants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChannelConfigurer {

	@Autowired
	private RabbitAdmin admin;
	
	@PostConstruct
	public void setup(){
		Queue pageWorker = new Queue(Constants.PAGE_WORKER_QUEUE, true, false, false);
		Queue controlQueue = new Queue(Constants.CONTROL_QUEUE,true,false,false);
		DirectExchange crawledLinks = new DirectExchange(Constants.CRAWLED_LINKS_EXCHANGE, true, false);
		DirectExchange crawledPages = new DirectExchange(Constants.CRAWLED_PAGES_EXCHANGE,true,false);
		DirectExchange pageExchange = new DirectExchange(Constants.PAGE_EXCHANGE,true,false);
		Binding binding = BindingBuilder.bind(pageWorker).to(pageExchange).with("");
		admin.declareQueue(pageWorker);
		admin.declareQueue(controlQueue);
		admin.declareExchange(crawledPages);
		admin.declareExchange(crawledLinks);
		admin.declareExchange(pageExchange);
		admin.declareBinding(binding);
	}
	
}
