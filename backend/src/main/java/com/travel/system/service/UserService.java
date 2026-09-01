package com.travel.system.service;

import com.travel.system.dto.LoginResponse;

/**
 * 用户业务层接口
 */
public interface UserService {

    /** 注册，成功返回 token+用户，失败返回 null */
    LoginResponse register(String username, String password, String nickname);

    /** 登录，成功返回 token+用户，失败返回 null */
    LoginResponse login(String username, String password);
}
