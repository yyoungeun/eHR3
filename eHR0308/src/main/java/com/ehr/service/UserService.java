package com.ehr.service;

import java.sql.SQLException;
import java.util.List;

import com.ehr.Search;
import com.ehr.User;

public interface UserService {
	
	public int update(User user);
	public List<User> retrieve(Search vo);
//	public int count(String uId);
	public int deleteUser(User user);
	public User get(String id);

	// 최초 사용자 베이직 레벨
	public void add(User user);

	public void upgradeLevels() throws SQLException;
}
