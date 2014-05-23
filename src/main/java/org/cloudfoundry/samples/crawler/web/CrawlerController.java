package org.cloudfoundry.samples.crawler.web;

import org.cloudfoundry.samples.crawler.domain.CrawlerSession;
import org.cloudfoundry.samples.crawler.domain.Page;
import org.cloudfoundry.samples.crawler.services.CrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/session")
public class CrawlerController {
	
	Logger logger = LoggerFactory.getLogger(CrawlerController.class);
	
	@Autowired
	private CrawlerService service;
	
	@RequestMapping(consumes="application/json", produces="application/json", method = RequestMethod.POST, value="/start")
	@ResponseBody
	public CrawlerSession start(@RequestBody CrawlerSession config){
		CrawlerSession session = new CrawlerSession();
		String url = config.getUrl().startsWith("http://") ? config.getUrl() : "http://"+config.getUrl();
		session.setUrl(config.getUrl());
		session.setMaxDepth(config.getMaxDepth());
		Page page = new Page(session.getUrl());
		
		page.setSession(session);
		logger.debug("Starting crawler session on service {}",service);
		service.crawl(page);
		return session;
	}
	
}
