-- 检查各城市景点数量
USE travel_plan;

-- 景点最多的城市
SELECT city, COUNT(*) as cnt FROM scenic_spot GROUP BY city ORDER BY cnt DESC LIMIT 10;

-- 景点最少的城市
SELECT city, COUNT(*) as cnt FROM scenic_spot GROUP BY city ORDER BY cnt ASC LIMIT 20;

-- 景点不足6个的城市
SELECT city, COUNT(*) as cnt FROM scenic_spot GROUP BY city HAVING cnt < 6 ORDER BY cnt ASC;

-- 景点总数
SELECT CONCAT('景点总数: ', COUNT(*)) FROM scenic_spot;