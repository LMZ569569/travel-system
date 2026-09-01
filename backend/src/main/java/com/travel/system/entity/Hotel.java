package com.travel.system.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 酒店实体类，对应 hotel 表
 */
public class Hotel {

    private Long id;                // 酒店ID
    private Long userId;            // 添加者（NULL=系统内置示例）
    private String name;            // 酒店名称
    private String city;            // 所在城市
    private String type;            // 酒店类型：经济型/舒适型/豪华型/民宿
    private String address;         // 地址
    private BigDecimal latitude;    // 纬度
    private BigDecimal longitude;   // 经度
    private BigDecimal price;       // 参考房价
    private Date createdAt;         // 创建时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
