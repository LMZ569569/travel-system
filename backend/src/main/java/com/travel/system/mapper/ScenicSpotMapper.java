package com.travel.system.mapper;

import com.travel.system.entity.ScenicSpot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScenicSpotMapper {

    @Select("SELECT * FROM scenic_spot")
    List<ScenicSpot> findAll();

    @Select("SELECT * FROM scenic_spot WHERE id = #{id}")
    ScenicSpot findById(@Param("id") Long id);

    @Select("SELECT * FROM scenic_spot WHERE province = #{province}")
    List<ScenicSpot> findByProvince(@Param("province") String province);

    @Select("SELECT * FROM scenic_spot WHERE city = #{city}")
    List<ScenicSpot> findByCity(@Param("city") String city);
}