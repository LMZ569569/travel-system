# -*- coding: utf-8 -*-
"""使用 picsum.photos 替换景点图片URL（真实可用的图片链接）"""

import subprocess

MYSQL = r'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
PASS = '20060204'
DB = 'travel_plan'

def run_sql(sql):
    cmd = [MYSQL, '-u', 'root', f'-p{PASS}', '-D', DB, '-N', '-B', '--default-character-set=utf8']
    r = subprocess.run(cmd, input=sql.encode('utf-8'), capture_output=True, timeout=60)
    return r.stdout.decode('utf-8').strip()

# 获取所有景点
rows = run_sql("SELECT id, name, city FROM scenic_spot ORDER BY id")
lines = rows.strip().split('\n')

spots = []
for line in lines:
    parts = line.strip().split('\t')
    if len(parts) >= 3:
        spots.append({'id': parts[0].strip(), 'name': parts[1].strip(), 'city': parts[2].strip()})

print(f"共 {len(spots)} 个景点，正在更新图片URL...")

# 使用 picsum.photos 的 seed 参数，确保每个景点有唯一且固定的图片
# 格式: https://picsum.photos/seed/{id}/800/600
# 使用景点ID作为seed，保证同一景点始终显示同一张图片

updated = 0
for s in spots:
    image_url = f"https://picsum.photos/seed/spot{s['id']}/800/600"
    sql = f"UPDATE scenic_spot SET image='{image_url}' WHERE id={s['id']}"
    run_sql(sql)
    updated += 1

    if updated % 500 == 0:
        print(f"  已更新 {updated}/{len(spots)}")

print(f"完成！共更新 {updated} 个景点图片URL")
print("现在使用 picsum.photos 图片服务，浏览器可直接加载显示")