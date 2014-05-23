package org.cloudfoundry.samples.crawler.services;

import org.cloudfoundry.samples.crawler.domain.CrawlerSession;
import org.cloudfoundry.samples.crawler.domain.Page;

public interface CrawlerService {
	
	public CrawlerSession crawl(Page page);
	
}
