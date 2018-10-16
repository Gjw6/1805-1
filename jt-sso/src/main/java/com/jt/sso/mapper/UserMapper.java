package com.jt.sso.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.jt.common.mapper.SysMapper;
import com.jt.sso.pojo.User;

public interface UserMapper extends SysMapper<User>{

	int findCheckUser(@Param("param")String param, @Param("column")String column);
	
	@Select("select * from tb_user where username = #{username} and password = #{password}")
	User findUserByUP(User user);
	
}
