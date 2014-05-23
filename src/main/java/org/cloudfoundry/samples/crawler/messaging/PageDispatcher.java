package org.cloudfoundry.samples.crawler.messaging;

import org.cloudfoundry.samples.crawler.domain.Page;

public interface PageDispatcher {
	
	public void dispatch(Page page);
	
}
