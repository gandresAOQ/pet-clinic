package org.springframework.samples.petclinic.aspectj;

import com.sun.management.OperatingSystemMXBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.samples.petclinic.repository.MongoDB;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

@Aspect
public class CpuSystemUsage {

	private static final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory
		.getOperatingSystemMXBean();

	@Around("execution(* org.springframework.samples.petclinic..*.*(..)) && "
			+ "!execution(* org.springframework.samples.petclinic.aspectj..*.*(..)) && "
			+ "!execution(* org.springframework.samples.petclinic.repository..*.*(..)) && "
			+ "!execution(* org.springframework.samples.petclinic.system.WebConfiguration.localeResolver(..))")
	public Object recordSystemCpu(ProceedingJoinPoint pjp) throws Throwable {

		String className = pjp.getTarget().getClass().getSimpleName();
		String methodName = pjp.getSignature().getName();

		double cpuBefore = this.getSystemCpuLoad();
		if (Double.isNaN(cpuBefore)) {
			return -1;
		}

		Object result = pjp.proceed();

		double cpuAfter = this.getSystemCpuLoad();

		if (Double.isNaN(cpuAfter)) {
			return -1;
		}

		double avgCpu = (cpuBefore + cpuAfter) / 2;

		System.out.printf("Class Name: %s, Method: %s.%s | System CPU usage: %.2f%%\n", className, className,
				methodName, avgCpu);

		Map<String, String> data = new HashMap<>();
		data.put("methodName", methodName);
		data.put("className", className);
		data.put("fullName", className + "." + methodName);

		MongoDB.getMongoDB().report(data, "cpu_system", Double.valueOf(avgCpu));

		return result;
	}

	private double getSystemCpuLoad() {
		return osBean.getCpuLoad() * 100;
	}

}
