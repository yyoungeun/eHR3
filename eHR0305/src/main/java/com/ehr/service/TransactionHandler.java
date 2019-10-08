package com.ehr.service;

import java.lang.reflect.InvocationTargetException;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class TransactionHandler implements  MethodInterceptor{
	private final Logger LOG = Logger.getLogger(TransactionHandler.class);

	private PlatformTransactionManager transactionManager;
	

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			//target method call
			Object retObj = invocation.proceed();
			//성공 commit;
			this.transactionManager.commit(status);
			LOG.debug("=================================");
			LOG.debug("=invoke retObj="+retObj.toString());
			LOG.debug("=================================");			
			return retObj;
		} catch (RuntimeException e) {
			//실패 rollback;
			this.transactionManager.rollback(status);	
			LOG.debug("=================================");
			LOG.debug("=invokeTransaction rollback=");
			LOG.debug("=================================");
			throw e;
		}
	}

	

}
