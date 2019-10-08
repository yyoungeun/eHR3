package com.ehr;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContextAfter.xml")
public class AspectJTestAfter {
	private final Logger LOG = Logger.getLogger(AspectJTestAfter.class);
	
	@Autowired
	ApplicationContext context;
	
	@Before
	public void setUp() {
		LOG.debug("context: " + context);
	}
	
	@Test
	public void aspectTest() {
		CommonDao dao = (CommonDao) context.getBean("memberDaoImpl");
		LOG.debug("dao: " + dao); //객체 생성
		
		dao.do_save();
		dao.selectOne();
		
	}

}
