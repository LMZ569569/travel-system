-- 为每个城市补充到至少10个景点
USE travel_plan;

-- 临时表：存储需要补充景点的城市
DROP TEMPORARY TABLE IF EXISTS tmp_city_need;
CREATE TEMPORARY TABLE tmp_city_need AS
SELECT s.city, s.province, COUNT(*) as cnt
FROM scenic_spot s
GROUP BY s.city, s.province
HAVING cnt < 10
ORDER BY cnt ASC, s.city;

-- 查看需要补充的城市
SELECT city, province, cnt FROM tmp_city_need LIMIT 20;