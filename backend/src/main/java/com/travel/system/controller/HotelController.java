package com.travel.system.controller;

import com.travel.system.common.Result;
import com.travel.system.entity.Hotel;
import com.travel.system.mapper.HotelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 酒店接口
 */
@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    @Autowired
    private HotelMapper hotelMapper;

    /** 查询所有酒店 */
    @GetMapping("/list")
    public Result<List<Hotel>> list() {
        return Result.success(hotelMapper.findAll());
    }

    /** 按城市查询酒店：GET /api/hotel/city?city=北京 */
    @GetMapping("/city")
    public Result<List<Hotel>> byCity(@RequestParam String city) {
        return Result.success(hotelMapper.findByCity(city));
    }

    /** 按条件筛选酒店：GET /api/hotel/search?city=杭州&type=舒适型&maxPrice=500 */
    @GetMapping("/search")
    public Result<List<Hotel>> search(@RequestParam(required = false) String city,
                                      @RequestParam(required = false) String type,
                                      @RequestParam(required = false) java.math.BigDecimal maxPrice) {
        return Result.success(hotelMapper.search(city, type, maxPrice));
    }

    /** 查询所有酒店所在城市：GET /api/hotel/cities */
    @GetMapping("/cities")
    public Result<List<String>> cities() {
        return Result.success(hotelMapper.findCities());
    }

    /** 新增酒店（用户自行添加） */
    @PostMapping
    public Result<Hotel> add(@RequestBody Hotel hotel) {
        hotelMapper.insert(hotel);
        return Result.success(hotel);
    }

    /** 更新酒店 */
    @PutMapping("/{id}")
    public Result<Hotel> update(@PathVariable Long id, @RequestBody Hotel hotel) {
        hotel.setId(id);
        hotelMapper.update(hotel);
        return Result.success(hotel);
    }

    /** 删除酒店 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        hotelMapper.delete(id);
        return Result.success(null);
    }
}
