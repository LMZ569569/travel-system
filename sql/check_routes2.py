import itertools

with open('reverse_train_data.sql', 'r', encoding='utf-8') as f:
    content = f.read()

routes = ['广州', '长沙', '武汉', '南昌', '杭州', '上海', '南京']
results = {}
for a, b in itertools.permutations(routes, 2):
    pattern = "'" + a + "','" + b + "'"
    count = len([l for l in content.split('\n') if pattern in l])
    if count > 0:
        print(f'{a} -> {b}: {count} entries')