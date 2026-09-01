package com.travel.system.entity;

import java.util.Date;

/**
 * 行程组实体类，对应 trip 表
 * 一次出游可去多个城市（跨省），每个城市一段行程
 */
public class Trip {

    private Long id;                // 行程组ID
    private Long userId;            // 所属用户
    private String title;           // 行程标题
    private String originCity;      // 出发城市
    private Date startDate;         // 开始日期
    private Date endDate;           // 结束日期
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
