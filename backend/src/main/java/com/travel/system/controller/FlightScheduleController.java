package com.travel.system.controller;

import com.travel.system.common.Result;
import com.travel.system.entity.FlightSchedule;
import com.travel.system.mapper.FlightScheduleMapper;
import com.travel.system.dto.TransferPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/flight")
public class FlightScheduleController {

    @Autowired
    private FlightScheduleMapper flightScheduleMapper;

    @GetMapping("/list")
    public Result<List<FlightSchedule>> list() {
        return Result.success(flightScheduleMapper.findAll());
    }

    @GetMapping("/route")
    public Result<List<FlightSchedule>> route(@RequestParam String from, @RequestParam String to) {
        List<FlightSchedule> result = flightScheduleMapper.findByRoute(from, to);
        if (result == null || result.isEmpty()) {
            result = flightScheduleMapper.findByRoute(to, from);
        }
        return Result.success(result);
    }

    @GetMapping("/query")
    public Result<List<FlightSchedule>> query(@RequestParam String no) {
        return Result.success(flightScheduleMapper.findByNo(no));
    }

    @GetMapping("/search")
    public Result<List<FlightSchedule>> search(@RequestParam(required = false) String from,
                                               @RequestParam(required = false) String to,
                                               @RequestParam(required = false) String airline) {
        List<FlightSchedule> result = flightScheduleMapper.findAll();
        if (from != null && !from.isEmpty()) {
            result = result.stream().filter(f -> f.getFromCity().contains(from)).collect(Collectors.toList());
        }
        if (to != null && !to.isEmpty()) {
            result = result.stream().filter(f -> f.getToCity().contains(to)).collect(Collectors.toList());
        }
        if (airline != null && !airline.isEmpty()) {
            result = result.stream().filter(f -> airline.equals(f.getAirline())).collect(Collectors.toList());
        }
        return Result.success(result);
    }

    @GetMapping("/from-cities")
    public Result<List<String>> fromCities() {
        return Result.success(flightScheduleMapper.findFromCities());
    }

    @GetMapping("/to-cities")
    public Result<List<String>> toCities() {
        return Result.success(flightScheduleMapper.findToCities());
    }

    @GetMapping("/transfer")
    public Result<List<TransferPlan>> transfer(@RequestParam String from, @RequestParam String to) {
        Set<String> midCities = new HashSet<>();

        List<String> reachable = flightScheduleMapper.findToCitiesByFrom(from);
        List<String> reverseReachable = flightScheduleMapper.findToCitiesByFrom(to);
        List<String> canReach = flightScheduleMapper.findFromCitiesByTo(to);
        List<String> reverseCanReach = flightScheduleMapper.findFromCitiesByTo(from);

        midCities.addAll(reachable); midCities.retainAll(canReach);
        Set<String> alt = new HashSet<>(reverseReachable);
        alt.retainAll(reverseCanReach);
        midCities.addAll(alt);
        midCities.remove(from);
        midCities.remove(to);

        List<TransferPlan> plans = new ArrayList<>();
        for (String mid : midCities) {
            List<FlightSchedule> firstLegs = flightScheduleMapper.findByRoute(from, mid);
            if (firstLegs == null || firstLegs.isEmpty()) {
                firstLegs = flightScheduleMapper.findByRoute(mid, from);
            }
            List<FlightSchedule> secondLegs = flightScheduleMapper.findByRoute(mid, to);
            if (secondLegs == null || secondLegs.isEmpty()) {
                secondLegs = flightScheduleMapper.findByRoute(to, mid);
            }
            if (!firstLegs.isEmpty() && !secondLegs.isEmpty()) {
                FlightSchedule leg1 = firstLegs.get(0);
                FlightSchedule leg2 = secondLegs.get(0);
                plans.add(new TransferPlan("flight", mid, leg1, leg2));
            }
        }

        plans.sort(Comparator.comparingDouble(TransferPlan::getTotalPrice));
        return Result.success(plans);
    }

    @PostMapping
    public Result<FlightSchedule> add(@RequestBody FlightSchedule schedule) {
        flightScheduleMapper.insert(schedule);
        return Result.success(schedule);
    }

    @PutMapping("/{id}")
    public Result<FlightSchedule> update(@PathVariable Long id, @RequestBody FlightSchedule schedule) {
        schedule.setId(id);
        flightScheduleMapper.update(schedule);
        return Result.success(schedule);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        flightScheduleMapper.delete(id);
        return Result.success(null);
    }
}