package com.travel.system.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 旅游规划请求参数
 *
 * 新版支持：
 * - 出发城市 + 多段交通（可混合火车/飞机中转）
 * - 日期范围（startDate ~ endDate）
 * - 多城市游玩（可跨省）
 * - 返程支持
 *
 * 兼容旧版：只填 province/city/days 时按旧逻辑规划
 */
public class PlanRequest {

    private String originCity;        // 出发城市
    private String transportMode;     // 出行方式（旧版，保留兼容）
    private String startDate;         // 开始日期 yyyy-MM-dd
    private String endDate;           // 结束日期 yyyy-MM-dd
    private Integer budget;           // 总预算（元），可不填

    // 多段交通（新版，替换 transportMode）
    private List<TransportSegment> transportSegments;

    // 多城市目的地
    private List<CityPlan> cities;

    // 旧版兼容字段
    private String province;
    private String city;
    private Integer days;

    /** 一段交通（火车或飞机） */
    public static class TransportSegment {
        private String type;          // train / flight
        private String scheduleNo;    // 车次号/航班号
        private String fromCity;      // 出发城市
        private String toCity;        // 到达城市
        private String departTime;    // 出发时间 HH:mm
        private String arriveTime;    // 到达时间 HH:mm
        private BigDecimal price;     // 票价

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getScheduleNo() { return scheduleNo; }
        public void setScheduleNo(String scheduleNo) { this.scheduleNo = scheduleNo; }
        public String getFromCity() { return fromCity; }
        public void setFromCity(String fromCity) { this.fromCity = fromCity; }
        public String getToCity() { return toCity; }
        public void setToCity(String toCity) { this.toCity = toCity; }
        public String getDepartTime() { return departTime; }
        public void setDepartTime(String departTime) { this.departTime = departTime; }
        public String getArriveTime() { return arriveTime; }
        public void setArriveTime(String arriveTime) { this.arriveTime = arriveTime; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    /** 一个目的地城市 */
    public static class CityPlan {
        private String province;
        private String city;
        private Long hotelId;
        private Integer days;

        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public Long getHotelId() { return hotelId; }
        public void setHotelId(Long hotelId) { this.hotelId = hotelId; }
        public Integer getDays() { return days; }
        public void setDays(Integer days) { this.days = days; }
    }

    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }
    public String getTransportMode() { return transportMode; }
    public void setTransportMode(String transportMode) { this.transportMode = transportMode; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public Integer getBudget() { return budget; }
    public void setBudget(Integer budget) { this.budget = budget; }
    public List<TransportSegment> getTransportSegments() { return transportSegments; }
    public void setTransportSegments(List<TransportSegment> transportSegments) { this.transportSegments = transportSegments; }
    public List<CityPlan> getCities() { return cities; }
    public void setCities(List<CityPlan> cities) { this.cities = cities; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
}
