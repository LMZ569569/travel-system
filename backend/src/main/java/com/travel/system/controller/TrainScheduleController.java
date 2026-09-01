package com.travel.system.controller;

import com.travel.system.common.Result;
import com.travel.system.entity.TrainSchedule;
import com.travel.system.mapper.TrainScheduleMapper;
import com.travel.system.dto.TransferPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
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

    @GetMapping("/transfer")
    public Result<List<TransferPlan>> transfer(@RequestParam String from, @RequestParam String to) {
        // 1. 找所有中转城市
        List<String> reachable = trainScheduleMapper.findToCitiesByFrom(from);
        List<String> canReach = trainScheduleMapper.findFromCitiesByTo(to);
        Set<String> midCities = new HashSet<>(reachable);
        midCities.retainAll(canReach);
        midCities.remove(from);
        midCities.remove(to);

        // 2. 找每个中转城市的最优方案
        List<TransferPlan> plans = new ArrayList<>();
        for (String mid : midCities) {
            List<TrainSchedule> firstLegs = trainScheduleMapper.findByRoute(from, mid);
            List<TrainSchedule> secondLegs = trainScheduleMapper.findByRoute(mid, to);
            if (!firstLegs.isEmpty() && !secondLegs.isEmpty()) {
                TrainSchedule leg1 = firstLegs.get(0);
                TrainSchedule leg2 = secondLegs.get(0);
                plans.add(new TransferPlan("train", mid, leg1, leg2));
            }
        }

        // 按总价排序
        plans.sort(Comparator.comparingDouble(TransferPlan::getTotalPrice));
        return Result.success(plans);
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