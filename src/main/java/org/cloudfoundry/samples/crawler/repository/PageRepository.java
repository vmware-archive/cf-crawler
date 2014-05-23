package org.cloudfoundry.samples.crawler.repository;

import org.cloudfoundry.samples.crawler.domain.Page;


@org.springframework.stereotype.Repository
public interface PageRepository {
	
		public void save(Page page);
}
