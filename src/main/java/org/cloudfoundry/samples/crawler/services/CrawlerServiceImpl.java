package org.cloudfoundry.samples.crawler.services;

import org.cloudfoundry.samples.crawler.domain.Constants;
import org.cloudfoundry.samples.crawler.domain.CrawlerSession;
import org.cloudfoundry.samples.crawler.domain.Page;
import org.cloudfoundry.samples.crawler.messaging.PageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrawlerServiceImpl implements CrawlerService {

	Logger logger = LoggerFactory.getLogger(CrawlerService.class);
	
	@Autowired
	private PageDispatcher dispatcher;
	
	@Autowired
	private RabbitAdmin admin;
	
	@Autowired
	private SimpleMessageListenerContainer crawledPageContainer;
	
	@Autowired
	private SimpleMessageListenerContainer crawledLinkContainer;
	
	@Override
	public CrawlerSession crawl(Page page) {
		logger.debug("Starting crawling session for page: {}",page);
		dispatcher.dispatch(page);
		return page.getSession();
	}
	
	private void createBindings(CrawlerSession session){
		 Queue queue = new Queue("pages_"+session.getId(), false, false, true);
		 Queue linkQueue = new Queue("links_"+session.getId(), false, false, true);
		 admin.declareQueue(queue);
		 admin.declareQueue(linkQueue);
		 Binding binding = BindingBuilder.bind(queue).to(new DirectExchange(Constants.CRAWLED_PAGES_EXCHANGE)).with(session.getId());
		 Binding linkBinding = BindingBuilder.bind(linkQueue).to(new DirectExchange(Constants.CRAWLED_LINKS_EXCHANGE)).with(session.getId());
		 admin.declareBinding(binding);
		 admin.declareBinding(linkBinding);
		 crawledPageContainer.addQueues(queue);
		 crawledLinkContainer.addQueues(linkQueue);
	}

}
