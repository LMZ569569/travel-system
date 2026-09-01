-- 更新所有景点图片URL为 picsum.photos 真实摄影照片
-- 使用景点ID作为seed参数，确保同一景点始终显示同一张照片
-- 本文件由脚本生成，用于在Navicat中执行

-- 临时开启大事务支持
SET SESSION net_read_timeout = 600;
SET SESSION net_write_timeout = 600;
SET SESSION wait_timeout = 600;

-- 更新所有景点图片
UPDATE scenic_spot SET image = CONCAT('https://picsum.photos/seed/spot', id, '/800/600')
WHERE id > 0;