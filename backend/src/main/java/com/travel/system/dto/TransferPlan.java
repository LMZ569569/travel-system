package com.travel.system.dto;

import com.travel.system.entity.TrainSchedule;
import com.travel.system.entity.FlightSchedule;

public class TransferPlan {
    private String type;          // "train" or "flight"
    private String transferCity;  // 中转城市
    private String firstLeg;      // 第一段：车次号
    private String firstFrom;
    private String firstTo;
    private String firstDepart;
    private String firstArrive;
    private double firstPrice;
    private String secondLeg;     // 第二段：车次号
    private String secondFrom;
    private String secondTo;
    private String secondDepart;
    private String secondArrive;
    private double secondPrice;
    private double totalPrice;
    private int totalMinutes;

    public TransferPlan() {}

    public TransferPlan(String type, String transferCity,
                        TrainSchedule leg1, TrainSchedule leg2) {
        this.type = type;
        this.transferCity = transferCity;
        this.firstLeg = leg1.getTrainNo();
        this.firstFrom = leg1.getFromCity();
        this.firstTo = leg1.getToCity();
        this.firstDepart = leg1.getDepartTime();
        this.firstArrive = leg1.getArriveTime();
        this.firstPrice = leg1.getPrice().doubleValue();
        this.secondLeg = leg2.getTrainNo();
        this.secondFrom = leg2.getFromCity();
        this.secondTo = leg2.getToCity();
        this.secondDepart = leg2.getDepartTime();
        this.secondArrive = leg2.getArriveTime();
        this.secondPrice = leg2.getPrice().doubleValue();
        this.totalPrice = this.firstPrice + this.secondPrice;
        this.totalMinutes = calcMinutes(leg1.getDepartTime(), leg2.getArriveTime());
    }

    public TransferPlan(String type, String transferCity,
                        FlightSchedule leg1, FlightSchedule leg2) {
        this.type = type;
        this.transferCity = transferCity;
        this.firstLeg = leg1.getFlightNo();
        this.firstFrom = leg1.getFromCity();
        this.firstTo = leg1.getToCity();
        this.firstDepart = leg1.getDepartTime();
        this.firstArrive = leg1.getArriveTime();
        this.firstPrice = leg1.getPrice().doubleValue();
        this.secondLeg = leg2.getFlightNo();
        this.secondFrom = leg2.getFromCity();
        this.secondTo = leg2.getToCity();
        this.secondDepart = leg2.getDepartTime();
        this.secondArrive = leg2.getArriveTime();
        this.secondPrice = leg2.getPrice().doubleValue();
        this.totalPrice = this.firstPrice + this.secondPrice;
        this.totalMinutes = calcMinutes(leg1.getDepartTime(), leg2.getArriveTime());
    }

    private int calcMinutes(String depart, String arrive) {
        try {
            String[] dp = depart.split(":");
            String[] ar = arrive.split(":");
            int d = Integer.parseInt(dp[0]) * 60 + Integer.parseInt(dp[1]);
            int a = Integer.parseInt(ar[0]) * 60 + Integer.parseInt(ar[1]);
            if (a < d) a += 24 * 60;
            return a - d;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTransferCity() { return transferCity; }
    public void setTransferCity(String transferCity) { this.transferCity = transferCity; }
    public String getFirstLeg() { return firstLeg; }
    public void setFirstLeg(String firstLeg) { this.firstLeg = firstLeg; }
    public String getFirstFrom() { return firstFrom; }
    public void setFirstFrom(String firstFrom) { this.firstFrom = firstFrom; }
    public String getFirstTo() { return firstTo; }
    public void setFirstTo(String firstTo) { this.firstTo = firstTo; }
    public String getFirstDepart() { return firstDepart; }
    public void setFirstDepart(String firstDepart) { this.firstDepart = firstDepart; }
    public String getFirstArrive() { return firstArrive; }
    public void setFirstArrive(String firstArrive) { this.firstArrive = firstArrive; }
    public double getFirstPrice() { return firstPrice; }
    public void setFirstPrice(double firstPrice) { this.firstPrice = firstPrice; }
    public String getSecondLeg() { return secondLeg; }
    public void setSecondLeg(String secondLeg) { this.secondLeg = secondLeg; }
    public String getSecondFrom() { return secondFrom; }
    public void setSecondFrom(String secondFrom) { this.secondFrom = secondFrom; }
    public String getSecondTo() { return secondTo; }
    public void setSecondTo(String secondTo) { this.secondTo = secondTo; }
    public String getSecondDepart() { return secondDepart; }
    public void setSecondDepart(String secondDepart) { this.secondDepart = secondDepart; }
    public String getSecondArrive() { return secondArrive; }
    public void setSecondArrive(String secondArrive) { this.secondArrive = secondArrive; }
    public double getSecondPrice() { return secondPrice; }
    public void setSecondPrice(double secondPrice) { this.secondPrice = secondPrice; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public int getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }
}