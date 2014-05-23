package org.cloudfoundry.samples.crawler.messaging;

import org.cloudfoundry.samples.crawler.domain.Constants;
import org.cloudfoundry.samples.crawler.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.Reactor;
import reactor.event.Event;

public class AmqpMessageListener implements MessageListener<Page> {
	
	Logger logger = LoggerFactory.getLogger(AmqpMessageListener.class);
	
	private Reactor reactor;
	
	
	public AmqpMessageListener(Reactor reactor) {
		this.reactor = reactor;
	}


	@Override
	public void onMessage(Page message) {
		logger.debug("Received page{} to process, forwarding to reactor", message.getUrl());
		reactor.notify(Constants.REACTOR_CRAWLER, Event.wrap(message));
	}

}
