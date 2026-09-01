package com.travel.system.service;

import com.travel.system.entity.User;

/**
 * 用户业务层接口
 */
public interface UserService {

    /** 注册，成功返回用户，失败返回 null */
    User register(String username, String password, String nickname);

    /** 登录，成功返回用户，失败返回 null */
    User login(String username, String password);
}
