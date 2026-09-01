import itertools, sys

with open('reverse_train_data.sql', 'r', encoding='utf-8') as f:
    content = f.read()

routes = ['广州', '长沙', '武汉', '南昌', '杭州', '上海', '南京']
results = {}
for a, b in itertools.permutations(routes, 2):
    pattern = "'" + a + "','" + b + "'"
    count = len([l for l in content.split('\n') if pattern in l])
    if count > 0:
        sys.stdout.write(f'{a} -> {b}: {count} entries\n')

if not any(count > 0 for _, count in results.items()):
    sys.stdout.write('No routes found in the generated file.\n')
    sys.stdout.write(f'File size: {len(content)} bytes\n')
    sys.stdout.write(f'First 200 chars: {content[:200]}\n')