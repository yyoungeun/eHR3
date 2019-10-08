package com.ehr;

import static com.ehr.service.UserServiceImpl.MIN_LOGINCOUNT_FOR_SILVER;
import static com.ehr.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.ehr.service.UserServiceImpl;
import com.ehr.service.UserServiceTx;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ="/test-applicationContext.xml")
public class UserServiceTest {
	
	private Logger LOG = Logger.getLogger(UserServiceTest.class);
	
	@Autowired
	MailSender mailSender;
	
	@Autowired //location에서 파일에서 객체를 찾아 자동으로 매칭시켜줌
	UserServiceImpl userService;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	DataSource dataSource;
	
	List<User> users;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	
	//대상 Data생성
	@Before
	public void setUp() {
		LOG.info("setUp()");
		users = Arrays.asList(
				 new User("j01_134", "송영은01", "1234", Level.BASIC, MIN_LOGINCOUNT_FOR_SILVER-1, 0, "1150amy@naver.com", "2019/08/26")
				,new User("j02_134", "송영은02", "1234", Level.BASIC, MIN_LOGINCOUNT_FOR_SILVER, 0, "1150amy@naver.com", "2019/08/26") //등업대상 (Basic -> Silver)
				,new User("j03_134", "송영은03", "1234", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "1150amy@naver.com", "2019/08/26")
				,new User("j04_134", "송영은04", "1234", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "1150amy@naver.com", "2019/08/26") //등업대상 (Silver -> Gold)
				,new User("j05_134", "송영은05", "1234", Level.GOLD, 99, 99, "1150amy@naver.com", "2019/08/26")
				);
	}
	
	//최초 사용자 등록 : Level == null이면  Basic처리
	@Test
	@Ignore
	public void add() {
		//1. 전체 삭제
		//2. Level
		//3. Level == null
		//4. update
		userDao.deleteAll();
		User userExistsLevel = users.get(4); //레벨 있는 사람(gold)
		
		User userNoLevel = users.get(0); //basic
		userNoLevel.sethLevel(null);
		
		//Gold Level, Level == null 등록
		userService.add(userExistsLevel);
		//Level == null -> Level.BASIC
		userService.add(userNoLevel);
		
		//등록 데이터 GET
		User userExistsLevelRead =  userDao.get(userExistsLevel.getU_id());
		User userNoLevelRead = userDao.get(userNoLevel.getU_id());
		
		//비교
		assertThat(userExistsLevelRead.gethLevel(), is(userExistsLevel.gethLevel()));
		assertThat(userNoLevelRead.gethLevel(), is(Level.BASIC));
			
	}
	
	@Test
	public void upgradeLevel() throws SQLException {
		//1. 전체삭제
		//2. user등록
		//3. upgradeLevels call
		//4. 2,4사용자 upgrade대상
		
		userDao.deleteAll();
		
		for(User user :users) {
			userDao.add(user);
		}
		
		this.userService.upgradeLevels();
		checkUser(users.get(0),Level.BASIC);
		checkUser(users.get(1),Level.SILVER);
		checkUser(users.get(2),Level.SILVER);
		checkUser(users.get(3),Level.GOLD);
		checkUser(users.get(4),Level.GOLD);
	}
	
	private void checkUser(User user, Level expectedLevel) {
		User userUpdate = userDao.get(user.getU_id());
		assertThat(userUpdate.gethLevel(), is(expectedLevel));
	}
	
	@Test
	public void serviceBean() {
		assertThat(this.userService, is(notNullValue()));
		assertThat(this.userDao, is(notNullValue()));
		assertThat(this.dataSource, is(notNullValue()));
		assertThat(this.transactionManager, is(notNullValue()));
		assertThat(this.mailSender, is(notNullValue()));
		
		LOG.info("==============================================");
		LOG.info("-userService-"+userService);
		LOG.info("-userDao-"+userDao);
		LOG.info("-dataSource-"+dataSource);
		LOG.info("-transactionManager-"+ transactionManager);
		LOG.info("mailsender-" + mailSender);
		LOG.info("==============================================");
	}
	
	@Test
	public void upgradeAllOrNothing() throws SQLException {
		UserServiceImpl testUserService = new TestUserService(users.get(3).getU_id());
		testUserService.setUserDao(userDao); //userDao 수동DI
		//testUserService.setDataSource(dataSource);
		//testUserService.setTransactionManager(transactionManager); //주입
		testUserService.setMailSender(mailSender);
		
		UserServiceTx userServiceTx = new UserServiceTx();
		userServiceTx.setTransactionManager(transactionManager);
		userServiceTx.setUserService(testUserService); //주입
		
		//전체 삭제
		userDao.deleteAll();
		
		//user입력
		for(User user : users) {
			userDao.add(user);
		}
		try {
			
			userServiceTx.upgradeLevels();
			
		}catch(TestUserServiceException t) {
			
		}
		//트랜잭션처리 되면 Level.BASIC rollback; 
		checkUser(users.get(1), Level.BASIC);
		
	}

}

class TestUserService extends UserServiceImpl{
	private String id;
	
	public TestUserService(String id) {
		this.id = id;
	}
	public void upgradeLevel(User user) { //재정의
		if(user.getU_id().equals(id)) {
			throw new TestUserServiceException();
		}
		super.upgradeLevel(user);
	}
	
}

class TestUserServiceException extends RuntimeException{
	public TestUserServiceException() {
		System.out.println("==============================");
		System.out.println("=TestUserServiceException()=");
		System.out.println("==============================");
		
	}
}
