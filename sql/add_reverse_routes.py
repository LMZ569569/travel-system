# -*- coding: utf-8 -*-
"""为每条正向火车线路生成对应的反向线路"""

import subprocess

MYSQL = r'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
PASS = '20060204'
DB = 'travel_plan'

def run_sql(sql):
    cmd = [MYSQL, '-u', 'root', f'-p{PASS}', '-D', DB, '-N']
    r = subprocess.run(cmd, input=sql.encode('utf-8'), capture_output=True, timeout=60)
    return r.stdout.decode('utf-8').strip()

# 获取所有正向线路
print("获取所有火车线路...")
rows = run_sql("SELECT from_city, to_city, train_no, train_type, depart_time, arrive_time, price FROM train_schedule ORDER BY from_city, to_city")

lines = [l.strip() for l in rows.split('\n') if l.strip()]
print(f"总线路: {len(lines)} 条")

# 构建已有反向线路集合
existing_raw = run_sql("SELECT from_city, to_city, train_no FROM train_schedule")
existing_rev = set()
for r in existing_raw.split('\n'):
    parts = r.strip().split('\t')
    if len(parts) >= 3:
        existing_rev.add(f"{parts[0]}|{parts[1]}|{parts[2]}")

# 为每条线路生成反向
inserts = []
count = 0
for line in lines:
    parts = line.split('\t')
    if len(parts) < 7:
        continue
    f, t, no, tt, dep, arr, price = parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]
    rev_key = f"{t}|{f}|{no}"  # 用同一车次号的反向
    if rev_key in existing_rev:
        existing_rev.discard(rev_key)  # 这行已存在，跳过
        continue
    
    # 生成反向车次号（在原车次号后加R）
    rev_no = f"{no}R"
    rev_key2 = f"{t}|{f}|{rev_no}"
    if rev_key2 in existing_rev:
        continue
    
    inserts.append((t, f, rev_no, tt, arr, dep, price))
    count += 1

print(f"需要添加的反向线路: {count} 条")

# 批量插入
batch_size = 200
total = 0
for i in range(0, len(inserts), batch_size):
    batch = inserts[i:i+batch_size]
    sql = "INSERT INTO train_schedule (user_id, train_no, train_type, from_city, to_city, depart_time, arrive_time, price) VALUES\n"
    rows = []
    for t, f, no, tt, dep, arr, price in batch:
        rows.append(f"(NULL,'{no}','{tt}','{t}','{f}','{dep}','{arr}',{price})")
    sql += ",\n".join(rows) + ";"
    run_sql(sql)
    total += len(batch)
    print(f"  已插入 {total}/{count}")

# 验证
r1 = run_sql("SELECT COUNT(*) FROM train_schedule")
r2 = run_sql("SELECT COUNT(*) FROM train_schedule WHERE from_city='佛山' AND to_city='广州'")
r3 = run_sql("SELECT COUNT(*) FROM train_schedule WHERE from_city='珠海' AND to_city='广州'")
r4 = run_sql("SELECT COUNT(*) FROM train_schedule WHERE from_city='广州' AND to_city='佛山'")
r5 = run_sql("SELECT COUNT(*) FROM train_schedule WHERE from_city='广州' AND to_city='珠海'")
print(f"\n验证:")
print(f"  火车总数: {r1}")
print(f"  广州→佛山: {r4}, 佛山→广州: {r2}")
print(f"  广州→珠海: {r5}, 珠海→广州: {r3}")