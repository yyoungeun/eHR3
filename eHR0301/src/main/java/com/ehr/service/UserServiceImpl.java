package com.ehr.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.ehr.Level;
import com.ehr.User;
import com.ehr.UserDao;

public class UserServiceImpl implements UserService {
	private Logger LOG = Logger.getLogger(this.getClass());
	
	private MailSender mailSender;
	
	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

//	private PlatformTransactionManager transactionManager;
//	
//	public void setTransactionManager(PlatformTransactionManager transactionManager) {
//		this.transactionManager = transactionManager;
//	}

	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	private UserDao userDao;
	public static final int MIN_LOGINCOUNT_FOR_SILVER = 50;
	public static final int MIN_RECCOMEND_FOR_GOLD = 30;
	
	public void setUserDao(UserDao userDao) { //주입을 위한 setter
		this.userDao = userDao;
	}
	
	public void add(User user) {
		if(null == user.gethLevel())user.sethLevel(Level.BASIC);
		
		userDao.add(user);
	}
	
	//level upgrade
	//1. 전체 사용자를 조회
	//2. 대상자를 선별
	//	2.1 BASIC사용자,  로그인 cnt 50이상이면: Basic -> SILVER
	//	2.1 SILVER사용자, 추천 cnt 30이상이면: SILVER -> GOLD
	//	2.3 GOLD 대상 아님.
	//3. 대상자를 업그레이드 레벨선정 및 upgrade
	
//	public void upgradeLevels() {
//		int  upCnt = 0;
//		//1. 전체 사용자를 조회
//		List<User> users = userDao.getAll();
//		
//		for(User user :users) {
//			Boolean changed = null;
//			//BASIC -> SILVER
//			if(user.gethLevel()==Level.BASIC && user.getLogin()>= 50) {
//				user.sethLevel(Level.SILVER);
//				changed = true;
//			
//			//SILVER -> GOLD
//			}else if(user.gethLevel()==Level.SILVER && user.getRecommend() >= 30) {
//				user.sethLevel(Level.GOLD);
//				changed = true;
//				
//			}else if(user.gethLevel() == Level.GOLD) {
//				changed = false;
//			}else {
//				changed = false;
//			}
//			if(changed == true) {
//				userDao.update(user);
//				upCnt++;
//			}
//		}//for
//		
//		LOG.debug("--------------------------------------------");
//		LOG.debug("===upCnt =" + upCnt);
//		LOG.debug("--------------------------------------------");
//	}
	
	protected void upgradeLevel(User user) {
//		if(user.gethLevel()==Level.BASIC) {
//			user.sethLevel(Level.SILVER);
//		}else if(user.gethLevel()==Level.SILVER) {
//			user.sethLevel(Level.GOLD);
//		}
		user.upgradeLevel();
		userDao.update(user);
		sendUpgradeMail(user); //mail send호출
	}
	
	/**
	 * 등업 사용자에게 mail 전송.
	 * @param user
	 */
	private void sendUpgradeMail(User user) {
		try {
			
//			POP 서버명 : pop.naver.com
//			SMTP 서버명 : smtp.naver.com
//			POP 포트 : 995, 보안연결(SSL) 필요
//			SMTP 포트 : 465, 보안 연결(SSL) 필요
//			아이디 : 1150amy
//			비밀번호 : 네이버 로그인 비밀번호
			
			//보내는 사람
			String host ="smtp.naver.com";
			String userName = "1150amy";
			String password = "@song7014";
			int port = 465;
			
			//받는사람
			String recipient = user.getEmail();
			//제목
			String title = user.getName()+"님 등업(https://cafe.naver.com/kndjang)";
			//내용
			String contents = user.getU_id()+ "님의 등급이 \n" + user.gethLevel().name()+"로 등업되었습니다.";
			
			Properties  props = System.getProperties();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", port);
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.ssl.trust",host);
			
			//인증
			Session session = Session.getInstance(props, new Authenticator() {
				String uName = userName;
				String passwd = password;
				
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					
					return new PasswordAuthentication(uName,passwd);
				}
			});
			
			session.setDebug(true);
			
//			Message mimeMessage = new MimeMessage(session);
			
//			//보내는 사람
//			mimeMessage.setFrom(new InternetAddress("1150amy@naver.com"));
//			//받는사람
//			mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
//			//제목
//			mimeMessage.setSubject(title);
//			//내용
//			mimeMessage.setText(contents);
			
			//전송
//			Transport.send(mimeMessage);
			SimpleMailMessage mimeMessage = new SimpleMailMessage();
			//보내는 사람
			mimeMessage.setFrom("1150amy@naver.com");
			//받는사람
			mimeMessage.setTo(recipient);
			//제목
			mimeMessage.setSubject(title);
			//내용
			mimeMessage.setText(contents);
			
			this.mailSender.send(mimeMessage);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		LOG.debug("===============================================");
		LOG.debug("=mail send=");
		LOG.debug("===============================================");
	}
	
	//두가지 기능 분리(upgrade대상, upCnt)
	//level upgrade
	//1. 전체 사용자를 조회
	//2. 대상자를 선별
	//	2.1 BASIC사용자,  로그인 cnt 50이상이면: Basic -> SILVER
	//	2.1 SILVER사용자, 추천 cnt 30이상이면: SILVER -> GOLD
	//	2.3 GOLD 대상 아님.
	//3. 대상자를 업그레이드 레벨선정 및 upgrade
	
	public void upgradeLevels() throws SQLException {
		int upCnt = 0;
		
		List<User> users = userDao.getAll(); //전체사용자 꺼내기
		
			for(User user : users) {
				if(canUpgradeLevel(user) == true) {
					upgradeLevel(user);
					upCnt++;
				}
			
			}//for
	}
	
	//update대상 여부 파악: true
	private boolean canUpgradeLevel(User user) {
		Level currLevel = user.gethLevel();
		
		switch(currLevel) {
			case BASIC:
				return (user.getLogin() >= MIN_LOGINCOUNT_FOR_SILVER);
			case SILVER:
				return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
			case GOLD:
				return false;
			default:
				throw new IllegalArgumentException("Unknown Level: "+ currLevel); //예외
			}
	}
	
}
