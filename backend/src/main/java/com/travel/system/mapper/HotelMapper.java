package com.travel.system.mapper;

import com.travel.system.entity.Hotel;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 酒店数据访问层
 */
@Mapper
public interface HotelMapper {

    /** 查询所有酒店 */
    @Select("SELECT * FROM hotel ORDER BY city, id")
    List<Hotel> findAll();

    /** 按城市查询酒店 */
    @Select("SELECT * FROM hotel WHERE city = #{city} ORDER BY id")
    List<Hotel> findByCity(@Param("city") String city);

    /** 按条件筛选酒店：城市/类型/最高价，条件可空 */
    @Select("<script>" +
            "SELECT * FROM hotel WHERE 1=1 " +
            "<if test='city != null and city != \"\"'> AND city = #{city} </if>" +
            "<if test='type != null and type != \"\"'> AND type = #{type} </if>" +
            "<if test='maxPrice != null'> AND price &lt;= #{maxPrice} </if>" +
            " ORDER BY city, price" +
            "</script>")
    List<Hotel> search(@Param("city") String city,
                       @Param("type") String type,
                       @Param("maxPrice") java.math.BigDecimal maxPrice);

    /** 查询所有酒店所在城市（去重） */
    @Select("SELECT DISTINCT city FROM hotel WHERE city IS NOT NULL AND city != '' ORDER BY city")
    List<String> findCities();

    /** 按ID查询 */
    @Select("SELECT * FROM hotel WHERE id = #{id}")
    Hotel findById(@Param("id") Long id);

    /** 新增酒店 */
    @Insert("INSERT INTO hotel (user_id, name, city, type, address, latitude, longitude, price) " +
            "VALUES (#{userId}, #{name}, #{city}, #{type}, #{address}, #{latitude}, #{longitude}, #{price})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Hotel hotel);

    /** 更新酒店 */
    @Update("UPDATE hotel SET name=#{name}, city=#{city}, type=#{type}, address=#{address}, " +
            "latitude=#{latitude}, longitude=#{longitude}, price=#{price} WHERE id=#{id}")
    int update(Hotel hotel);

    /** 删除酒店 */
    @Delete("DELETE FROM hotel WHERE id = #{id}")
    int delete(@Param("id") Long id);
}
