package com.travel.system.mapper;

import com.travel.system.entity.FlightSchedule;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface FlightScheduleMapper {

    @Select("SELECT * FROM flight_schedule ORDER BY from_city, depart_time")
    List<FlightSchedule> findAll();

    @Select("SELECT * FROM flight_schedule WHERE from_city = #{fromCity} AND to_city = #{toCity} ORDER BY depart_time")
    List<FlightSchedule> findByRoute(@Param("fromCity") String fromCity, @Param("toCity") String toCity);

    @Select("SELECT * FROM flight_schedule WHERE flight_no = #{no} ORDER BY depart_time")
    List<FlightSchedule> findByNo(@Param("no") String no);

    @Select("SELECT DISTINCT from_city FROM flight_schedule ORDER BY from_city")
    List<String> findFromCities();

    @Select("SELECT DISTINCT to_city FROM flight_schedule ORDER BY to_city")
    List<String> findToCities();

    @Insert("INSERT INTO flight_schedule (user_id, flight_no, airline, from_city, to_city, depart_time, arrive_time, price) " +
            "VALUES (#{userId}, #{flightNo}, #{airline}, #{fromCity}, #{toCity}, #{departTime}, #{arriveTime}, #{price})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FlightSchedule schedule);

    @Update("UPDATE flight_schedule SET flight_no=#{flightNo}, airline=#{airline}, from_city=#{fromCity}, " +
            "to_city=#{toCity}, depart_time=#{departTime}, arrive_time=#{arriveTime}, price=#{price} WHERE id=#{id}")
    int update(FlightSchedule schedule);

    @Delete("DELETE FROM flight_schedule WHERE id = #{id}")
    int delete(@Param("id") Long id);

    @Select("SELECT DISTINCT to_city FROM flight_schedule WHERE from_city = #{fromCity} ORDER BY to_city")
    List<String> findToCitiesByFrom(@Param("fromCity") String fromCity);

    @Select("SELECT DISTINCT from_city FROM flight_schedule WHERE to_city = #{toCity} ORDER BY from_city")
    List<String> findFromCitiesByTo(@Param("toCity") String toCity);
}