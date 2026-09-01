import re
from collections import defaultdict

# Read the SQL file
with open('transport_full_data.sql', 'r', encoding='utf-8') as f:
    content = f.read()

# Extract train segments
train_section = content[content.find('INSERT INTO train_schedule'):]
train_section = train_section[:train_section.find('INSERT INTO flight_schedule')]

# Parse each segment
segments = []
lines = train_section.split('\n')
for line in lines:
    line = line.strip()
    if line.startswith('(NULL,'):
        # Extract fields
        match = re.search(r"\(NULL,\s*'([^']*)',\s*'([^']*)',\s*'([^']*)',\s*'([^']*)',\s*'([^']*)',\s*'([^']*)',\s*([\d.]+)\)", line)
        if match:
            train_no, train_type, from_city, to_city, depart, arrive, price = match.groups()
            segments.append((train_no, train_type, from_city, to_city, depart, arrive, float(price)))

# Group by train_no
by_train = defaultdict(list)
for s in segments:
    by_train[s[0]].append(s)

# Find missing reverse routes
reverse_segments = []
for train_no, segs in by_train.items():
    for ts in segs:
        tn, tt, fc, tc, dep, arr, pr = ts
        reverse_exists = any(
            s[0] == tn and s[3] == fc and s[2] == tc
            for s in segs
        )
        if not reverse_exists and fc != tc:
            h1, m1 = map(int, dep.split(':'))
            h2, m2 = map(int, arr.split(':'))
            duration = (h2 * 60 + m2) - (h1 * 60 + m1)
            if duration < 0:
                duration += 24 * 60

            # New departure: roughly 14:00 + offset based on original departure
            new_depart_h = 14 + (h1 - 7)
            new_depart_m = m1
            if new_depart_h >= 24:
                new_depart_h -= 24
            new_depart = f'{new_depart_h:02d}:{new_depart_m:02d}'

            new_total = new_depart_h * 60 + new_depart_m + duration
            new_arrive_h = (new_total // 60) % 24
            new_arrive_m = new_total % 60
            new_arrive = f'{new_arrive_h:02d}:{new_arrive_m:02d}'

            reverse_segments.append((tn, tt, tc, fc, new_depart, new_arrive, pr))

# Generate SQL
with open('reverse_train_data.sql', 'w', encoding='utf-8') as out:
    out.write(f'-- Generated reverse direction routes ({len(reverse_segments)} segments)\n')
    out.write('INSERT INTO train_schedule (user_id, train_no, train_type, from_city, to_city, depart_time, arrive_time, price) VALUES\n')
    for i, (no, typ, frm, to, dep, arr, price) in enumerate(reverse_segments):
        comma = ',' if i < len(reverse_segments) - 1 else ';'
        out.write(f"(NULL, '{no}', '{typ}', '{frm}', '{to}', '{dep}', '{arr}', {price:.0f}){comma}\n")
print(f'Done! Generated {len(reverse_segments)} segments to reverse_train_data.sql')