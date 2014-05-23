package org.cloudfoundry.samples.crawler.config;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudfoundry.samples.crawler.domain.StompConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudException;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.config.StompBrokerRelayRegistration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocketMessageBroker
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	private Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

	@Autowired
	private StompConfig stompConfig;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/stomp").withSockJS();
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
	}

	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {

	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		StompBrokerRelayRegistration relay = registry.enableStompBrokerRelay("/topic/", "/queue/");
		logger.debug("setting up relay with config : {}", stompConfig);
		relay.setSystemLogin(stompConfig.getUsername());
		relay.setSystemPasscode(stompConfig.getPassword());
		relay.setClientLogin(stompConfig.getUsername());
		relay.setClientPasscode(stompConfig.getPassword());
		relay.setRelayHost(stompConfig.getHost());
		relay.setRelayPort(stompConfig.getPort());
		if(stompConfig.getVhost() != null){
			relay.setVirtualHost(stompConfig.getVhost());
		}
		registry.setApplicationDestinationPrefixes("/app");

	}

	@Override
	public boolean configureMessageConverters(List<MessageConverter> converters) {
		return true;
	}

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration arg0) {

	}

}
