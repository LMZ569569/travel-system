# -*- coding: utf-8 -*-
"""为每个城市补充到至少10个景点 v3"""

import subprocess, random

MYSQL = r'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
PASS = '20060204'
DB = 'travel_plan'

def run_sql(sql):
    cmd = [MYSQL, '-u', 'root', f'-p{PASS}', '-D', DB, '-N', '--default-character-set=utf8']
    r = subprocess.run(cmd, input=sql.encode('utf-8'), capture_output=True, timeout=60)
    out = r.stdout.decode('utf-8').strip()
    err = r.stderr.decode('utf-8').strip()
    if err and 'Warning' not in err:
        print(f"SQL Error: {err}")
    return out

# 获取每个城市的景点数 - 用子查询（避免only_full_group_by）
rows = run_sql("SELECT city, province, cnt FROM (SELECT city, ANY_VALUE(province) as province, COUNT(*) as cnt FROM scenic_spot GROUP BY city) t ORDER BY cnt ASC, city")
lines = [l.strip() for l in rows.split('\n') if l.strip()]

cities = []
for line in lines:
    parts = line.split('\t')
    if len(parts) >= 3:
        city = parts[0].strip()
        prov = parts[1].strip()
        cnt = parts[2].strip()
        if cnt.isdigit():
            cities.append({'city': city, 'province': prov, 'count': int(cnt)})

print(f"共 {len(cities)} 个城市")
need_fix = [c for c in cities if c['count'] < 10]
print(f"不足10个景点的城市: {len(need_fix)}")

# 已存在的景点名称
existing_raw = run_sql("SELECT DISTINCT name FROM scenic_spot")
existing_names = set(n.strip() for n in existing_raw.split('\n') if n.strip())

spot_types = ['风景区', '公园', '古镇', '遗址', '纪念馆', '生态园', '度假村', '博物馆', '文化园', '植物园', '寺庙', '山', '湖', '岛', '温泉', '峡谷', '瀑布', '湿地公园', '老街', '广场']

random.seed(789)
inserted = 0

for c in need_fix:
    city = c['city']
    prov = c['province']
    need = 10 - c['count']
    if need <= 0:
        continue
    
    values = []
    for i in range(need):
        base = random.choice(spot_types)
        name = f"{city}{base}"
        if name in existing_names:
            name = f"{city}{city}第{i+1}景{base}"
        existing_names.add(name)
        
        open_time = random.choice(['08:00-17:30', '08:30-17:00', '09:00-17:00', '08:00-18:00', '全天开放', '07:00-19:00'])
        level = random.choice(['4A', '4A', '3A', '4A', '5A'])
        price = 0.00 if random.random() < 0.3 else round(random.uniform(10, 150), 2)
        rating = round(3.5 + random.random() * 1.5, 1)
        visit_dur = round(2.0 + random.random() * 4.0, 1)
        lat = round(22.0 + random.random() * 5.0, 6)
        lng = round(113.0 + random.random() * 3.0, 6)
        desc = f"{city}知名旅游景区，{name}，风景优美，设施完善"
        name_safe = name.replace("'", "''")
        desc_safe = desc.replace("'", "''")
        values.append(f"('{name_safe}','{desc_safe}','{prov}','{city}','{level}','{city}市区',{lat},{lng},{price:.2f},'{open_time}',{rating:.1f},{visit_dur:.1f})")
    
    if values:
        sql = "INSERT INTO scenic_spot (name, description, province, city, level, address, latitude, longitude, price, open_time, rating, visit_duration) VALUES\n"
        sql += ",\n".join(values) + ";"
        run_sql(sql)
        inserted += len(values)
        print(f"  {city}({prov}): +{len(values)}")

print(f"\n总计新增: {inserted} 个景点")

# 验证
total = run_sql("SELECT COUNT(*) FROM scenic_spot")
low = run_sql("SELECT city, cnt FROM (SELECT city, COUNT(*) as cnt FROM scenic_spot GROUP BY city) t WHERE cnt < 10 ORDER BY cnt")
low_lines = [l.strip() for l in low.split('\n') if l.strip()]
if low_lines:
    print(f"仍不足10个景点的城市: {len(low_lines)}")
else:
    print(f"景点总数: {total}")
    print("所有城市均有至少10个景点 ✓")