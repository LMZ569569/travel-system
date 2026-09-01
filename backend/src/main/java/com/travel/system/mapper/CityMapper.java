package com.travel.system.mapper;

import com.travel.system.entity.City;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CityMapper {

    @Select("SELECT * FROM city ORDER BY province, city_name")
    List<City> findAll();

    @Select("SELECT * FROM city WHERE province = #{province} ORDER BY city_name")
    List<City> findByProvince(@Param("province") String province);

    @Select("SELECT DISTINCT province FROM city ORDER BY province")
    List<String> findProvinces();
}