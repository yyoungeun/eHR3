package com.ehr;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class UserDaoJdbc implements UserDao {

	static final Logger LOG = Logger.getLogger(UserDaoJdbc.class);
	
	private RowMapper<User> userMapper = new RowMapper<User>() {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User tmp=new User();
			tmp.setU_id(rs.getString("u_id"));
			tmp.setName(rs.getString("name"));
			tmp.setPasswd(rs.getString("passwd"));
			tmp.sethLevel(Level.valueOf(rs.getInt("h_level")));
			tmp.setLogin(rs.getInt("login"));
			tmp.setRecommend(rs.getInt("recommend"));
			tmp.setEmail(rs.getString("email"));
			tmp.setRegDt(rs.getString("reg_dt"));
			tmp.setNum(rs.getInt("rnum"));
			tmp.setTotalCnt(rs.getInt("total_cnt"));
			return tmp;
		}
	};
	
	private JdbcTemplate jdbcTemplate;
	
	private DataSource dataSource;
	
	public UserDaoJdbc() {}
	
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		this.dataSource = dataSource;
	}
	
	@Override
	public User get(String id) {
		User outVO=null;
		
		//----------------------------------------
		//02.SQL작성
		//----------------------------------------		
		StringBuilder sb=new StringBuilder();
		sb.append(" SELECT         							 \n");
		sb.append("     u_id,      							 \n");
		sb.append("     name,      							 \n");
		sb.append("     passwd,    							 \n");
		sb.append("     h_level,    						 \n");
		sb.append("     login,     							 \n");
		sb.append("     recommend,  						 \n");
		sb.append("     email,      						 \n");
		sb.append("     TO_CHAR(reg_dt, 'YYYY/MM/DD') reg_dt, \n");
		//rowMapper를 retrieve와 공유해서 컬럼 수를 맞추기 위해 추가한 쿼리
		sb.append("     1 as rnum,   						 \n");
		sb.append("     1 as total_cnt			   		     \n");
		sb.append(" FROM users      						 \n");
		sb.append(" WHERE u_id = ?  						 \n");
		
		LOG.debug("=============================");
		LOG.debug("02. sql=\n"+sb.toString());
		LOG.debug("=============================");		
		
		outVO = jdbcTemplate.queryForObject(sb.toString()
				, new Object[] {id}
				, userMapper);
		
		//----------------------------------------
		//06.outVO==null 예외발생
		//----------------------------------------
		if(null == outVO) {
			throw new EmptyResultDataAccessException(1);
		}
		
		return outVO;
	}

	@Override
	public int add(User user) {
		int flag = 0;
		StringBuilder sb=new StringBuilder();
		sb.append("  INSERT INTO users (  \n");
		sb.append("      u_id,            \n");
		sb.append("      name,            \n");
		sb.append("      passwd,          \n");
		sb.append("      h_level,         \n");
		sb.append("      login,           \n");
		sb.append("      recommend,       \n");
		sb.append("      email,           \n");
		sb.append("      reg_dt           \n");
		sb.append("  ) VALUES (           \n");
		sb.append("      ?,               \n");
		sb.append("      ?,               \n");
		sb.append("      ?,               \n");
		sb.append("      ?,               \n");
		sb.append("      ?,               \n");
		sb.append("      ?,               \n");
		sb.append("      ?,               \n");
		sb.append("      SYSDATE          \n");
		sb.append("  )                    \n");
		
		LOG.debug("=============================");
		LOG.debug("02. sql=\n"+sb.toString());
		LOG.debug("=============================");		
		
		String query = sb.toString();
		Object[] args = {user.getU_id(), user.getName(), user.getPasswd(), user.gethLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail()};
		
		LOG.debug("=============================");
		LOG.debug("03. param:"+user.toString());
		LOG.debug("=============================");		
		
		flag = jdbcTemplate.update(query, args);
		
		return flag;
	}
	
	@Override
	public int deleteAll() {
		String query = "DELETE FROM users";
		int flag = jdbcTemplate.update(query);		
		return flag;
	}

	@Override
	public int deleteUser(User user) {
		String query = "DELETE FROM users WHERE u_id = ?";
		Object[] args = {user.getU_id()};
		int flag = jdbcTemplate.update(query, args);		
		return flag;
	}

	@Override
	public int count(String uId) {
		int cnt = 0;//조회 카운트		
		
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT COUNT(*) cnt      \n");
		sb.append(" FROM users               \n");
		sb.append(" WHERE u_id LIKE ?        \n");
		
		LOG.debug("================================");
		LOG.debug("2.sql:"+sb.toString());
		LOG.debug("================================");
		
		cnt = jdbcTemplate.queryForObject(sb.toString(), new Object[] {"%"+uId+"%"}, Integer.class);
		
		return cnt;
	}

	@Override
	public List<User> getAll() {
		String query = "SELECT u_id, name, passwd, h_level, login, recommend, email, TO_CHAR(reg_dt, 'YYYY/MM/DD') reg_dt, 1 as rnum, 1 as total_cnt FROM users ORDER BY u_id";
		LOG.debug("query:"+query);
		List<User> list = jdbcTemplate.query(query, userMapper);
		return list;
	}

	@Override
	public List<User> retrieve(Search vo) {
		//U_ID : 10
		//NAME : 20
		//EMAIL: 30
		
		StringBuilder param=new StringBuilder();
		
		//검색조건이 선택되면
		if(null !=vo && !"".equals(vo.getSearchDiv())) {
			//아이디 검색
			if("10".equals(vo.getSearchDiv())) {
				param.append(" WHERE u_id like '%'|| ? ||'%' \n");
			//이름	
			}else if("20".equals(vo.getSearchDiv())) {
				param.append(" WHERE name like '%'|| ? ||'%' \n");
			//이메일	
			}else if("30".equals(vo.getSearchDiv())) {
				param.append(" WHERE email like '%'|| ? ||'%' \n");
			}
			
		}
		
		StringBuilder sb=new StringBuilder();
		sb.append(" SELECT T1.*,T2.*                                                    \n");
		sb.append(" FROM(                                                               \n");
		sb.append("      SELECT B.u_id,                                                 \n");
		sb.append("             B.name,                                                 \n");
		sb.append("             B.passwd,                                               \n");
		sb.append("             B.h_level,                                              \n");
		sb.append("             B.login,                                                \n");
		sb.append("             B.recommend,                                            \n");
		sb.append("             B.email,                                                \n");
		sb.append("             TO_CHAR(B.reg_dt,'YYYY/MM/DD') reg_dt,                  \n");
		sb.append("             B.rnum                                                  \n");
		sb.append("      FROM(                                                          \n");
		sb.append("          SELECT ROWNUM AS rnum,A.*                                  \n");
		sb.append("          FROM(                                                      \n");
		sb.append("               SELECT *                                              \n");
		sb.append("               FROM users                                            \n");
		//----------------------------------------------------------------------------------
		// where
		//----------------------------------------------------------------------------------
		sb.append("               --SEARCH CONDITION                                    \n");
		sb.append(param.toString());
		
		sb.append("               ORDER BY reg_dt DESC                                  \n");
		//sb.append("          )A WHERE ROWNUM <=(&PAGE_SIZE*(&PAGE_NUM-1)+&PAGE_SIZE)    \n");
		//sb.append("      )B WHERE B.rnum >=(&PAGE_SIZE*(&PAGE_NUM-1)+1)                 \n");
		sb.append("          )A WHERE ROWNUM <=(?*(?-1)+?)    \n");
		sb.append("      )B WHERE B.rnum >=(?*(?-1)+1)                 \n");		
		sb.append("  )T1                                                                \n");
		sb.append("  CROSS JOIN                                                         \n");
		sb.append("  (SELECT COUNT(*) total_cnt                                         \n");
		sb.append("   FROM users                                                        \n");
		//----------------------------------------------------------------------------------
		// where
		//----------------------------------------------------------------------------------
		sb.append("    --SEARCH CONDITION                                               \n");
		sb.append(param.toString());				
		sb.append("  )T2                                                                \n");		
		
		LOG.debug("=============================");
		LOG.debug("01. sql=\n"+sb.toString());
		LOG.debug("=============================");	
		
		
		LOG.debug("=============================");
		LOG.debug("02. param=\n"+vo);
		LOG.debug("=============================");			
		//param to List<Object>
		List<Object> listArgs=new ArrayList<Object>();
		
		if(null !=vo && !"".equals(vo.getSearchDiv())) {
			listArgs.add(vo.getSearchWord());
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageNum());
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageNum());
			listArgs.add(vo.getSearchWord());
		}else {
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageNum());
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageSize());
			listArgs.add(vo.getPageNum());
		}
		LOG.debug("=============================");
		LOG.debug("03. listArgs=\n"+listArgs);
		LOG.debug("=============================");			
		
		return jdbcTemplate.query(sb.toString()
				, listArgs.toArray()
				, userMapper);
	}

	@Override
	public int update(User user) {
		int flag = 0;
		StringBuilder sb = new StringBuilder();
		sb.append(" UPDATE users       \n");
		sb.append(" SET                \n");
		sb.append("     name = ?       \n");
		sb.append("     ,passwd = ?    \n");
		sb.append("     ,h_level = ?   \n");
		sb.append("     ,login = ?     \n");
		sb.append("     ,recommend = ? \n");
		sb.append("     ,email = ?     \n");
		sb.append("     ,reg_dt = SYSDATE \n");
		sb.append(" WHERE              \n");
		sb.append("     u_id = ?       \n");
		
		LOG.debug("=============================");
		LOG.debug("01. sql=\n"+sb.toString());
		LOG.debug("=============================");	
		
		List<Object> listArgs=new ArrayList<Object>();
		
		String query = sb.toString();
		Object[] args = {user.getName()
						,user.getPasswd()
						,user.gethLevel().intValue()
						,user.getLogin()
						,user.getRecommend()
						,user.getEmail()
						,user.getU_id()
					};
		
		LOG.debug("=============================");
		LOG.debug("03. param:"+user.toString());
		LOG.debug("=============================");		
		
		flag = jdbcTemplate.update(query, args);
		
		return flag;
	}

}
