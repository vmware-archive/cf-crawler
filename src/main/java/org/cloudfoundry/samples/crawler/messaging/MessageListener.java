package org.cloudfoundry.samples.crawler.messaging;

public interface MessageListener<T> {

	public void onMessage(T message);
	
	
}
