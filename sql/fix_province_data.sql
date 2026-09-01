-- 修复 scenic_spot 表的 province 字段
-- 用 hotel 表的 province 作为参考，更新 scenic_spot 中 province 错误的数据

USE travel_plan;

-- 先看看哪些 scenic_spot 记录的 province 实际上是城市名而非省份名
-- 直辖市（北京、上海、天津、重庆）的 province=city 是正常的
-- 其他城市如果 province=city，说明 province 字段存错了

-- 1. 找出 province 和 city 相同但 city 不是直辖市的城市
SELECT city, COUNT(*) as cnt FROM scenic_spot 
WHERE province = city AND city NOT IN ('北京', '上海', '天津', '重庆')
GROUP BY city ORDER BY cnt DESC;

-- 2. 用 hotel 表的 province 字段来更新
UPDATE scenic_spot s
JOIN (
    SELECT DISTINCT h.city, h.province 
    FROM hotel h 
    WHERE h.province != h.city
) h ON s.city = h.city
SET s.province = h.province
WHERE s.province = s.city AND s.city NOT IN ('北京', '上海', '天津', '重庆');

-- 3. 验证修复结果
SELECT province, COUNT(DISTINCT city) as city_count, COUNT(*) as spot_count 
FROM scenic_spot 
GROUP BY province 
ORDER BY province;

-- 4. 验证还有没有 province=city 的非直辖市
SELECT city, province FROM scenic_spot 
WHERE province = city AND city NOT IN ('北京', '上海', '天津', '重庆')
LIMIT 10;