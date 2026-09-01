package com.travel.system.controller;

import com.travel.system.common.Result;
import com.travel.system.entity.TrainSchedule;
import com.travel.system.mapper.TrainScheduleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/train")
public class TrainScheduleController {

    @Autowired
    private TrainScheduleMapper trainScheduleMapper;

    @GetMapping("/list")
    public Result<List<TrainSchedule>> list() {
        return Result.success(trainScheduleMapper.findAll());
    }

    @GetMapping("/route")
    public Result<List<TrainSchedule>> route(@RequestParam String from, @RequestParam String to) {
        return Result.success(trainScheduleMapper.findByRoute(from, to));
    }

    @GetMapping("/query")
    public Result<List<TrainSchedule>> query(@RequestParam String no) {
        return Result.success(trainScheduleMapper.findByNo(no));
    }

    @GetMapping("/search")
    public Result<List<TrainSchedule>> search(@RequestParam(required = false) String from,
                                              @RequestParam(required = false) String to,
                                              @RequestParam(required = false) String trainType) {
        List<TrainSchedule> result = trainScheduleMapper.findAll();
        if (from != null && !from.isEmpty()) {
            result = result.stream().filter(t -> t.getFromCity().contains(from)).collect(Collectors.toList());
        }
        if (to != null && !to.isEmpty()) {
            result = result.stream().filter(t -> t.getToCity().contains(to)).collect(Collectors.toList());
        }
        if (trainType != null && !trainType.isEmpty()) {
            result = result.stream().filter(t -> trainType.equals(t.getTrainType())).collect(Collectors.toList());
        }
        return Result.success(result);
    }

    @GetMapping("/from-cities")
    public Result<List<String>> fromCities() {
        return Result.success(trainScheduleMapper.findFromCities());
    }

    @GetMapping("/to-cities")
    public Result<List<String>> toCities() {
        return Result.success(trainScheduleMapper.findToCities());
    }

    @PostMapping
    public Result<TrainSchedule> add(@RequestBody TrainSchedule schedule) {
        trainScheduleMapper.insert(schedule);
        return Result.success(schedule);
    }

    @PutMapping("/{id}")
    public Result<TrainSchedule> update(@PathVariable Long id, @RequestBody TrainSchedule schedule) {
        schedule.setId(id);
        trainScheduleMapper.update(schedule);
        return Result.success(schedule);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        trainScheduleMapper.delete(id);
        return Result.success(null);
    }
}