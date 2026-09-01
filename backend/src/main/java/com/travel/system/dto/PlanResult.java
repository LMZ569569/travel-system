package com.travel.system.dto;

import com.travel.system.entity.Hotel;
import com.travel.system.entity.ScenicSpot;

import java.math.BigDecimal;
import java.util.List;

public class PlanResult {

    public static class CityPlanResult {
        private String province;
        private String city;
        private Hotel hotel;
        private TransportInfo transport;
        private int days;
        private BigDecimal cost;
        private List<DayPlan> dayPlans;

        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public Hotel getHotel() { return hotel; }
        public void setHotel(Hotel hotel) { this.hotel = hotel; }
        public TransportInfo getTransport() { return transport; }
        public void setTransport(TransportInfo transport) { this.transport = transport; }
        public int getDays() { return days; }
        public void setDays(int days) { this.days = days; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public List<DayPlan> getDayPlans() { return dayPlans; }
        public void setDayPlans(List<DayPlan> dayPlans) { this.dayPlans = dayPlans; }
    }

    public static class DayPlan {
        private String date;
        private BigDecimal cost;
        private List<TimeSlot> slots;

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public List<TimeSlot> getSlots() { return slots; }
        public void setSlots(List<TimeSlot> slots) { this.slots = slots; }
    }

    public static class TimeSlot {
        private String type;
        private String name;
        private String startTime;
        private int duration;
        private BigDecimal cost;
        private String note;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    private String originCity;
    private String transportMode;
    private String startDate;
    private String endDate;
    private int days;
    private int dailyHours;
    private BigDecimal totalCost;
    private double avgRating;
    private int totalAttractions;
    private List<CityPlanResult> cities;
    private List<DayPlan> dayPlans;
    private String province;
    private String city;
    // 多段交通（完整保留，含中转段）
    private List<TransportInfo> transportSegments;

    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }
    public String getTransportMode() { return transportMode; }
    public void setTransportMode(String transportMode) { this.transportMode = transportMode; }
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }
    public int getDailyHours() { return dailyHours; }
    public void setDailyHours(int dailyHours) { this.dailyHours = dailyHours; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
    public int getTotalAttractions() { return totalAttractions; }
    public void setTotalAttractions(int totalAttractions) { this.totalAttractions = totalAttractions; }
    public List<CityPlanResult> getCities() { return cities; }
    public void setCities(List<CityPlanResult> cities) { this.cities = cities; }
    public List<DayPlan> getDayPlans() { return dayPlans; }
    public void setDayPlans(List<DayPlan> dayPlans) { this.dayPlans = dayPlans; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public List<TransportInfo> getTransportSegments() { return transportSegments; }
    public void setTransportSegments(List<TransportInfo> transportSegments) { this.transportSegments = transportSegments; }
}
