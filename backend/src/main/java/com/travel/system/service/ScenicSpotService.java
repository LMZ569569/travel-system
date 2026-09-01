package com.travel.system.service;

import com.travel.system.entity.ScenicSpot;

import java.util.List;

/**
 * 景点业务层接口
 */
public interface ScenicSpotService {

    List<ScenicSpot> findAll();

    ScenicSpot findById(Long id);

    List<ScenicSpot> findByProvince(String province);

    List<ScenicSpot> findByCity(String city);

    List<String> findAllProvinces();

    List<String> findCitiesByProvince(String province);
}
