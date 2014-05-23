package org.cloudfoundry.samples.crawler.messaging;

import org.cloudfoundry.samples.crawler.domain.Constants;
import org.cloudfoundry.samples.crawler.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AmqpPageDispatcher implements PageDispatcher {
	
	Logger logger = LoggerFactory.getLogger(AmqpPageDispatcher.class);
	
	private RabbitTemplate template;
	
	
	@Autowired
	public AmqpPageDispatcher(RabbitTemplate template) {
		this.template = template;
	}



	@Override
	public void dispatch(Page page) {
		logger.debug("Sending page {} to exchange: {}", page.getUrl(), Constants.PAGE_EXCHANGE);
		template.convertAndSend(Constants.PAGE_EXCHANGE,"",page);
	}

}
