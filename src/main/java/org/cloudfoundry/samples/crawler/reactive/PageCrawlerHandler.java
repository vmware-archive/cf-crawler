package org.cloudfoundry.samples.crawler.reactive;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.Link;
import org.apache.tika.sax.LinkContentHandler;
import org.cloudfoundry.samples.crawler.concurrent.TokenBucket;
import org.cloudfoundry.samples.crawler.domain.Constants;
import org.cloudfoundry.samples.crawler.domain.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import reactor.event.Event;
import reactor.spring.annotation.ReplyTo;
import reactor.spring.annotation.Selector;

@Component
public class PageCrawlerHandler implements PageHandler {
	
	private Logger logger = LoggerFactory.getLogger(PageCrawlerHandler.class);

	@Autowired
	private HttpClient client;
	
	
	@Autowired
	private TokenBucket requestToken;
	
	@Autowired
	private SimpMessagingTemplate template;
	
	RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(2000)
	        .setSocketTimeout(1000)
	        .setConnectTimeout(1000)
	        .build();
	
	@Override
	@Selector(value=Constants.REACTOR_CRAWLER,reactor="@rootReactor")
	@ReplyTo(Constants.REACTOR_WORK_DISPATCHER)
	public Page handle(Event<Page> event){
		Page page = event.getData();
		logger.debug("Fetching page " + page);
		CloseableHttpResponse response = null;
		try {
			requestToken.acquire();
			HttpGet get = new HttpGet(page.getUrl());
			get.setConfig(requestConfig);
			long startTime = System.currentTimeMillis();
			response = (CloseableHttpResponse) client.execute(get);
			page.setResponseCode(response.getStatusLine().getStatusCode());
			logger.debug("Response: {}", response.getStatusLine());
			if(page.getResponseCode() == 200 && accepts(response.getHeaders("Content-Type"))){
				byte[] content = EntityUtils.toByteArray(response.getEntity());
				long endTime = System.currentTimeMillis();
				
				ByteArrayInputStream input = new ByteArrayInputStream(content);
				page.setSize(new Long(content.length));
				page.setCrawlTime(endTime-startTime);
				LinkContentHandler linkHandler = new LinkContentHandler();
		        Metadata metadata = new Metadata();
		        ParseContext parseContext = new ParseContext();
		        HtmlParser parser = new HtmlParser();
		        parser.parse(input, linkHandler, metadata, parseContext);
		        for(Link link : linkHandler.getLinks()){
		        	if(link.getType().equals("a") && link.getUri().startsWith("http://")){
		        		Page destination = new Page(link.getUri(),page);
		        		destination.setSession(page.getSession());
		        		page.addLink(destination);
		        	}
		        }
		        logger.debug("Page retrieved: {}" + page);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			page.setResponseCode(400);
		}finally{
			if(response != null){
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		logger.debug("Sending page to stomp clients");
		template.convertAndSend("/queue/page_"+page.getSession().getId(),page);
		return page;
	}

	public boolean accepts(Header[] headers){
		boolean accepts = false;
		for(Header h : headers){
			ContentType ct = ContentType.parse(h.getValue());
			if(ct.getMimeType().equals(ContentType.TEXT_HTML.getMimeType())){
				accepts = true;
				break;
			}
		}
		return accepts;
	}
	
	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}
	
}
