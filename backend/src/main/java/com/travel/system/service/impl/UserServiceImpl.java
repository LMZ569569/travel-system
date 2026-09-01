package com.travel.system.service.impl;

import com.travel.system.entity.User;
import com.travel.system.mapper.UserMapper;
import com.travel.system.service.UserService;
import com.travel.system.dto.LoginResponse;
import com.travel.system.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户业务层实现
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginResponse register(String username, String password, String nickname) {
        if (userMapper.findByUsername(username) != null) {
            return null;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setNickname(nickname);
        userMapper.insert(user);
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(token, user);
    }

    @Override
    public LoginResponse login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return null;
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            return null;
        }
        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(token, user);
    }
}
