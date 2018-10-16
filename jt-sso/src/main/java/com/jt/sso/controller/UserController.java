package com.jt.sso.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.SysResult;
import com.jt.sso.pojo.User;
import com.jt.sso.service.UserService;

import redis.clients.jedis.JedisCluster;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private JedisCluster jedisCluster;
	
	@RequestMapping("/check/{param}/{type}")
	@ResponseBody
	public MappingJacksonValue checkUser(@PathVariable String param,@PathVariable int type,String callback){
		boolean flag=userService.findCheckUser(param,type);
		MappingJacksonValue value=new MappingJacksonValue(SysResult.oK(flag));
		value.setJsonpFunction(callback);
		return value;
	}
	@RequestMapping("/register")
	@ResponseBody
	public SysResult saveUser(User user){
		try {
			userService.saveUser(user);
			return SysResult.oK();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SysResult.build(201, "新增失败");
	}
	/**
	 * 校验用户信息
	 * @param user
	 * @return
	 */
	@RequestMapping("/login")
	@ResponseBody
	public SysResult findUserByUP(User user){
		
		try {
			String token=userService.findUserByup(user);
			if (StringUtils.isEmpty(token)) {
				return SysResult.build(201,"用户查询失败");
			}
			return SysResult.oK(token);//如果查询到数据,就将token返回
		} catch (Exception e) {
			e.printStackTrace();	
		}
		return SysResult.build(201,"用户查询失败");
	}
	/**
	 * 根据cookie查询用户,在首页回显
	 */
	@RequestMapping("/query/{token}")
	@ResponseBody
	public MappingJacksonValue findUserByTicket(
			@PathVariable String token,
			String callback){
		//根据token获取用户信息的json串
		String userJSON = jedisCluster.get(token);
		MappingJacksonValue jacksonValue = null;//spring中用于跨域访问的jsonp
		//对结果进行判断
		if(!StringUtils.isEmpty(userJSON)){
			//redis中的数据不为null
			jacksonValue = new MappingJacksonValue(SysResult.oK(userJSON));
		}else{
			jacksonValue = new MappingJacksonValue(SysResult.build(201,"用户查询失败"));
		}
		jacksonValue.setJsonpFunction(callback);//设置回调名称
		return jacksonValue;
	}
	/**
	 * 用户退出操作
	 */
	
}
