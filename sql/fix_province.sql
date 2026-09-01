-- 修复 scenic_spot 表的 province 字段
USE travel_plan;

-- 1. 查看 province=city 的非直辖市记录
SELECT city, COUNT(*) as cnt FROM scenic_spot 
WHERE province = city AND city NOT IN ('北京', '上海', '天津', '重庆')
GROUP BY city ORDER BY cnt DESC
LIMIT 20;

-- 2. 更新用 hotel 的正确 province 数据
UPDATE scenic_spot s
JOIN (SELECT DISTINCT city, province FROM hotel WHERE province != city) h ON s.city = h.city
SET s.province = h.province
WHERE s.province = s.city AND s.city NOT IN ('北京', '上海', '天津', '重庆');

-- 3. 验证修复结果
SELECT province, COUNT(DISTINCT city) as city_count, COUNT(*) as spot_count 
FROM scenic_spot GROUP BY province ORDER BY province;