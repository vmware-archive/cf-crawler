package org.cloudfoundry.samples.crawler.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StompUnsubscribeAspect {
	
	@Autowired
	private RabbitAdmin admin;

	public Object removeQueues(ProceedingJoinPoint pjp) throws Throwable{
		Object result = pjp.proceed();
		Message<?> message = (Message)result;
		StompHeaderAccessor headers = StompHeaderAccessor.wrap(message);
		if(headers.getCommand().equals(StompCommand.UNSUBSCRIBE)){
			admin.deleteQueue(headers.getDestination());
		}
		return result;
	}
	
}
