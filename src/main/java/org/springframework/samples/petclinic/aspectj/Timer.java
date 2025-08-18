package org.springframework.samples.petclinic.aspectj;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.samples.petclinic.repository.MongoDB;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class Timer {

	@Around("execution(* org.springframework.samples.petclinic..*.*(..)) && "
			+ "!execution(* org.springframework.samples.petclinic.repository..*.*(..)) && "
			+ "!execution(* org.springframework.samples.petclinic.aspectj..*.*(..))")
	public Object timeMethod(ProceedingJoinPoint pjp) throws Throwable {
		Double start = Double.valueOf(System.nanoTime());

		Object result = pjp.proceed();

		Double executionTime = System.nanoTime() - start;

		String className = pjp.getTarget().getClass().getSimpleName();
		String methodName = pjp.getSignature().getName();

		System.out.printf("[TIMING] %s.%s() ejecutado en %f", className, methodName, executionTime);

		Map<String, String> data = new HashMap<>();
		data.put("methodName", methodName);
		data.put("className", className);
		data.put("fullName", className + "." + methodName);

		MongoDB.getMongoDB().report(data, "time", executionTime);

		return result;
	}

}
