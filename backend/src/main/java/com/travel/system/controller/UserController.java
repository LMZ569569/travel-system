package com.travel.system.controller;

import com.travel.system.common.Result;
import com.travel.system.dto.LoginRequest;
import com.travel.system.dto.RegisterRequest;
import com.travel.system.entity.User;
import com.travel.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /** 注册 */
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request.getUsername(), request.getPassword(), request.getNickname());
        if (user == null) {
            return Result.error("用户名已存在");
        }
        return Result.success(user);
    }

    /** 登录 */
    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest request) {
        User user = userService.login(request.getUsername(), request.getPassword());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }
        return Result.success(user);
    }
}