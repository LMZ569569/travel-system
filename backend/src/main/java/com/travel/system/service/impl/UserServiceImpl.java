package com.travel.system.service.impl;

import com.travel.system.entity.User;
import com.travel.system.mapper.UserMapper;
import com.travel.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户业务层实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User register(String username, String password, String nickname) {
        // 用户名已存在则返回 null
        if (userMapper.findByUsername(username) != null) {
            return null;
        }
        User user = new User();
        user.setUsername(username);
        // 密码用 MD5 加密后存储，不存明文
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setNickname(nickname);
        userMapper.insert(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        // 用户不存在或密码不匹配则返回 null
        if (user == null) {
            return null;
        }
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            return null;
        }
        return user;
    }
}
