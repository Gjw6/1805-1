package com.jt.sso.service;

import com.jt.sso.pojo.User;

public interface UserService {

	boolean findCheckUser(String param, int type);

	void saveUser(User user);

	String findUserByup(User user);

}
