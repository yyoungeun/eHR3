package com.ehr;

import static com.ehr.service.UserServiceImpl.MIN_LOGINCOUNT_FOR_SILVER;
import static com.ehr.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ehr.service.UserService;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")

public class UserDaoJunitFinal {
	
	private Logger LOG = Logger.getLogger(UserDaoJunitFinal.class);
	
	@Autowired
	ApplicationContext context;
	
	private UserDao dao;
	
	List<User> users;
	
	@Autowired
	UserService userService;
	
	@Before
	public void setUp() {
		LOG.debug("^^^^^^^^^^^^^^^^^^");
		LOG.debug("setUp()");
		LOG.debug("^^^^^^^^^^^^^^^^^^");
		
		users = Arrays.asList(
				 new User("j01_134","송영은01","1234",Level.BASIC,MIN_LOGINCOUNT_FOR_SILVER-1,0,"1150amy@naver.com","2019/08/23")
				,new User("j02_134","송영은02","1234",Level.BASIC,MIN_LOGINCOUNT_FOR_SILVER,0,"1150amy@naver.com","2019/08/23") //BASIC -> SILVER
				,new User("j03_134","송영은03","1234",Level.SILVER,50,MIN_RECCOMEND_FOR_GOLD-1,"1150amy@naver.com","2019/08/23")
				,new User("j04_134","송영은04","1234",Level.SILVER,50,MIN_RECCOMEND_FOR_GOLD,"1150amy@naver.com","2019/08/23") //SILVER -> GOLD
				,new User("j05_134","송영은05","1234",Level.GOLD,99,99,"1150amy@naver.com","2019/08/23")
				);
		
		dao = context.getBean("userDao", UserDaoJdbc.class);
		
		LOG.debug("========================");
		LOG.debug("context:"+context);
		LOG.debug("dao:"+dao);
		LOG.debug("========================");
		
	}
	
	@After
	public void tearDown() {
		LOG.debug("^^^^^^^^^^^^^^^^^^");
		LOG.debug("99 tearDown()");
		LOG.debug("^^^^^^^^^^^^^^^^^^");
	}
	
	private void checkUser(User user, Level expectedLevel) {
		User userUpdate = dao.get(user.getU_id());
		assertThat(userUpdate.gethLevel(), is(expectedLevel));
	}
	
	@Test
	@Ignore
	public void upgradeAllOrNothing() throws SQLException {
		//전체 삭제
		dao.deleteAll();
		
		//users입력
		for(User user: users) {
			dao.add(user);
		}
		
		try {
			
			userService.upgradeLevels();
			
		}catch(RuntimeException t) {
			LOG.debug("======================================");
			LOG.debug("=RuntimeException=" + t.getMessage());
			LOG.debug("======================================");
		}
		//트랜잭션처리 되면 Level.BASIC rollback;
		LOG.debug("rollback");
		checkUser(users.get(1), Level.BASIC);
	}
	
	@Test
	@Ignore
	public void update() {
		
		LOG.debug("======================================");
		LOG.debug("=01. 기존 데이터 삭제=");
		LOG.debug("======================================");
		for(User user:users) {
			dao.deleteUser(user);
		}
		
		LOG.debug("======================================");
		LOG.debug("=02. 데이터 입력=");
		LOG.debug("======================================");
		dao.add(users.get(0));
		
		LOG.debug("======================================");
		LOG.debug("=03. 입력데이터 수정=");
		LOG.debug("=03.1 user01 수정=");
		LOG.debug("=03.1 user01 업데이트 수행=");
		LOG.debug("======================================");
		
		User user01 = users.get(0);
		user01.setName("송영은U");
		user01.setPasswd("1234U");
		user01.sethLevel(Level.GOLD);
		user01.setLogin(99);
		user01.setRecommend(999);
		user01.setEmail("1150am@naver.com");
		
		dao.update(user01);
		
		LOG.debug("======================================");
		LOG.debug("=04. 수정데이터와 3번 비교=");
		LOG.debug("======================================");
		User vsUser = dao.get(user01.getU_id());
		
		assertTrue(user01.equals(vsUser));
		
	}
	
	@Test
	public void retrieve() {
		Search vo = new Search(10,1,"",""); //생성자 참고해서 하드코딩
		List<User> list = dao.retrieve(vo); //List에 담아야함. User에 대한내용을
		
		for(User user : list) {
			LOG.debug(user);
		}
	}
	
	@Test
	public void getAll() throws SQLException {
//		dao.deleteUser(user01);
//		dao.deleteUser(user02);
//		dao.deleteUser(user03);
		
		List<User> list = dao.getAll();
		for(User user:list) {
			LOG.debug(user);
		}
	}
	
	@Test(expected = EmptyResultDataAccessException.class) //예외 발생 시 true
	@Ignore
	public void getFailure() throws ClassNotFoundException, SQLException {
		LOG.debug("======================================");
		LOG.debug("=01. 기존 데이터 삭제=");
		LOG.debug("======================================");
		for(User user:users) {
			dao.deleteUser(user);
		}
		assertThat(dao.count("_134"), is(0));
		dao.get("unknownUserId");
	}
	
	@Test
	@Ignore
	public void count() throws ClassNotFoundException, SQLException {
		LOG.debug("======================================");
		LOG.debug("=01. 기존 데이터 삭제=");
		LOG.debug("======================================");
		for(User user:users) {
			dao.deleteUser(user);
		}
		assertThat(dao.count("_134"), is(0));
		
		//----------------------------------
		//1건 추가
		//----------------------------------
		dao.add(users.get(0));
		assertThat(dao.count("_134"), is(1));
		
		//----------------------------------
		//1건 추가
		//----------------------------------
		dao.add(users.get(1));
		assertThat(dao.count("_134"), is(2));
		
		//----------------------------------
		//1건 추가
		//----------------------------------
		dao.add(users.get(2));
		assertThat(dao.count("_134"), is(3));

		
		dao.add(users.get(3));
		assertThat(dao.count("_134"), is(4));
		
		dao.add(users.get(4));
		assertThat(dao.count("_134"), is(5));
		
		//----------------------------------
		//count:3
		//----------------------------------
		
	}
	
	@Test(timeout=5000) //1.JUnit에게 테스트용 메소드임을 알려줌
	@Ignore //테스트할 때 스킵함
	public void addAndGet() { //2.public
		LOG.debug("======================================");
		LOG.debug("=01. 기존 데이터 삭제=");
		LOG.debug("======================================");
		for(User user:users) {
			dao.deleteUser(user);
		}
		
		LOG.debug("======================================");
		LOG.debug("=02. 단건등록=");
		LOG.debug("======================================");
        int flag = dao.add(users.get(0));         
        flag = dao.add(users.get(1));         
        flag = dao.add(users.get(2));   
        flag = dao.add(users.get(3)); 
        flag = dao.add(users.get(4));    
	    LOG.debug("======================================");
	    LOG.debug("=01.01 add flag="+flag);
	    LOG.debug("======================================");
		assertThat(flag, is(1));
        assertThat(dao.count("_134"), is(5)); //3건 넣었으니 3개여야 함
        
        User  userOne = dao.get(users.get(0).getU_id());
		
		LOG.debug("======================================");
		LOG.debug("=02. 단건조회=");
		LOG.debug("======================================");
		assertThat(userOne.getU_id(), is(users.get(0).getU_id()));
		assertThat(userOne.getPasswd(), is(users.get(0).getPasswd()));
		assertThat(userOne.getPasswd(), is(users.get(0).getPasswd()));
			
	}
}
