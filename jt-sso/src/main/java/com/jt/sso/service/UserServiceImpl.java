package com.jt.sso.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jt.common.service.HttpClientService;
import com.jt.common.vo.SysResult;
import com.jt.sso.mapper.UserMapper;
import com.jt.sso.pojo.User;

import redis.clients.jedis.JedisCluster;
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private JedisCluster jedisCluster;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public boolean findCheckUser(String param, int type) {
		String column=null;
		switch (type) {
		case 1:column="username";
			break;
		case 2:column="phone";
		break;
		case 3:column="email";
		break;
		}
		int count=userMapper.findCheckUser(param,column);
		return count==0?false:true;
	}
	@Override
		public void saveUser(User user) {
			String md5Pass = DigestUtils.md5Hex(user.getPassword());
			user.setPassword(md5Pass);//将密码进行加密
			user.setEmail(user.getPhone());//暂时代替,否则入库报错
			user.setCreated(new Date());
			user.setUpdated(user.getCreated());
			userMapper.insert(user);
		}
	@Override
	public String findUserByup(User user) {
		user.setPassword(DigestUtils.md5Hex(user.getPassword()));
		User userDB = userMapper.findUserByUP(user);
		
		String returnToken = null;
		if(userDB == null){
			throw new RuntimeException();
		}
		
		String token = "JT_TICKET_" + System.currentTimeMillis() + user.getUsername();
		returnToken = DigestUtils.md5Hex(token);
		try {
			String userJSON = objectMapper.writeValueAsString(userDB);
			jedisCluster.setex(returnToken, 3600 * 24 * 7, userJSON);
			System.out.println("用户单点登录成功!!!");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
		return returnToken;
	}
}
