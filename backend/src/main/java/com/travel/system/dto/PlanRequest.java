package com.travel.system.dto;

import java.util.List;

/**
 * 旅游规划请求参数
 *
 * 新版支持：
 * - 出发城市 + 出行方式（自驾/火车/飞机）
 * - 日期范围（startDate ~ endDate）替代固定天数
 * - 多城市游玩（可跨省）
 *
 * 兼容旧版：只填 province/city/days 时按旧逻辑规划
 */
public class PlanRequest {

    private String originCity;        // 出发城市
    private String transportMode;     // 出行方式：self-drive/train/flight
    private String startDate;         // 开始日期 yyyy-MM-dd
    private String endDate;           // 结束日期 yyyy-MM-dd
    private Integer budget;           // 总预算（元），可不填

    // 多城市目的地（新版主推）
    private List<CityPlan> cities;

    // 旧版兼容字段
    private String province;          // 目的地省份
    private String city;              // 目的地城市
    private Integer days;             // 游玩天数

    /** 一个目的地城市 */
    public static class CityPlan {
        private String province;      // 省份
        private String city;          // 城市
        private Long hotelId;         // 可选：指定酒店
        private Integer days;         // 可选：该城市停留天数（不填则自动分配）

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

        public Long getHotelId() {
            return hotelId;
        }

        public void setHotelId(Long hotelId) {
            this.hotelId = hotelId;
        }

        public Integer getDays() {
            return days;
        }

        public void setDays(Integer days) {
            this.days = days;
        }
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Integer getBudget() {
        return budget;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public List<CityPlan> getCities() {
        return cities;
    }

    public void setCities(List<CityPlan> cities) {
        this.cities = cities;
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

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
