package com.travel.system.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 景点实体类，对应数据库 scenic_spot 表
 */
public class ScenicSpot {

    private Long id;                 // 景点ID
    private String name;             // 景点名称
    private String description;      // 景点介绍
    private String province;         // 所在省份
    private String city;             // 所在城市
    private String level;            // A级评定（5A/4A/3A）
    private String address;          // 详细地址
    private BigDecimal latitude;     // 纬度
    private BigDecimal longitude;    // 经度
    private String image;            // 图片URL
    private BigDecimal price;        // 门票价格
    private String openTime;         // 开放时间
    private BigDecimal rating;       // 用户评分
    private BigDecimal visitDuration; // 建议游玩时长(小时)
    private Date createdAt;          // 录入时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public BigDecimal getVisitDuration() {
        return visitDuration;
    }

    public void setVisitDuration(BigDecimal visitDuration) {
        this.visitDuration = visitDuration;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
