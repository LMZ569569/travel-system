# -*- coding: utf-8 -*-
# 生成SQL更新脚本，用picsum.photos真实摄影照片替换景点图片

with open('update_picsum.sql', 'w', encoding='utf-8') as f:
    f.write('-- 更新景点图片为 picsum.photos 真实摄影照片\n')
    f.write('-- 在Navicat中运行此文件\n\n')
    # 直接用一条UPDATE搞定
    f.write("UPDATE scenic_spot SET image = CONCAT('https://picsum.photos/seed/spot', id, '/800/600');\n")

print("SQL脚本已生成：update_picsum.sql")
print("请在Navicat中打开 travel 数据库，运行此SQL文件")