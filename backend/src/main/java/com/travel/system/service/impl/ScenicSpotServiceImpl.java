package com.travel.system.service.impl;

import com.travel.system.entity.ScenicSpot;
import com.travel.system.mapper.ScenicSpotMapper;
import com.travel.system.service.ScenicSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 景点业务层实现
 */
@Service
public class ScenicSpotServiceImpl implements ScenicSpotService {

    @Autowired
    private ScenicSpotMapper scenicSpotMapper;

    @Override
    public List<ScenicSpot> findAll() {
        return scenicSpotMapper.findAll();
    }

    @Override
    public ScenicSpot findById(Long id) {
        return scenicSpotMapper.findById(id);
    }

    @Override
    public List<ScenicSpot> findByProvince(String province) {
        return scenicSpotMapper.findByProvince(province);
    }

    @Override
    public List<ScenicSpot> findByCity(String city) {
        return scenicSpotMapper.findByCity(city);
    }
}
