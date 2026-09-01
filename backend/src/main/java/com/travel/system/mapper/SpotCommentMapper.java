package com.travel.system.mapper;

import com.travel.system.entity.SpotComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpotCommentMapper {

    @Select("SELECT * FROM scenic_spot_comment WHERE spot_id = #{spotId} ORDER BY created_at DESC")
    List<SpotComment> findBySpotId(@Param("spotId") Long spotId);

    @Insert("INSERT INTO scenic_spot_comment(spot_id, user_id, username, content, rating) " +
            "VALUES(#{spotId}, #{userId}, #{username}, #{content}, #{rating})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SpotComment comment);

    @Delete("DELETE FROM scenic_spot_comment WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT COALESCE(AVG(rating), 0) FROM scenic_spot_comment WHERE spot_id = #{spotId}")
    Double avgRatingBySpotId(@Param("spotId") Long spotId);
}