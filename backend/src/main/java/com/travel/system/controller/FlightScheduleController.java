package com.travel.system.controller;

import com.travel.system.common.Result;
import com.travel.system.entity.FlightSchedule;
import com.travel.system.mapper.FlightScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
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
        return Result.success(flightScheduleMapper.findByRoute(from, to));
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