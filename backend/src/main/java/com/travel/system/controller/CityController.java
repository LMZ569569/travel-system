package com.travel.system.controller;

import com.travel.system.common.Result;
import com.travel.system.entity.City;
import com.travel.system.mapper.CityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/city")
public class CityController {

    @Autowired
    private CityMapper cityMapper;

    @GetMapping("/all")
    public Result<List<City>> all() {
        return Result.success(cityMapper.findAll());
    }

    @GetMapping("/provinces")
    public Result<List<String>> provinces() {
        return Result.success(cityMapper.findProvinces());
    }

    @GetMapping("/by-province")
    public Result<List<City>> byProvince(@RequestParam String province) {
        return Result.success(cityMapper.findByProvince(province));
    }
}