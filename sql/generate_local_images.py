# -*- coding: utf-8 -*-
"""为每个景点生成本地SVG图片，保存到后端静态资源目录"""

import subprocess
import os

MYSQL = r'C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe'
PASS = '20060204'
DB = 'travel_plan'

# 后端静态资源目录
STATIC_DIR = r'C:\Users\zheyue\Desktop\毕设\travel-system\backend\src\main\resources\static\images'

def run_sql(sql):
    cmd = [MYSQL, '-u', 'root', f'-p{PASS}', '-D', DB, '-N', '-B', '--default-character-set=utf8']
    r = subprocess.run(cmd, input=sql.encode('utf-8'), capture_output=True, timeout=60)
    return r.stdout.decode('utf-8').strip()

# 获取所有景点
rows = run_sql("SELECT id, name, city, province FROM scenic_spot ORDER BY id")
lines = rows.strip().split('\n')

spots = []
for line in lines:
    parts = line.strip().split('\t')
    if len(parts) >= 3:
        spots.append({
            'id': parts[0].strip(),
            'name': parts[1].strip(),
            'city': parts[2].strip(),
            'province': parts[3].strip() if len(parts) >= 4 else ''
        })

print(f"共 {len(spots)} 个景点，正在生成SVG图片...")

# 确保目录存在
os.makedirs(STATIC_DIR, exist_ok=True)

# 渐变色方案（循环使用）
GRADIENTS = [
    ('#667eea', '#764ba2'),  # 紫蓝
    ('#f093fb', '#f5576c'),  # 粉红
    ('#4facfe', '#00f2fe'),  # 蓝青
    ('#43e97b', '#38f9d7'),  # 绿
    ('#fa709a', '#fee140'),  # 橙粉
    ('#a18cd1', '#fbc2eb'),  # 淡紫
    ('#fccb90', '#d57eeb'),  # 橙紫
    ('#e0c3fc', '#8ec5fc'),  # 紫蓝淡
    ('#f5576c', '#ff6f91'),  # 红粉
    ('#30cfd0', '#330867'),  # 青紫
    ('#a8edea', '#fed6e3'),  # 青粉
    ('#5ee7df', '#b490ca'),  # 青紫
    ('#d299c2', '#fef9d7'),  # 粉黄
    ('#fdfcfb', '#e2d1c3'),  # 米白
    ('#667eea', '#f5576c'),  # 紫红
    ('#96fbc4', '#f9f586'),  # 绿黄
    ('#fbab7e', '#f7ce68'),  # 橙黄
    ('#cfd9df', '#e2ebf0'),  # 灰白
    ('#a1c4fd', '#c2e9fb'),  # 淡蓝
    ('#d4fc79', '#96e6a1'),  # 淡绿
]

def generate_svg(spot_id, name, city, province, color_idx):
    c1, c2 = GRADIENTS[color_idx % len(GRADIENTS)]
    # 根据景点名称长度调整字号
    name_len = len(name)
    if name_len <= 4:
        font_size = 42
    elif name_len <= 6:
        font_size = 36
    elif name_len <= 8:
        font_size = 30
    else:
        font_size = 24

    # SVG 内容 - 现代简约风格，带渐变背景和景点名称
    svg = f'''<svg xmlns="http://www.w3.org/2000/svg" width="800" height="600" viewBox="0 0 800 600">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:{c1};stop-opacity:1" />
      <stop offset="100%" style="stop-color:{c2};stop-opacity:1" />
    </linearGradient>
    <linearGradient id="overlay" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" style="stop-color:rgba(255,255,255,0.1)" />
      <stop offset="100%" style="stop-color:rgba(0,0,0,0.1)" />
    </linearGradient>
  </defs>
  <rect width="800" height="600" fill="url(#bg)" />
  <rect width="800" height="600" fill="url(#overlay)" />
  <!-- 装饰性圆圈 -->
  <circle cx="100" cy="100" r="150" fill="rgba(255,255,255,0.05)" />
  <circle cx="700" cy="500" r="200" fill="rgba(0,0,0,0.03)" />
  <circle cx="650" cy="150" r="80" fill="rgba(255,255,255,0.06)" />
  <circle cx="150" cy="450" r="100" fill="rgba(0,0,0,0.04)" />
  <!-- 中央内容 -->
  <text x="400" y="260" text-anchor="middle" fill="white" font-size="{font_size}px" font-weight="bold" font-family="PingFang SC, Microsoft YaHei, sans-serif">{name}</text>
  <text x="400" y="320" text-anchor="middle" fill="rgba(255,255,255,0.8)" font-size="18px" font-family="PingFang SC, Microsoft YaHei, sans-serif">{city}</text>'''
    if province and province != city:
        svg += f'''
  <text x="400" y="350" text-anchor="middle" fill="rgba(255,255,255,0.6)" font-size="14px" font-family="PingFang SC, Microsoft YaHei, sans-serif">{province}</text>'''
    svg += '''
  <!-- 底部装饰线 -->
  <line x1="300" y1="400" x2="500" y2="400" stroke="rgba(255,255,255,0.3)" stroke-width="2" />
  <circle cx="400" cy="400" r="4" fill="rgba(255,255,255,0.5)" />
</svg>'''
    return svg


updated = 0
for i, s in enumerate(spots):
    svg_content = generate_svg(s['id'], s['name'], s['city'], s['province'], i)
    file_path = os.path.join(STATIC_DIR, f"spot_{s['id']}.svg")

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(svg_content)

    # 更新数据库中的图片URL为本地路径
    local_url = f"/images/spot_{s['id']}.svg"
    sql = f"UPDATE scenic_spot SET image='{local_url}' WHERE id={s['id']}"
    run_sql(sql)

    updated += 1
    if updated % 500 == 0:
        print(f"  已生成 {updated}/{len(spots)}")

print(f"完成！共生成 {updated} 个SVG图片到: {STATIC_DIR}")
print(f"数据库图片URL已更新为本地路径 /images/spot_{{id}}.svg")