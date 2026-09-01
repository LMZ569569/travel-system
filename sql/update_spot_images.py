# -*- coding: utf-8 -*-
"""为每个景点生成图片URL（使用文本转图片API）"""

import subprocess, urllib.parse

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

print(f"共 {len(spots)} 个景点，正在生成图片URL...")

API_BASE = "https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image"

updated = 0
for s in spots:
    # 构建prompt：景点名称 + 城市 + 真实摄影风格
    prompt = f"A beautiful scenic photo of {s['name']} in {s['city']}, China, realistic photography, natural lighting, high quality"
    prompt_encoded = urllib.parse.quote(prompt)
    image_url = f"{API_BASE}?prompt={prompt_encoded}&image_size=landscape_16_9"
    
    name_safe = s['name'].replace("'", "''")
    sql = f"UPDATE scenic_spot SET image='{image_url}' WHERE id={s['id']}"
    run_sql(sql)
    updated += 1
    
    if updated % 500 == 0:
        print(f"  已更新 {updated}/{len(spots)}")

print(f"完成！共更新 {updated} 个景点图片")