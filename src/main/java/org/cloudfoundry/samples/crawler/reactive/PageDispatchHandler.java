package org.cloudfoundry.samples.crawler.reactive;

import java.net.URI;

import org.cloudfoundry.samples.crawler.domain.Link;
import org.cloudfoundry.samples.crawler.domain.Page;
import org.cloudfoundry.samples.crawler.messaging.PageDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.cloudfoundry.samples.crawler.domain.Constants;

import reactor.event.Event;
import reactor.spring.annotation.Selector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PageDispatchHandler implements PageHandler{
	
	@Autowired
	private PageDispatcher dispatcher;
	
	private Logger logger = LoggerFactory.getLogger(PageDispatchHandler.class);
	
	@Autowired
	private SimpMessagingTemplate template;
	
	private ObjectMapper mapper = new ObjectMapper();
	
	
	@Override
	@Selector(value=Constants.REACTOR_WORK_DISPATCHER,reactor="@rootReactor")
	public Page handle(Event<Page> event) {
		Page page = event.getData();
		
		if(page.getResponseCode() == 200 && page.getDepth() < page.getSession().getMaxDepth()){
			Page startDomain = new Page(domain(page.getUrl()));
			startDomain.setCrawlTime(page.getCrawlTime());
			startDomain.setResponseCode(page.getResponseCode());
			startDomain.setDepth(page.getDepth());
			startDomain.setRef(page.getRef());
			startDomain.setSession(page.getSession());
			for(Link link : event.getData().getLinks()){
				startDomain.addLink(new Page(domain(link.getDestination().getUrl())));
				dispatcher.dispatch(link.getDestination());
			}
			for(Link link : startDomain.getLinks()){
				try {
					template.convertAndSend("/queue/link_"+page.getSession().getId(),link);
				} catch (Exception e ) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	
	private String domain(String url){
		String domain = url;
		try {
			URI uri = new URI(url);
			domain = uri.getHost();
		} catch (Exception e) {
		}
		return domain;
	}
}
