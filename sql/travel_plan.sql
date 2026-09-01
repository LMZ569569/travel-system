-- ============================================
-- 旅游规划系统 数据库建表脚本
-- 数据库名：travel_plan
-- 字符集：utf8mb4（支持中文和 emoji）
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS travel_plan
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE travel_plan;

-- ============================================
-- 1. 用户表
-- ============================================
CREATE TABLE user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码（加密存储）',
    nickname    VARCHAR(50)  COMMENT '昵称',
    avatar      VARCHAR(255) COMMENT '头像URL',
    phone       VARCHAR(20)  COMMENT '手机号',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) COMMENT='用户表';

-- ============================================
-- 2. 景点表
-- ============================================
CREATE TABLE scenic_spot (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '景点ID',
    name        VARCHAR(100)  NOT NULL COMMENT '景点名称',
    description TEXT          COMMENT '景点介绍',
    province    VARCHAR(50)   COMMENT '所在省份',
    city        VARCHAR(50)   COMMENT '所在城市',
    level       VARCHAR(10)   COMMENT 'A级评定（5A/4A/3A）',
    address     VARCHAR(255)  COMMENT '详细地址',
    latitude    DECIMAL(10,6) COMMENT '纬度',
    longitude   DECIMAL(10,6) COMMENT '经度',
    image       VARCHAR(255)  COMMENT '图片URL',
    price       DECIMAL(10,2) DEFAULT 0 COMMENT '门票价格',
    open_time   VARCHAR(50)   COMMENT '开放时间',
    rating      DECIMAL(2,1)  DEFAULT 0 COMMENT '用户评分',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间'
) COMMENT='景点表';

-- ============================================
-- 3. 行程表
-- ============================================
CREATE TABLE itinerary (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '行程ID',
    user_id     BIGINT       NOT NULL COMMENT '所属用户',
    title       VARCHAR(100) NOT NULL COMMENT '行程标题',
    province    VARCHAR(50)  COMMENT '目的地省份',
    city        VARCHAR(50)  COMMENT '目的地城市',
    start_date  DATE         COMMENT '开始日期',
    end_date    DATE         COMMENT '结束日期',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT fk_itinerary_user FOREIGN KEY (user_id) REFERENCES user(id)
) COMMENT='行程表';

-- ============================================
-- 4. 行程明细表（一个行程每天的安排）
-- ============================================
CREATE TABLE itinerary_item (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    itinerary_id    BIGINT       NOT NULL COMMENT '所属行程',
    scenic_spot_id  BIGINT       NOT NULL COMMENT '景点',
    day             INT          COMMENT '第几天',
    sort_order      INT          COMMENT '当天游玩顺序',
    note            VARCHAR(255) COMMENT '备注',
    CONSTRAINT fk_item_itinerary FOREIGN KEY (itinerary_id) REFERENCES itinerary(id),
    CONSTRAINT fk_item_spot FOREIGN KEY (scenic_spot_id) REFERENCES scenic_spot(id)
) COMMENT='行程明细表';

-- ============================================
-- 5. 收藏表
-- ============================================
CREATE TABLE favorite (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    user_id         BIGINT NOT NULL COMMENT '用户',
    scenic_spot_id  BIGINT NOT NULL COMMENT '景点',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES user(id),
    CONSTRAINT fk_fav_spot FOREIGN KEY (scenic_spot_id) REFERENCES scenic_spot(id)
) COMMENT='收藏表';

-- ============================================
-- 测试数据（方便开发时查看效果）
-- ============================================

-- 测试用户
INSERT INTO user (username, password, nickname) VALUES
('admin', '123456', '管理员'),
('test',  '123456', '测试用户');

-- 测试景点（按省份/城市分组，含 A 级评定）
INSERT INTO scenic_spot (name, description, province, city, level, address, latitude, longitude, price, open_time, rating) VALUES
-- 北京
('故宫博物院', '中国明清两代的皇家宫殿，世界文化遗产', '北京', '北京', '5A', '北京市东城区景山前街4号', 39.916344, 116.397154, 60.00, '08:30-17:00', 4.8),
('颐和园', '中国现存规模最大、保存最完整的皇家园林', '北京', '北京', '5A', '北京市海淀区新建宫门路19号', 39.999961, 116.275522, 30.00, '06:30-18:00', 4.7),
('八达岭长城', '明长城中保存最好的一段，世界文化遗产', '北京', '北京', '5A', '北京市延庆区G6京藏高速58号出口', 40.354134, 116.018249, 40.00, '07:30-17:30', 4.6),
('天坛公园', '明清两代皇帝祭天祈谷的场所', '北京', '北京', '5A', '北京市东城区天坛东里甲1号', 39.882201, 116.406605, 15.00, '06:00-22:00', 4.7),
('圆明园遗址公园', '清代大型皇家园林遗址', '北京', '北京', '4A', '北京市海淀区清华西路28号', 40.008864, 116.299408, 10.00, '07:00-21:00', 4.5),
-- 浙江
('西湖风景名胜区', '杭州标志性景点，三面云山一面城', '浙江', '杭州', '5A', '浙江省杭州市西湖区龙井路1号', 30.242152, 120.150081, 0.00, '全天开放', 4.9),
('灵隐寺', '杭州最早的名刹，济公出家地', '浙江', '杭州', '5A', '浙江省杭州市西湖区法云弄1号', 30.240613, 120.101005, 45.00, '07:00-18:00', 4.7),
('千岛湖风景区', '以千岛、秀水、金腰带为主要特色的湖泊景区', '浙江', '杭州', '5A', '浙江省杭州市淳安县千岛湖镇', 29.604610, 119.017685, 130.00, '08:00-17:00', 4.6),
('西溪国家湿地公园', '中国首个国家湿地公园', '浙江', '杭州', '5A', '浙江省杭州市西湖区天目山路518号', 30.270670, 120.066990, 80.00, '08:30-17:30', 4.5),
-- 上海
('外滩', '上海标志性景观，万国建筑博览群', '上海', '上海', '4A', '上海市黄浦区中山东一路', 31.239703, 121.490317, 0.00, '全天开放', 4.8),
('东方明珠广播电视塔', '上海地标建筑，可俯瞰全城', '上海', '上海', '5A', '上海市浦东新区世纪大道1号', 31.239690, 121.499820, 220.00, '08:00-21:30', 4.6),
('上海迪士尼乐园', '中国大陆首座迪士尼主题乐园', '上海', '上海', '5A', '上海市浦东新区川沙新镇黄赵路310号', 31.143378, 121.657119, 399.00, '08:30-20:30', 4.8),
-- 四川
('都江堰景区', '世界文化遗产，古代水利工程典范', '四川', '成都', '5A', '四川省成都市都江堰市公园路', 31.007472, 103.619408, 80.00, '08:00-18:00', 4.8),
('青城山', '道教发源地之一，青城天下幽', '四川', '成都', '5A', '四川省成都市都江堰市青城山镇', 30.901558, 103.575125, 80.00, '08:00-17:30', 4.7),
('宽窄巷子', '成都最具代表性的历史文化街区', '四川', '成都', '4A', '四川省成都市青羊区金河路口', 30.669890, 104.056510, 0.00, '全天开放', 4.5),
-- 陕西
('秦始皇兵马俑博物馆', '世界第八大奇迹，秦代地下军团', '陕西', '西安', '5A', '陕西省西安市临潼区秦陵北路', 34.384106, 109.278580, 120.00, '08:30-17:00', 4.9),
('大雁塔', '唐代玄奘译经之地，西安地标', '陕西', '西安', '5A', '陕西省西安市雁塔区雁塔南路', 34.218520, 108.964040, 50.00, '08:00-18:00', 4.6),
('华清宫', '唐代皇家温泉行宫，西安事变发生地', '陕西', '西安', '5A', '陕西省西安市临潼区华清路38号', 34.364840, 109.211700, 120.00, '07:30-18:00', 4.6),
-- 江苏
('中山陵', '孙中山先生陵寝，南京地标', '江苏', '南京', '5A', '江苏省南京市玄武区石象路7号', 32.058380, 118.848940, 0.00, '08:30-17:00', 4.7),
('夫子庙秦淮风光带', '南京历史文化名街，秦淮河畔', '江苏', '南京', '5A', '江苏省南京市秦淮区贡院街', 32.020650, 118.787180, 0.00, '全天开放', 4.6),
-- 湖北
('黄鹤楼', '江南三大名楼之首，武汉地标', '湖北', '武汉', '5A', '湖北省武汉市武昌区蛇山西山坡特1号', 30.545390, 114.301640, 70.00, '08:00-18:00', 4.6),
('东湖生态旅游风景区', '中国最大的城中湖', '湖北', '武汉', '5A', '湖北省武汉市武昌区沿湖大道16号', 30.555680, 114.378860, 0.00, '全天开放', 4.6),
-- 广东
('白云山风景区', '广州城市绿肺，羊城第一秀', '广东', '广州', '5A', '广东省广州市白云区广园中路801号', 23.168860, 113.292100, 5.00, '06:00-21:00', 4.6),
('广州长隆旅游度假区', '大型综合主题乐园', '广东', '广州', '5A', '广东省广州市番禺区汉溪大道东299号', 22.998930, 113.327500, 250.00, '09:30-18:00', 4.8);
