package com.ehr.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

/**
 * 어드바이스(Advice)
     횡단 관(공통기능)심에 해당하는 기능. 독립된 클래스로 생성.
 * @author sist
 *
 */
public class MemberAdvice implements MethodInterceptor {
	private final Logger LOG=Logger.getLogger(this.getClass());
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String method = invocation.getMethod().getName();//method name
		LOG.debug("*******************************");
		LOG.debug("*"+method+" 수행! ");
		
		Object result = invocation.proceed();
		
		LOG.debug("*"+method+" 종료! ");
		LOG.debug("*******************************");
		return result;
	}

}
