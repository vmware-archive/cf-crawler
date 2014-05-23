package org.cloudfoundry.samples.crawler.reactive;

import org.cloudfoundry.samples.crawler.domain.Page;

import reactor.event.Event;

public interface PageHandler {

	public abstract Page handle(Event<Page> event);

}