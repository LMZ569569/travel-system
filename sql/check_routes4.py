import itertools, sys

with open('reverse_train_data.sql', 'r', encoding='utf-8') as f:
    content = f.read()

routes = ['广州', '长沙', '武汉', '南昌', '杭州', '上海', '南京']
found_any = False
for a, b in itertools.permutations(routes, 2):
    # Check with or without space after comma
    pattern1 = "'" + a + "','" + b + "'"
    pattern2 = "'" + a + "', '" + b + "'"
    count1 = len([l for l in content.split('\n') if pattern1 in l])
    count2 = len([l for l in content.split('\n') if pattern2 in l])
    count = count1 + count2
    if count > 0:
        sys.stdout.write(f'{a} -> {b}: {count} entries\n')
        found_any = True

if not found_any:
    sys.stdout.write('No routes found!\n')
    # Show a sample line
    for line in content.split('\n'):
        if '广州' in line:
            sys.stdout.write(f'Sample: {line[:120]}...\n')
            break