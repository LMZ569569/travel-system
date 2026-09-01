# -*- coding: utf-8 -*-
import pymysql

conn = pymysql.connect(
    host='localhost',
    port=3306,
    user='root',
    password='20060204',
    db='travel_plan',
    charset='utf8mb4'
)
cursor = conn.cursor()

# 查询所有景点
cursor.execute("SELECT id, name, city FROM scenic_spot ORDER BY id")
spots = cursor.fetchall()
total = len(spots)

print(f"共 {total} 个景点")
count = 0
for spot in spots:
    sid = spot[0]
    # picsum.photos 提供真实摄影照片
    # 用景点ID作为seed参数，确保同一景点始终显示同一张照片
    image_url = f"https://picsum.photos/seed/spot{sid}/800/600"
    cursor.execute("UPDATE scenic_spot SET image=%s WHERE id=%s", (image_url, sid))
    count += 1
    if count % 500 == 0:
        conn.commit()
        print(f"  已更新 {count}/{total}")

conn.commit()
cursor.close()
conn.close()
print(f"\n更新完成！共更新 {count} 个景点图片URL")
print("所有图片均使用 picsum.photos 提供的真实摄影照片")