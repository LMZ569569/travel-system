package com.travel.system.mapper;

import com.travel.system.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户数据访问层
 */
@Mapper
public interface UserMapper {

    /** 根据用户名查询用户（注册时判断是否已存在、登录时校验） */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /** 新增用户 */
    @Insert("INSERT INTO user (username, password, nickname) VALUES (#{username}, #{password}, #{nickname})")
    int insert(User user);
}
