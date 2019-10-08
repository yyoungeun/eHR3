package com.ehr.service;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.ehr.User;

public class UserServiceTx implements UserService {
	private final Logger LOG = Logger.getLogger(UserServiceTx.class);
	
	private PlatformTransactionManager transactionManager;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	private UserService userService;

	public void setUserService(UserService userService) {
		this.userService = userService;
	} //외부에서 호출(set)

	@Override
	public void add(User user) {
		userService.add(user);

	}

	@Override
	public void upgradeLevels() throws SQLException {
		//트랜잭션 시작
		int upCnt = 0;
		
		TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			//정상이면 Commit
			userService.upgradeLevels();
			transactionManager.commit(status); 
		}catch(RuntimeException e) {
			//실패면 rollback
			LOG.info("==================================================");
			LOG.info("=Exception= " + e.toString());
			LOG.info("=rollback c= " + transactionManager.toString());
			LOG.info("=status= " + status.toString());
			LOG.info("==================================================");
			transactionManager.rollback(status); 
			throw e;
		}
		LOG.info("==================================================");
		LOG.info("=upCnt= " + upCnt);
		LOG.info("==================================================");
		//트랜잭션 완료
	}
}
