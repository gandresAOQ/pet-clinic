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
public class Memory {

	@Around("execution(* org.springframework.samples.petclinic..*.*(..)) && "
			+ "!execution(* org.springframework.samples.petclinic.repository..*.*(..)) && "
			+ "!execution(* org.springframework.samples.petclinic.aspectj..*.*(..))")
	public Object recordMemory(ProceedingJoinPoint pjp) throws Throwable {

		String className = pjp.getTarget().getClass().getSimpleName();
		String methodName = pjp.getSignature().getName();

		long usedMemoryBefore = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println(">>> [START - MEMORY] " + className + " - " + methodName + " - Used Memory: "
				+ usedMemoryBefore + " bytes\n");

		Object result = pjp.proceed();

		long usedMemoryAfter = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("<<< [END - MEMORY] " + className + " - " + methodName + " - Used memory before: "
				+ usedMemoryBefore + " - Used memory after: " + usedMemoryAfter + " bytes\n");

		Map<String, String> data = new HashMap<>();
		data.put("methodName", methodName);
		data.put("className", className);
		data.put("fullName", className + "." + methodName);

		MongoDB.getMongoDB().report(data, "memory", Double.valueOf(usedMemoryBefore));

		return result;

	}

}
