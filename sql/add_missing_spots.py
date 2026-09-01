# -*- coding: utf-8 -*-
"""补充0景点城市的景点数据，并检查规划问题"""

import subprocess

MYSQL = r'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
PASS = '20060204'
DB = 'travel_plan'

def run_sql(sql):
    cmd = [MYSQL, '-u', 'root', f'-p{PASS}', '-D', DB, '-N']
    r = subprocess.run(cmd, input=sql.encode('utf-8'), capture_output=True, timeout=60)
    return r.stdout.decode('utf-8').strip()

# 检查哪些城市在景点表里没有数据
print("=== 检查无景点城市 ===")
# 获取所有城市（从酒店表取，因为酒店已覆盖所有城市）
cities_raw = run_sql("SELECT DISTINCT city FROM hotel ORDER BY city")
all_cities = [c.strip() for c in cities_raw.split('\n') if c.strip()]
print(f"酒店覆盖城市: {len(all_cities)} 个")

# 获取已有景点的城市
spots_raw = run_sql("SELECT DISTINCT city FROM scenic_spot")
has_spots = set(s.strip() for s in spots_raw.split('\n') if s.strip())

# 找出无景点城市
no_spots = [c for c in all_cities if c not in has_spots]
print(f"无景点城市: {len(no_spots)} 个")
for c in no_spots:
    print(f"  {c}")

# 为这些城市补充景点
import random
random.seed(456)

existing_names_raw = run_sql("SELECT DISTINCT name FROM scenic_spot")
existing_names = set(n.strip() for n in existing_names_raw.split('\n') if n.strip())

# 针对珠海等城市补充真实景点
real_spots = {
    '珠海': ['珠海长隆海洋王国', '珠海渔女雕像', '情侣路', '圆明新园', '东澳岛', '外伶仃岛', '珠海博物馆'],
    '中山': ['孙中山故居纪念馆', '中山詹园', '中山温泉', '孙文西路步行街', '金钟水库', '中山影视城', '中山博物馆'],
    '东莞': ['松山湖风景区', '可园博物馆', '鸦片战争博物馆', '粤晖园', '银瓶山森林公园', '华阳湖湿地公园', '东莞展览馆'],
    '惠州': ['惠州西湖', '罗浮山', '巽寮湾', '大亚湾', '南昆山', '双月湾', '惠州博物馆'],
    '江门': ['开平碉楼', '上下川岛', '圭峰山', '小鸟天堂', '古劳水乡', '恩平温泉', '江门博物馆'],
    '肇庆': ['七星岩', '鼎湖山', '端州古城', '星湖湿地公园', '德庆孔庙', '怀集燕岩', '肇庆博物馆'],
    '佛山': ['佛山祖庙', '西樵山', '南风古灶', '清晖园', '岭南天地', '佛山科学馆', '三水荷花世界'],
    '汕头': ['南澳岛', '汕头老城', '潮汕历史文化博览中心', '礐石风景区', '莲花山', '妈屿岛', '汕头大学'],
    '湛江': ['湖光岩', '金沙湾', '东海岛', '特呈岛', '徐闻珊瑚礁', '硇洲岛', '湛江海滨公园'],
    '茂名': ['中国第一滩', '放鸡岛', '浪漫海岸', '冼太庙', '天马山', '玉湖风景区', '茂名森林公园'],
    '韶关': ['丹霞山', '南华寺', '珠玑古巷', '梅关古道', '广东大峡谷', '南岭国家森林公园', '韶关国家森林公园'],
    '梅州': ['雁南飞茶田', '梅州客天下', '叶剑英故居', '松口古镇', '五指石', '龙归寨瀑布', '梅州博物馆'],
    '清远': ['连州地下河', '英西峰林', '黄腾峡', '古龙峡', '飞来寺', '宝晶宫', '清远温泉'],
    '潮州': ['广济桥', '牌坊街', '潮州古城', '开元寺', '韩文公祠', '西湖公园', '潮州博物馆'],
    '揭阳': ['揭阳楼', '黄岐山', '揭阳学宫', '德安里', '揭西大洋', '京明温泉', '揭阳博物馆'],
    '汕尾': ['红海湾', '莲花山', '凤山妈祖', '玄武山', '铜鼎山', '金厢滩', '汕尾博物馆'],
    '河源': ['万绿湖', '桂山', '恐龙博物馆', '野趣沟', '霍山', '苏家围', '河源温泉'],
    '阳江': ['海陵岛', '大角湾', '闸坡', '凌霄岩', '春湾石林', '东平珍珠湾', '阳江博物馆'],
    '云浮': ['国恩寺', '蟠龙洞', '天露山', '龙山温泉', '大王山', '新兴温泉', '云浮博物馆'],
}

# 从省份表找城市对应的省份
prov_cache = {}
for c in no_spots + list(real_spots.keys()):
    p = run_sql(f"SELECT province FROM scenic_spot WHERE city='{c}' LIMIT 1")
    if p:
        prov_cache[c] = p.strip()
    # 从酒店表拿省份
    p2 = run_sql(f"SELECT province FROM hotel WHERE city='{c}' LIMIT 1")
    if p2:
        prov_cache[c] = p2.strip()

# 生成插入数据
inserts = []
for city in no_spots:
    prov = prov_cache.get(city, city)
    if city in real_spots:
        spots = real_spots[city]
    else:
        # 自动生成
        spot_types = ['公园', '广场', '博物馆', '景区', '古镇', '寺', '塔']
        spots = []
        for j, st in enumerate(spot_types):
            name = f"{city}{st}"
            if name in existing_names:
                name = f"{city}{city}第{j+1}景"
            spots.append(name)
    
    for i, name in enumerate(spots[:7]):
        open_time = random.choice(['08:00-17:30', '08:30-17:00', '09:00-17:00', '08:00-18:00', '全天开放', '09:00-21:00'])
        level = random.choice(['4A', '4A', '3A', '4A', '5A'])
        price = 0.00 if random.random() < 0.3 else round(random.uniform(10, 150), 2)
        rating = round(3.5 + random.random() * 1.5, 1)
        visit_dur = round(2.0 + random.random() * 4.0, 1)
        lat = round(22.0 + random.random() * 5.0, 6)
        lng = round(113.0 + random.random() * 3.0, 6)
        desc = f"{city}知名旅游景区，{name}，风景优美，设施完善"
        addr = f"位于{city}市区"
        name_safe = name.replace("'", "''")
        desc_safe = desc.replace("'", "''")
        inserts.append(f"('{name_safe}','{desc_safe}','{prov}','{city}','{level}','{addr}',{lat},{lng},{price:.2f},'{open_time}',{rating:.1f},{visit_dur:.1f})")

print(f"\n新增景点: {len(inserts)} 条")

# 批量写入
for i in range(0, len(inserts), 100):
    batch = inserts[i:i+100]
    sql = "INSERT INTO scenic_spot (name, description, province, city, level, address, latitude, longitude, price, open_time, rating, visit_duration) VALUES\n"
    sql += ",\n".join(batch) + ";"
    run_sql(sql)
    print(f"  已插入 {min(i+100, len(inserts))}/{len(inserts)}")

# 验证
total = run_sql("SELECT COUNT(*) FROM scenic_spot")
low = run_sql("SELECT city, COUNT(*) FROM scenic_spot GROUP BY city HAVING COUNT(*) < 6 ORDER BY COUNT(*)")
print(f"\n验证:")
print(f"景点总数: {total}")
if low:
    c = len(low.split('\n'))
    print(f"仍不足6个景点的城市: {c} 个")
    for l in low.split('\n')[:5]:
        print(f"  {l.strip()}")
else:
    print("所有城市均有至少6个景点 ✓")