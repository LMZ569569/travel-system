-- ============================================
-- 增量脚本：新增「交通班次」和「酒店」两张表
-- 用于已有 travel_plan 数据库的情况（不影响你已导入的景点数据）
-- 在 Navicat 中打开本文件直接运行即可
-- ============================================

USE travel_plan;

-- 1. 交通班次表（火车/航班，用户可自行添加）
CREATE TABLE IF NOT EXISTS transport_schedule (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '班次ID',
    user_id     BIGINT       COMMENT '添加者（NULL=系统内置示例）',
    type        VARCHAR(10)  NOT NULL COMMENT '类型：train/flight',
    schedule_no VARCHAR(20)  NOT NULL COMMENT '车次/航班号',
    from_city   VARCHAR(50)  NOT NULL COMMENT '出发城市',
    to_city     VARCHAR(50)  NOT NULL COMMENT '到达城市',
    depart_time VARCHAR(20)  NOT NULL COMMENT '出发时间 HH:mm',
    arrive_time VARCHAR(20)  NOT NULL COMMENT '到达时间 HH:mm',
    price       DECIMAL(10,2) DEFAULT 0 COMMENT '参考票价',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_transport_user FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT='交通班次表';

-- 2. 酒店表
CREATE TABLE IF NOT EXISTS hotel (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '酒店ID',
    user_id     BIGINT       COMMENT '添加者（NULL=系统内置示例）',
    name        VARCHAR(100) NOT NULL COMMENT '酒店名称',
    city        VARCHAR(50)  COMMENT '所在城市',
    address     VARCHAR(255) COMMENT '地址',
    latitude    DECIMAL(10,6) COMMENT '纬度',
    longitude   DECIMAL(10,6) COMMENT '经度',
    price       DECIMAL(10,2) DEFAULT 0 COMMENT '参考房价',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_hotel_user FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT='酒店表';

-- 3. 示例交通班次（火车/航班，可自行添加更多）
INSERT INTO transport_schedule (user_id, type, schedule_no, from_city, to_city, depart_time, arrive_time, price) VALUES
(NULL, 'train', 'G1',   '北京', '上海', '07:00', '11:29', 553.00),
(NULL, 'train', 'G3',   '北京', '上海', '08:00', '12:26', 553.00),
(NULL, 'train', 'G101', '北京', '杭州', '06:43', '12:19', 538.00),
(NULL, 'train', 'G19',  '北京', '杭州', '08:00', '13:29', 538.00),
(NULL, 'train', 'G27',  '上海', '杭州', '07:00', '08:30', 73.00),
(NULL, 'train', 'G35',  '上海', '杭州', '09:00', '10:30', 73.00),
(NULL, 'train', 'G1371','上海', '南京', '07:00', '08:20', 135.00),
(NULL, 'train', 'G7002','南京', '上海', '07:00', '08:20', 135.00),
(NULL, 'train', 'D3065','杭州', '南京', '08:00', '10:30', 118.00),
(NULL, 'flight','MU5101','北京', '上海', '08:00', '10:15', 620.00),
(NULL, 'flight','CA1831','北京', '杭州', '09:00', '11:20', 680.00),
(NULL, 'flight','CZ3521','广州', '上海', '08:30', '10:50', 720.00),
(NULL, 'flight','HU7601','北京', '广州', '08:00', '11:20', 890.00),
(NULL, 'flight','MU2331','西安', '上海', '09:00', '11:15', 610.00);

-- 4. 示例酒店（可自行添加更多）
INSERT INTO hotel (user_id, name, city, address, latitude, longitude, price) VALUES
(NULL, '北京王府井希尔顿酒店', '北京', '北京市东城区王府井东街8号', 39.914100, 116.411900, 880.00),
(NULL, '北京前门建国饭店', '北京', '北京市东城区前门东大街', 39.899300, 116.398100, 520.00),
(NULL, '上海外滩华尔道夫酒店', '上海', '上海市黄浦区中山东一路2号', 31.237500, 121.490700, 1280.00),
(NULL, '上海静安香格里拉大酒店', '上海', '上海市静安区延安中路1218号', 31.226700, 121.447700, 980.00),
(NULL, '杭州西湖国宾馆', '杭州', '浙江省杭州市西湖区杨公堤18号', 30.232800, 120.132900, 1180.00),
(NULL, '杭州黄龙饭店', '杭州', '浙江省杭州市西湖区曙光路120号', 30.270200, 120.130700, 680.00),
(NULL, '南京金陵饭店', '南京', '江苏省南京市鼓楼区汉中路2号', 32.042100, 118.782600, 760.00),
(NULL, '广州白天鹅宾馆', '广州', '广东省广州市荔湾区沙面南街1号', 23.108300, 113.240500, 920.00),
(NULL, '成都香格里拉大酒店', '成都', '四川省成都市锦江区滨江东路9号', 30.648800, 104.086700, 860.00),
(NULL, '西安威斯汀大酒店', '西安', '陕西省西安市雁塔区慈恩路66号', 34.218900, 108.964300, 780.00);
