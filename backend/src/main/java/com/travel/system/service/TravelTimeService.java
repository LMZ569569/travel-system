package com.travel.system.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 出行时间计算服务
 *
 * 优先调用高德地图「驾车路线规划」API（需要在 application.yml 配置 gaode.key），
 * 未配置 key 或调用失败时，按两点直线距离估算（兜底），保证功能始终可用。
 */
@Service
public class TravelTimeService {

    /** 高德 API key（application.yml 中配置，留空则走估算） */
    @Value("${gaode.key:}")
    private String gaodeKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 计算两点间的出行时间（分钟）
     *
     * @param lat1 起点纬度
     * @param lng1 起点经度
     * @param lat2 终点纬度
     * @param lng2 终点经度
     * @return 出行时间（分钟），坐标缺失时返回默认 30 分钟
     */
    public int estimateMinutes(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        // 有高德 key 时优先调 API
        if (gaodeKey != null && !gaodeKey.trim().isEmpty()) {
            int apiMinutes = gaodeDrivingMinutes(lat1, lng1, lat2, lng2);
            if (apiMinutes > 0) {
                return apiMinutes;
            }
        }
        return fallbackMinutes(lat1, lng1, lat2, lng2);
    }

    /** 高德驾车路线规划：返回耗时（分钟），失败返回 -1 */
    private int gaodeDrivingMinutes(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return -1;
        }
        try {
            String origin = lng1 + "," + lat1;
            String dest = lng2 + "," + lat2;
            String urlStr = "https://restapi.amap.com/v3/direction/driving"
                    + "?origin=" + origin
                    + "&destination=" + dest
                    + "&key=" + gaodeKey;

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("GET");

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            conn.disconnect();

            JsonNode root = objectMapper.readTree(sb.toString());
            if (root.path("status").asText().equals("1")) {
                JsonNode paths = root.path("route").path("paths");
                if (paths.isArray() && paths.size() > 0) {
                    // duration 单位是秒
                    int seconds = paths.get(0).path("cost").path("duration").asInt(0);
                    if (seconds > 0) {
                        return Math.max(5, seconds / 60);
                    }
                }
            }
        } catch (Exception e) {
            // 网络异常/解析失败，走兜底估算
        }
        return -1;
    }

    /** 兜底估算：直线距离按市内 40km/h 折算，限制在 20~120 分钟 */
    private int fallbackMinutes(BigDecimal lat1, BigDecimal lng1, BigDecimal lat2, BigDecimal lng2) {
        if (lat1 == null || lng1 == null || lat2 == null || lng2 == null) {
            return 30;
        }
        double distKm = haversineKm(
                lat1.doubleValue(), lng1.doubleValue(),
                lat2.doubleValue(), lng2.doubleValue());
        int minutes = (int) Math.round(distKm / 40.0 * 60.0);
        return Math.max(20, Math.min(120, minutes));
    }

    /** 球面两点距离（公里），Haversine 公式 */
    private double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
