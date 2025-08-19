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
@Component
public class CpuProcessUsage {

	private static final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory
		.getOperatingSystemMXBean();

	@Around("execution(* org.springframework.samples.petclinic..*.*(..)) && "
			+ "!execution(* org.springframework.samples.petclinic.repository..*.*(..)) && "
			+ "!execution(* org.springframework.samples.petclinic.aspectj..*.*(..))")
	public Object recordProcessCpu(ProceedingJoinPoint pjp) throws Throwable {

		String className = pjp.getTarget().getClass().getSimpleName();
		String methodName = pjp.getSignature().getName();

		double cpuBefore = this.getProcessCpuLoad();
		System.out.println("CPU before: " + cpuBefore);
		if (Double.isNaN(cpuBefore)) {
			return -1;
		}

		Object result = pjp.proceed();

		double cpuAfter = this.getProcessCpuLoad();

		System.out.println("CPU after: " + cpuBefore);

		if (Double.isNaN(cpuAfter)) {
			return -1;
		}

		double avgCpu = (cpuBefore + cpuAfter) / 2;

		System.out.printf("Class Name: %s, Method: %s.%s | Process CPU usage: %.2f%%\n", className, className,
				methodName, avgCpu);

		Map<String, String> data = new HashMap<>();
		data.put("methodName", methodName);
		data.put("className", className);
		data.put("fullName", className + "." + methodName);

		MongoDB.getMongoDB().report(data, "cpu_process", Double.valueOf(avgCpu));

		return result;
	}

	private static double getProcessCpuLoad() {
		// Returns a double in [0.0,1.0]; multiply by 100 for %
		return osBean.getProcessCpuLoad() * 100;
	}

}
