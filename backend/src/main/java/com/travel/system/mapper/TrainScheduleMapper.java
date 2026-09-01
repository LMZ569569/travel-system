package com.travel.system.mapper;

import com.travel.system.entity.TrainSchedule;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TrainScheduleMapper {

    @Select("SELECT * FROM train_schedule ORDER BY from_city, depart_time")
    List<TrainSchedule> findAll();

    @Select("SELECT * FROM train_schedule WHERE from_city = #{fromCity} AND to_city = #{toCity} ORDER BY depart_time")
    List<TrainSchedule> findByRoute(@Param("fromCity") String fromCity, @Param("toCity") String toCity);

    @Select("SELECT * FROM train_schedule WHERE train_no = #{no} ORDER BY depart_time")
    List<TrainSchedule> findByNo(@Param("no") String no);

    @Select("SELECT DISTINCT from_city FROM train_schedule ORDER BY from_city")
    List<String> findFromCities();

    @Select("SELECT DISTINCT to_city FROM train_schedule ORDER BY to_city")
    List<String> findToCities();

    @Insert("INSERT INTO train_schedule (user_id, train_no, train_type, from_city, to_city, depart_time, arrive_time, price) " +
            "VALUES (#{userId}, #{trainNo}, #{trainType}, #{fromCity}, #{toCity}, #{departTime}, #{arriveTime}, #{price})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TrainSchedule schedule);

    @Update("UPDATE train_schedule SET train_no=#{trainNo}, train_type=#{trainType}, from_city=#{fromCity}, " +
            "to_city=#{toCity}, depart_time=#{departTime}, arrive_time=#{arriveTime}, price=#{price} WHERE id=#{id}")
    int update(TrainSchedule schedule);

    @Delete("DELETE FROM train_schedule WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT DISTINCT to_city FROM train_schedule WHERE from_city = #{fromCity} ORDER BY to_city")
    List<String> findToCitiesByFrom(@Param("fromCity") String fromCity);

    @Select("SELECT DISTINCT from_city FROM train_schedule WHERE to_city = #{toCity} ORDER BY from_city")
    List<String> findFromCitiesByTo(@Param("toCity") String toCity);
}