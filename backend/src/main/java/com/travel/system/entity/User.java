package com.travel.system.entity;

import java.util.Date;

/**
 * 用户实体类，对应数据库 user 表
 */
public class User {

    private Long id;            // 用户ID
    private String username;    // 用户名
    private String password;    // 密码（MD5加密存储）
    private String nickname;    // 昵称
    private String avatar;      // 头像URL
    private String phone;       // 手机号
    private Date createdAt;     // 注册时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
