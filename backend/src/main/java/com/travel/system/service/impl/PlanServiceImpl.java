package com.travel.system.service.impl;

import com.travel.system.dto.PlanRequest;
import com.travel.system.dto.PlanResult;
import com.travel.system.dto.TransportInfo;
import com.travel.system.entity.FlightSchedule;
import com.travel.system.entity.Hotel;
import com.travel.system.entity.ScenicSpot;
import com.travel.system.entity.TrainSchedule;
import com.travel.system.mapper.FlightScheduleMapper;
import com.travel.system.mapper.HotelMapper;
import com.travel.system.mapper.ScenicSpotMapper;
import com.travel.system.mapper.TrainScheduleMapper;
import com.travel.system.service.PlanService;
import com.travel.system.service.TravelTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PlanServiceImpl implements PlanService {

    private static final int DAILY_HOURS = 8;
    private static final int DAY_START_MIN = 8 * 60;
    private static final int LUNCH_MIN = 12 * 60;
    private static final int DINNER_MIN = 18 * 60;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;
    @Autowired
    private HotelMapper hotelMapper;
    @Autowired
    private TrainScheduleMapper trainScheduleMapper;
    @Autowired
    private FlightScheduleMapper flightScheduleMapper;
    @Autowired
    private TravelTimeService travelTimeService;

    @Override
    public PlanResult plan(PlanRequest request) {
        List<PlanRequest.CityPlan> cities = normalizeCities(request);
        if (cities.isEmpty()) {
            return buildEmptyResult(request);
        }

        int totalDays = computeTotalDays(request, cities);
        List<List<ScenicSpot>> allCitySpots = new ArrayList<>();
        for (PlanRequest.CityPlan cp : cities) {
            allCitySpots.add(querySpots(cp));
        }

        List<Integer> cityDays = assignCityDays(cities, allCitySpots, totalDays);
        BigDecimal totalBudget = request.getBudget() == null ? null : BigDecimal.valueOf(request.getBudget());
        List<BigDecimal> cityBudgets = splitBudget(allCitySpots, totalBudget);

        PlanResult result = new PlanResult();
        result.setOriginCity(request.getOriginCity());
        result.setTransportMode(request.getTransportMode());
        result.setStartDate(request.getStartDate());
        result.setEndDate(request.getEndDate());
        result.setDays(totalDays);
        result.setDailyHours(DAILY_HOURS);

        // 预处理多段交通：为每个城市匹配到达该城市的交通段
        List<TransportInfo> cityTransports = new ArrayList<>();
        List<TransportInfo> allSegments = new ArrayList<>();
        if (request.getTransportSegments() != null && !request.getTransportSegments().isEmpty()) {
            for (int i = 0; i < cities.size(); i++) {
                String city = cities.get(i).getCity();
                TransportInfo matched = null;
                for (PlanRequest.TransportSegment seg : request.getTransportSegments()) {
                    TransportInfo ti = new TransportInfo();
                    ti.setType(seg.getType());
                    ti.setScheduleNo(seg.getScheduleNo());
                    ti.setFromCity(seg.getFromCity());
                    ti.setToCity(seg.getToCity());
                    ti.setDepartTime(seg.getDepartTime());
                    ti.setArriveTime(seg.getArriveTime());
                    ti.setPrice(seg.getPrice());
                    allSegments.add(ti);
                    if (seg.getToCity() != null && seg.getToCity().equals(city)) {
                        matched = ti;
                    }
                }
                cityTransports.add(matched);
            }
        }
        result.setTransportSegments(allSegments);

        List<PlanResult.CityPlanResult> cityResults = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalAttractions = 0;

        LocalDate cursor = parseDate(request.getStartDate());
        String transportMode = request.getTransportMode();

        for (int i = 0; i < cities.size(); i++) {
            PlanRequest.CityPlan cp = cities.get(i);
            int days = cityDays.get(i);
            List<ScenicSpot> spots = allCitySpots.get(i);
            BigDecimal budget = cityBudgets.get(i);

            Hotel hotel = null;
            if (cp.getHotelId() != null && cp.getHotelId() > 0) {
                hotel = hotelMapper.findById(cp.getHotelId().longValue());
            }

            // 确定该城市的交通：优先使用用户指定的多段交通，否则按旧逻辑自动匹配
            TransportInfo cityTransport = null;
            if (!cityTransports.isEmpty()) {
                cityTransport = cityTransports.get(i);
            } else if (i > 0) {
                cityTransport = matchTransport(cities.get(i - 1).getCity(), cp.getCity(), transportMode);
            }

            PlanResult.CityPlanResult cpr = planCity(
                    cp.getProvince(), cp.getCity(), days, spots, hotel, budget,
                    cityTransport, cursor
            );
            cityResults.add(cpr);
            if (cpr.getCost() != null) {
                totalCost = totalCost.add(cpr.getCost());
            }
            if (cpr.getDayPlans() != null) {
                for (PlanResult.DayPlan dp : cpr.getDayPlans()) {
                    if (dp.getSlots() != null) {
                        for (PlanResult.TimeSlot ts : dp.getSlots()) {
                            if ("spot".equals(ts.getType())) {
                                totalAttractions++;
                            }
                        }
                    }
                }
            }
            cursor = cursor.plusDays(days);
        }

        result.setCities(cityResults);
        result.setTotalCost(totalCost);
        result.setTotalAttractions(totalAttractions);

        if (cities.size() == 1) {
            PlanRequest.CityPlan cp = cities.get(0);
            result.setProvince(cp.getProvince());
            result.setCity(cp.getCity());
            result.setDayPlans(cityResults.get(0).getDayPlans());
        }

        return result;
    }

    private List<PlanRequest.CityPlan> normalizeCities(PlanRequest request) {
        List<PlanRequest.CityPlan> out = new ArrayList<>();
        if (request.getCities() != null && !request.getCities().isEmpty()) {
            for (PlanRequest.CityPlan cp : request.getCities()) {
                if (cp.getProvince() != null && cp.getCity() != null && !cp.getCity().trim().isEmpty()) {
                    out.add(cp);
                }
            }
        } else if (request.getProvince() != null && request.getCity() != null) {
            PlanRequest.CityPlan cp = new PlanRequest.CityPlan();
            cp.setProvince(request.getProvince());
            cp.setCity(request.getCity());
            cp.setDays(request.getDays());
            out.add(cp);
        }
        return out;
    }

    private int computeTotalDays(PlanRequest request, List<PlanRequest.CityPlan> cities) {
        if (request.getStartDate() != null && request.getEndDate() != null) {
            LocalDate start = parseDate(request.getStartDate());
            LocalDate end = parseDate(request.getEndDate());
            return (int) ChronoUnit.DAYS.between(start, end) + 1;
        }
        int total = 0;
        for (PlanRequest.CityPlan cp : cities) {
            total += cp.getDays() > 0 ? cp.getDays() : 1;
        }
        return total > 0 ? total : 3;
    }

    private List<ScenicSpot> querySpots(PlanRequest.CityPlan cp) {
        if (cp.getProvince() == null || cp.getCity() == null) {
            return new ArrayList<>();
        }
        List<ScenicSpot> byProvince = scenicSpotMapper.findByProvince(cp.getProvince());
        if (byProvince == null) {
            return new ArrayList<>();
        }
        return byProvince.stream()
                .filter(s -> cp.getCity().equals(s.getCity()))
                .collect(java.util.stream.Collectors.toList());
    }

    private List<Integer> assignCityDays(List<PlanRequest.CityPlan> cities,
                                         List<List<ScenicSpot>> allCitySpots,
                                         int totalDays) {
        int totalSpots = 0;
        for (List<ScenicSpot> spots : allCitySpots) {
            totalSpots += spots.size();
        }
        if (totalSpots == 0) {
            List<Integer> out = new ArrayList<>();
            for (int i = 0; i < cities.size(); i++) {
                out.add(cities.get(i).getDays() > 0 ? cities.get(i).getDays() : 1);
            }
            return out;
        }
        List<Integer> out = new ArrayList<>();
        int remaining = totalDays;
        for (int i = 0; i < cities.size(); i++) {
            if (i == cities.size() - 1) {
                out.add(remaining);
                continue;
            }
            int spots = allCitySpots.get(i).size();
            int days = (int) Math.ceil((double) spots / 3);
            int max = cities.get(i).getDays() > 0 ? cities.get(i).getDays() : Integer.MAX_VALUE;
            days = Math.min(days, max);
            days = Math.min(days, remaining);
            out.add(days);
            remaining -= days;
        }
        if (remaining < 0) {
            for (int i = 0; i < out.size(); i++) {
                out.set(i, Math.max(1, out.get(i) + remaining));
                if (remaining >= 0) {
                    break;
                }
                remaining = -remaining;
            }
        }
        return out;
    }

    private List<BigDecimal> splitBudget(List<List<ScenicSpot>> allCitySpots, BigDecimal totalBudget) {
        if (totalBudget == null) {
            return null;
        }
        int totalSpots = 0;
        for (List<ScenicSpot> spots : allCitySpots) {
            totalSpots += spots.size();
        }
        if (totalSpots == 0) {
            return null;
        }
        List<BigDecimal> out = new ArrayList<>();
        for (List<ScenicSpot> spots : allCitySpots) {
            BigDecimal share = totalBudget.multiply(BigDecimal.valueOf(spots.size()))
                    .divide(BigDecimal.valueOf(totalSpots), 2, RoundingMode.HALF_UP);
            out.add(share);
        }
        return out;
    }

    private PlanResult.CityPlanResult planCity(String province, String city, int days,
                                               List<ScenicSpot> spots, Hotel hotel,
                                               BigDecimal budget, TransportInfo transport,
                                               LocalDate startDate) {
        PlanResult.CityPlanResult cpr = new PlanResult.CityPlanResult();
        cpr.setProvince(province);
        cpr.setCity(city);
        cpr.setHotel(hotel);
        cpr.setDays(days);
        cpr.setCost(BigDecimal.ZERO);

        if (transport != null) {
            cpr.setTransport(transport);
            if (transport.getPrice() != null) {
                cpr.setCost(cpr.getCost().add(transport.getPrice()));
            }
        }

        List<PlanResult.DayPlan> dayPlans = new ArrayList<>();
        int totalAvailableMinutes = days * DAILY_HOURS * 60;

        if (hotel != null) {
            cpr.setCost(cpr.getCost().add(hotel.getPrice()));
        }

        List<ScenicSpot> sortedSpots = new ArrayList<>(spots);
        sortedSpots.sort(Comparator.comparing(ScenicSpot::getRating).reversed());

        int currentDay = 0;
        int currentMinutesUsed = 0;
        List<PlanResult.TimeSlot> currentSlots = new ArrayList<>();
        LocalDate currentDate = startDate;

        for (ScenicSpot spot : sortedSpots) {
            int visitDuration = (int) Math.ceil(spot.getVisitDuration().doubleValue() * 60);
            int totalSlotDuration = visitDuration + (hotel != null ? travelTimeService.estimateMinutes(
                    hotel.getLatitude(), hotel.getLongitude(),
                    spot.getLatitude(), spot.getLongitude()) : 30);

            if (currentMinutesUsed + totalSlotDuration > totalAvailableMinutes) {
                if (currentDay < days - 1) {
                    dayPlans.add(finishDay(currentDate, currentSlots, (hotel != null ? hotel.getPrice() : BigDecimal.ZERO)));
                    currentDay++;
                    currentMinutesUsed = 0;
                    currentSlots.clear();
                    currentDate = currentDate.plusDays(1);
                }
                if (currentMinutesUsed + totalSlotDuration > totalAvailableMinutes) {
                    break;
                }
            }

            PlanResult.TimeSlot slot = new PlanResult.TimeSlot();
            slot.setType("spot");
            slot.setName(spot.getName());
            slot.setDuration(totalSlotDuration);
            slot.setCost(spot.getPrice() != null ? spot.getPrice() : BigDecimal.ZERO);
            currentSlots.add(slot);
            cpr.setCost(cpr.getCost().add(spot.getPrice() != null ? spot.getPrice() : BigDecimal.ZERO));
            currentMinutesUsed += totalSlotDuration;
        }

        if (!currentSlots.isEmpty()) {
            dayPlans.add(finishDay(currentDate, currentSlots, BigDecimal.ZERO));
        }

        while (dayPlans.size() < days) {
            dayPlans.add(finishDay(currentDate.plusDays(dayPlans.size()), new ArrayList<>(), BigDecimal.ZERO));
        }

        if (budget != null && cpr.getCost().compareTo(budget) > 0) {
            BigDecimal over = cpr.getCost().subtract(budget);
            cutLowRating(sortedSpots, over, cpr);
        }

        cpr.setDayPlans(dayPlans);
        return cpr;
    }

    private PlanResult.DayPlan finishDay(LocalDate date, List<PlanResult.TimeSlot> slots, BigDecimal hotelCost) {
        int currentMin = DAY_START_MIN;
        for (PlanResult.TimeSlot slot : slots) {
            int h = currentMin / 60;
            int m = currentMin % 60;
            slot.setStartTime(String.format("%02d:%02d", h, m));
            currentMin += slot.getDuration();
            if (currentMin >= LUNCH_MIN && currentMin < LUNCH_MIN + 60) {
                PlanResult.TimeSlot lunch = new PlanResult.TimeSlot();
                lunch.setType("meal");
                lunch.setName("午餐");
                lunch.setStartTime(String.format("%02d:%02d", LUNCH_MIN / 60, LUNCH_MIN % 60));
                lunch.setDuration(60);
                lunch.setCost(BigDecimal.valueOf(50));
                slots.add(slots.indexOf(slot), lunch);
                currentMin += 60;
            }
            if (currentMin >= DINNER_MIN && currentMin < DINNER_MIN + 60) {
                PlanResult.TimeSlot dinner = new PlanResult.TimeSlot();
                dinner.setType("meal");
                dinner.setName("晚餐");
                dinner.setStartTime(String.format("%02d:%02d", DINNER_MIN / 60, DINNER_MIN % 60));
                dinner.setDuration(60);
                dinner.setCost(BigDecimal.valueOf(80));
                slots.add(slots.indexOf(slot), dinner);
                currentMin += 60;
            }
        }
        BigDecimal dayCost = hotelCost;
        for (PlanResult.TimeSlot s : slots) {
            if (s.getCost() != null) {
                dayCost = dayCost.add(s.getCost());
            }
        }
        PlanResult.DayPlan dp = new PlanResult.DayPlan();
        dp.setDate(date.format(DATE_FMT));
        dp.setCost(dayCost);
        dp.setSlots(slots);
        return dp;
    }

    private void cutLowRating(List<ScenicSpot> sortedSpots, BigDecimal over, PlanResult.CityPlanResult cpr) {
        for (int i = sortedSpots.size() - 1; i >= 0 && over.compareTo(BigDecimal.ZERO) > 0; i--) {
            ScenicSpot spot = sortedSpots.get(i);
            if (spot.getPrice() != null && spot.getPrice().compareTo(over) <= 0) {
                over = over.subtract(spot.getPrice());
                cpr.setCost(cpr.getCost().subtract(spot.getPrice()));
                for (PlanResult.DayPlan dp : cpr.getDayPlans()) {
                    if (dp.getSlots() != null) {
                        dp.getSlots().removeIf(s -> spot.getName().equals(s.getName()));
                    }
                }
            }
        }
    }

    private TransportInfo matchTransport(String fromCity, String toCity, String mode) {
        if (mode == null || "self-drive".equals(mode)) {
            return null;
        }
        if (fromCity == null || fromCity.trim().isEmpty() || toCity == null || toCity.trim().isEmpty()) {
            return null;
        }
        if ("train".equals(mode)) {
            List<TrainSchedule> list = trainScheduleMapper.findByRoute(fromCity, toCity);
            if (list == null || list.isEmpty()) {
                return null;
            }
            TrainSchedule t = list.get(0);
            TransportInfo info = new TransportInfo();
            info.setType("train");
            info.setScheduleNo(t.getTrainNo());
            info.setTrainType(t.getTrainType());
            info.setFromCity(t.getFromCity());
            info.setToCity(t.getToCity());
            info.setDepartTime(t.getDepartTime());
            info.setArriveTime(t.getArriveTime());
            info.setPrice(t.getPrice());
            return info;
        }
        if ("flight".equals(mode)) {
            List<FlightSchedule> list = flightScheduleMapper.findByRoute(fromCity, toCity);
            if (list == null || list.isEmpty()) {
                return null;
            }
            FlightSchedule f = list.get(0);
            TransportInfo info = new TransportInfo();
            info.setType("flight");
            info.setScheduleNo(f.getFlightNo());
            info.setAirline(f.getAirline());
            info.setFromCity(f.getFromCity());
            info.setToCity(f.getToCity());
            info.setDepartTime(f.getDepartTime());
            info.setArriveTime(f.getArriveTime());
            info.setPrice(f.getPrice());
            return info;
        }
        return null;
    }

    private PlanResult buildEmptyResult(PlanRequest request) {
        PlanResult r = new PlanResult();
        r.setOriginCity(request.getOriginCity());
        r.setTransportMode(request.getTransportMode());
        r.setStartDate(request.getStartDate());
        r.setEndDate(request.getEndDate());
        r.setDays(0);
        r.setTotalCost(BigDecimal.ZERO);
        return r;
    }

    private LocalDate parseDate(String s) {
        if (s == null || s.isEmpty()) {
            return LocalDate.now();
        }
        try {
            return LocalDate.parse(s, DATE_FMT);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
}