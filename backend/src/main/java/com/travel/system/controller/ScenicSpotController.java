package com.travel.system.controller;

import com.travel.system.common.Result;
import com.travel.system.entity.ScenicSpot;
import com.travel.system.service.ScenicSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 景点接口
 */
@RestController
@RequestMapping("/api/scenic-spot")
public class ScenicSpotController {

    @Autowired
    private ScenicSpotService scenicSpotService;

    /** 查询所有景点 */
    @GetMapping("/list")
    public Result<List<ScenicSpot>> list() {
        return Result.success(scenicSpotService.findAll());
    }

    /** 查询景点详情 */
    @GetMapping("/{id}")
    public Result<ScenicSpot> detail(@PathVariable Long id) {
        return Result.success(scenicSpotService.findById(id));
    }

    /** 按省份查询景点 */
    @GetMapping("/province/{province}")
    public Result<List<ScenicSpot>> byProvince(@PathVariable String province) {
        return Result.success(scenicSpotService.findByProvince(province));
    }

    /** 按城市查询景点 */
    @GetMapping("/city/{city}")
    public Result<List<ScenicSpot>> byCity(@PathVariable String city) {
        return Result.success(scenicSpotService.findByCity(city));
    }
}
