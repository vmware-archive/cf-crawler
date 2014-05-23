package org.cloudfoundry.samples.crawler.stomp;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class ClientConnectionHandler implements ApplicationListener<SessionDisconnectEvent>{
	
	

	
	@Override
	public void onApplicationEvent(SessionDisconnectEvent event) {
	}

}
