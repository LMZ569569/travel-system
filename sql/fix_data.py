# -*- coding: utf-8 -*-
"""生成反向交通数据 + 补充景点数据"""

import subprocess
import random

MYSQL = r'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
PASS = '20060204'
DB = 'travel_plan'

def run_sql(sql):
    cmd = [MYSQL, '-u', 'root', f'-p{PASS}', '-D', DB, '-N']
    r = subprocess.run(cmd, input=sql.encode('utf-8'), capture_output=True, timeout=60)
    return r.stdout.decode('utf-8').strip()

# ==================== 1. 反向交通 ====================
print("=== 处理反向交通 ===")
rows = run_sql("SELECT DISTINCT from_city, to_city, train_type FROM train_schedule ORDER BY from_city, to_city")
pairs = []
for line in rows.split('\n'):
    parts = line.strip().split('\t')
    if len(parts) >= 3:
        pairs.append({'from': parts[0], 'to': parts[1], 'type': parts[2]})

existing = set()
for p in pairs:
    existing.add(f"{p['from']}|{p['to']}")

missing = []
for p in pairs:
    rev = f"{p['to']}|{p['from']}"
    if rev not in existing:
        missing.append({'from': p['to'], 'to': p['from'], 'type': p['type']})
        existing.add(rev)  # 避免重复添加

print(f"总路线对: {len(pairs)}, 缺失反向: {len(missing)}")

# 为每个反向路线生成车次
inserts = []
for src, dst, ttype in [(m['from'], m['to'], m['type']) for m in missing]:
    # 找原路线取价格和时间的参考
    ref = run_sql(f"SELECT depart_time, arrive_time, price FROM train_schedule WHERE from_city='{src.replace(chr(39),'')}' AND to_city='{dst.replace(chr(39),'')}' AND train_type='{ttype}' LIMIT 1")
    # 如果找不到原路线（说明原路线也只有单向），跳过
    if not ref:
        # 用随机生成
        h = random.randint(6, 22)
        m = random.randint(0, 59)
        dep = f"{h:02d}:{m:02d}"
        travel = random.randint(30, 300)
        h2 = (h + travel // 60) % 24
        m2 = m + travel % 60
        arr = f"{h2:02d}:{m2:02d}"
        price = random.randint(20, 500)
        inserts.append(f"('{src}','{dst}','{ttype}','{dep}','{arr}',{price})")
        continue

    parts = ref.split('\t')
    dep = parts[0].strip()
    arr = parts[1].strip()
    price = parts[2].strip()
    # 交换出发和到达时间（加一点偏移模拟实际运行）
    inserts.append(f"('{dst}','{src}','{ttype}','{arr}','{dep}',{price})")

# 分批插入
batch_size = 100
total = 0
for i in range(0, len(inserts), batch_size):
    batch = inserts[i:i+batch_size]
    sql = "INSERT INTO train_schedule (user_id, train_no, train_type, from_city, to_city, depart_time, arrive_time, price) VALUES\n"
    rows_sql = []
    for j, ins in enumerate(batch):
        parts = ins.strip("()").split(",")
        f = parts[0].strip("'")
        t = parts[1].strip("'")
        tt = parts[2].strip("'")
        dep = parts[3].strip("'")
        arr = parts[4].strip("'")
        price = parts[5]
        # 生成车次号
        if tt == 'G':
            num = f"G{6000+total+j}"
        elif tt == 'D':
            num = f"D{7000+total+j}"
        elif tt == 'C':
            num = f"C{8000+total+j}"
        elif tt == 'K':
            num = f"K{9000+total+j}"
        else:
            num = f"T{5000+total+j}"
        rows_sql.append(f"(NULL,'{num}','{tt}','{f}','{t}','{dep}','{arr}',{price})")
    sql += ",\n".join(rows_sql) + ";"
    run_sql(sql)
    total += len(batch)
    print(f"  已插入 {total}/{len(inserts)} 条反向路线")

print(f"反向路线完成: 共 {total} 条")

# ==================== 2. 补充景点 ====================
print("\n=== 补充景点数据 ===")
# 获取每个城市的景点数
spots_raw = run_sql("SELECT city, COUNT(*) as cnt FROM scenic_spot GROUP BY city ORDER BY cnt")
spot_lines = spots_raw.split('\n')
city_spots = {}
for line in spot_lines:
    parts = line.strip().split('\t')
    if len(parts) >= 2:
        city_spots[parts[0]] = int(parts[1])

# 获取所有城市
cities_raw = run_sql("SELECT DISTINCT city FROM scenic_spot ORDER BY city")
all_cities = [c.strip() for c in cities_raw.split('\n') if c.strip()]

# 不足6个的
need_cities = [(c, city_spots.get(c, 0)) for c in all_cities if city_spots.get(c, 0) < 6]
need_cities.sort(key=lambda x: x[1])
print(f"需要补充景点的城市: {len(need_cities)} 个")

# 真实景点模板（按城市类型）
spot_templates = {
    '公园': ['{city}中央公园', '{city}滨江公园', '{city}森林公园', '{city}城市公园', '{city}湿地公园', '{city}生态公园', '{city}湖滨公园', '{city}南山公园'],
    '文化': ['{city}博物馆', '{city}文化广场', '{city}美术馆', '{city}科技馆', '{city}规划展览馆', '{city}民俗博物馆', '{city}图书馆', '{city}剧院'],
    '历史': ['{city}古城墙遗址', '{city}老街', '{city}古码头', '{city}古塔', '{city}古桥', '{city}名人故居', '{city}书院', '{city}牌坊群'],
    '休闲': ['{city}人民广场', '{city}体育中心', '{city}游乐场', '{city}水上世界', '{city}动物园', '{city}植物园', '{city}商业步行街', '{city}美食街'],
    '宗教': ['{city}大佛寺', '{city}观音庙', '{city}文庙', '{city}城隍庙', '{city}教堂', '{city}清真寺', '{city}道观', '{city}古寺'],
}

all_template_keys = [k for g in spot_templates.values() for k in g]

# 已有景点名
existing_names_raw = run_sql("SELECT DISTINCT name FROM scenic_spot")
existing_names = set(n.strip() for n in existing_names_raw.split('\n') if n.strip())

# 使用计数确保唯一性
random.seed(42)
insert_values = []
name_counter = {}
for city, cnt in need_cities:
    need = 6 - cnt
    if need <= 0:
        continue
    city_used = set()
    for i in range(need):
        # 生成唯一名称
        base_name = None
        for attempt in range(50):
            template = random.choice(all_template_keys)
            name = template.format(city=city)
            if name not in existing_names and name not in city_used:
                base_name = name
                break
        if not base_name:
            name_counter[city] = name_counter.get(city, 0) + 1
            base_name = f"{city}第{name_counter[city]}景"
        
        city_used.add(base_name)
        existing_names.add(base_name)
        
        # 获取省份
        prov = run_sql(f"SELECT province FROM scenic_spot WHERE city='{city}' LIMIT 1")
        if not prov:
            prov = city
        prov = prov.strip()
        
        level = random.choice(['4A', '4A', '3A', '3A', '4A', '5A'])
        lat = round(28.0 + random.random() * 18.0, 6)
        lng = round(100.0 + random.random() * 25.0, 6)
        price = 0.00 if random.random() < 0.3 else round(random.uniform(10, 150), 2)
        open_time = random.choice(['08:00-17:30', '08:30-17:00', '09:00-17:00', '08:00-18:00', '全天开放', '09:00-21:00'])
        rating = round(3.5 + random.random() * 1.5, 1)
        visit_dur = round(2.0 + random.random() * 4.0, 1)
        desc = f"{city}著名{level}级旅游景区，{base_name}，风景优美，设施完善"
        addr = f"位于{city}市区"
        
        insert_values.append(f"('{base_name}','{desc}','{prov}','{city}','{level}','{addr}',{lat},{lng},{price:.2f},'{open_time}',{rating:.1f},{visit_dur:.1f})")

print(f"新景点数据: {len(insert_values)} 条")

# 分批插入景点
batch_size = 100
total_spot = 0
for i in range(0, len(insert_values), batch_size):
    batch = insert_values[i:i+batch_size]
    sql = "INSERT INTO scenic_spot (name, description, province, city, level, address, latitude, longitude, price, open_time, rating, visit_duration) VALUES\n"
    sql += ",\n".join(batch) + ";"
    run_sql(sql)
    total_spot += len(batch)
    print(f"  已插入 {total_spot}/{len(insert_values)} 条景点")

# 验证
train_count = run_sql("SELECT COUNT(*) FROM train_schedule")
spot_count = run_sql("SELECT COUNT(*) FROM scenic_spot")
low_cities = run_sql("SELECT city, COUNT(*) FROM scenic_spot GROUP BY city HAVING COUNT(*) < 6 ORDER BY COUNT(*)")
print(f"\n=== 验证 ===")
print(f"火车线路: {train_count} 条")
print(f"景点总数: {spot_count} 个")
if low_cities:
    print(f"仍不足6个景点的城市: {len(low_cities.split(chr(10)))} 个")
    for l in low_cities.split('\n')[:10]:
        print(f"  {l.strip()}")
else:
    print("所有城市均有至少6个景点 ✓")