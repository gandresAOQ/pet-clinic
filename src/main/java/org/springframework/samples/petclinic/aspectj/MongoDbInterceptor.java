package org.springframework.samples.petclinic.aspectj;

import jakarta.annotation.PreDestroy;
import org.springframework.samples.petclinic.repository.MongoDB;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MongoDbInterceptor implements HandlerInterceptor {

	@PreDestroy
	public void shutdown() {
		MongoDB.getMongoDB().storeDocuments();
	}

}
