package org.cloudfoundry.samples.crawler.domain;

public interface Constants {
	
	String CRAWLED_PAGES_EXCHANGE = "crawled_pages.direct";
	String CRAWLED_LINKS_EXCHANGE = "crawled_links.direct";
	String PAGE_EXCHANGE = "page.direct";
	String CONTROL_QUEUE = "stomp_control_queue";
	String PAGE_WORKER_QUEUE = "page_worker";
	String REACTOR_CRAWLER = "page.crawl";
	String REACTOR_WORK_DISPATCHER = "page.dispatch";
	
}
