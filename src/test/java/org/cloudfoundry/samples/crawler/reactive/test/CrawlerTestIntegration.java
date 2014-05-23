package org.cloudfoundry.samples.crawler.reactive.test;

import org.cloudfoundry.samples.crawler.domain.CrawlerSession;
import org.cloudfoundry.samples.crawler.domain.Page;
import org.cloudfoundry.samples.crawler.reactive.config.test.DevConfig;
import org.cloudfoundry.samples.crawler.services.CrawlerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={DevConfig.class})
@ActiveProfiles("dev")
public class CrawlerTestIntegration {

	@Autowired
	private CrawlerService service;
	
	@Test
	public void crawl() throws Exception {
		Page page = new Page("http://www.ign.com/");
		CrawlerSession session = new CrawlerSession();
		session.setMaxDepth(4);
		page.setSession(session);
		service.crawl(page);
		Thread.sleep(60000L);
	}
	
}
