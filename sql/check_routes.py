with open('reverse_train_data.sql', 'r', encoding='utf-8') as f:
    content = f.read()

checks = ['广州', '长沙', '武汉', '南昌', '杭州', '上海', '南京']
for c in checks:
    for c2 in checks:
        if c != c2:
            lines = [l for l in content.split('\n') if f"'{c}','{c2}'" in l]
            status = f'FOUND ({len(lines)} entr)' if lines else 'MISSING'
            print(f'{c} -> {c2}: {status}')