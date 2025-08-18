package org.springframework.samples.petclinic.aspectj;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.samples.petclinic.repository.MongoDB;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MongoDbInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, java.lang.Object handler,
			java.lang.Exception ex) throws Exception {
		MongoDB.getMongoDB().storeDocuments();
	}

}
