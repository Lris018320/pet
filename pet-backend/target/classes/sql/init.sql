-- ============================================================
-- 宠知 AI - 数据库初始化脚本
-- 数据库：pet_platform  字符集：utf8mb4
-- ============================================================

DROP DATABASE IF EXISTS `pet_platform`;
CREATE DATABASE `pet_platform` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `pet_platform`;

-- ----------------------------
-- 1. 用户信息表
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `account` VARCHAR(50) NOT NULL COMMENT '账号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色：user普通用户 vipVIP用户',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ----------------------------
-- 2. 管理员信息表
--   说明：admin 初始账号由后端 DataInitializer 在首次启动时
--   使用 BCrypt 编码 "123456" 后写入，避免明文密码污染 SQL 脚本。
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `account` VARCHAR(50) NOT NULL COMMENT '账号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员信息表';

-- ----------------------------
-- 3. 宠物档案表
-- ----------------------------
DROP TABLE IF EXISTS `pet_archive`;
CREATE TABLE `pet_archive` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '归属用户ID',
  `pet_name` VARCHAR(50) NOT NULL COMMENT '宠物名称',
  `breed` VARCHAR(50) NOT NULL COMMENT '品种',
  `pet_type` VARCHAR(10) DEFAULT NULL COMMENT '类型：猫/狗',
  `age` VARCHAR(20) DEFAULT NULL COMMENT '年龄',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别：公/母/未知',
  `weight` VARCHAR(20) DEFAULT NULL COMMENT '体重',
  `vaccine_status` VARCHAR(50) DEFAULT NULL COMMENT '疫苗状态',
  `sterilized` VARCHAR(10) DEFAULT NULL COMMENT '是否绝育：是/否',
  `health_status` VARCHAR(20) NOT NULL DEFAULT '良好' COMMENT '健康状况：良好/轻微异常/异常',
  `photo_url` VARCHAR(255) DEFAULT NULL COMMENT '宠物照片',
  `source` VARCHAR(20) DEFAULT 'manual' COMMENT '建档来源：manual手动/ai识别',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物档案表';

-- ----------------------------
-- 3.1 宠物健康记录表
-- ----------------------------
DROP TABLE IF EXISTS `pet_health_record`;
CREATE TABLE `pet_health_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `pet_id` BIGINT NOT NULL COMMENT '宠物档案ID',
  `type` VARCHAR(20) NOT NULL COMMENT '记录类型：体检/疫苗/驱虫/就诊',
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述/结果',
  `record_date` DATE NOT NULL COMMENT '发生日期',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_pet_id` (`pet_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物健康记录表';

-- ----------------------------
-- 4. 宠物识别记录表
-- ----------------------------
DROP TABLE IF EXISTS `recognition_record`;
CREATE TABLE `recognition_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '操作用户ID',
  `image_url` VARCHAR(255) NOT NULL COMMENT '识别图片URL',
  `pet_type` VARCHAR(10) DEFAULT NULL COMMENT '识别类型：猫/狗',
  `breed` VARCHAR(50) DEFAULT NULL COMMENT '识别品种',
  `confidence` DECIMAL(5,2) DEFAULT NULL COMMENT '置信度（%）',
  `success` TINYINT NOT NULL DEFAULT 1 COMMENT '是否识别成功：1成功 0失败',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物识别记录表';

-- ----------------------------
-- 5. 宠物资讯表
-- ----------------------------
DROP TABLE IF EXISTS `pet_info`;
CREATE TABLE `pet_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `content` LONGTEXT NOT NULL COMMENT '正文（富文本）',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `category` VARCHAR(20) DEFAULT NULL COMMENT '分类：喂养/洗护/疾病/习性',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1已发布 0下架',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `author` VARCHAR(50) DEFAULT '小知宠' COMMENT '作者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物资讯表';

-- ----------------------------
-- 6. 品种科普表
-- ----------------------------
DROP TABLE IF EXISTS `breed_knowledge`;
CREATE TABLE `breed_knowledge` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `breed_name` VARCHAR(50) NOT NULL COMMENT '品种名',
  `pet_type` VARCHAR(10) DEFAULT NULL COMMENT '类型：猫/狗',
  `description` TEXT COMMENT '品种简介',
  `personality` TEXT COMMENT '性格特点',
  `feeding_taboo` TEXT COMMENT '喂养禁忌',
  `care_tips` TEXT COMMENT '日常养护建议',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_breed_name` (`breed_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品种科普表';

-- ----------------------------
-- 7. AI 聊天消息表
-- ----------------------------
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '操作用户ID',
  `role` VARCHAR(20) NOT NULL COMMENT '消息角色：user用户 / assistant助手',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_time` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天消息表';

-- ----------------------------
-- 7.1 知识记录表
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_record`;
CREATE TABLE `knowledge_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `excerpt` VARCHAR(500) DEFAULT NULL COMMENT '摘要',
  `content` LONGTEXT COMMENT '正文（富文本）',
  `category` VARCHAR(20) DEFAULT NULL COMMENT '分类：健康护理/饮食营养/疾病防治/行为训练/日常护理',
  `thumbnail` VARCHAR(255) DEFAULT NULL COMMENT '缩略图',
  `tags` VARCHAR(255) DEFAULT NULL COMMENT '标签，逗号分隔',
  `pet_type` VARCHAR(10) DEFAULT '全部' COMMENT '宠物类型：猫/狗/全部',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `collect_count` INT NOT NULL DEFAULT 0 COMMENT '收藏数',
  `share_count` INT NOT NULL DEFAULT 0 COMMENT '分享数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1已发布 0草稿',
  `author` VARCHAR(50) DEFAULT '小知宠' COMMENT '作者',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识记录表';

-- ----------------------------
-- 初始数据：品种科普（供 Mock 识别返回使用）
-- ----------------------------
INSERT INTO `breed_knowledge` (`breed_name`,`pet_type`,`description`,`personality`,`feeding_taboo`,`care_tips`) VALUES
('金毛','狗','大型犬，性格温顺亲人，是家庭伴侣犬代表。','温顺、忠诚、对儿童友好。','忌喂巧克力、葡萄、洋葱；忌高盐人类剩饭。','每日梳毛，定期洗澡，保证运动量，关注髋关节。'),
('哈士奇','狗','中型工作犬，精力旺盛，外形俊朗。','活泼好动、好奇心强、服从性一般。','忌高糖高脂；忌长时间暴晒。','需大量运动，每周梳毛，注意肠胃调理。'),
('泰迪','狗','小型玩赏犬，毛卷不易掉毛。','聪明黏人、爱叫、易训练。','忌鸡骨头、忌高盐食物。','定期修剪造型，注意泪痕清理。'),
('拉布拉多','狗','中大型犬，性格温和，常作导盲犬。','聪明温顺、服从性高。','忌过量喂食易肥胖。','每日运动，控制饮食量。'),
('布偶猫','猫','大型长毛猫，蓝眼白色长毛。','温顺安静、粘人、忍耐力强。','忌牛奶、忌生肉。','每日梳毛防打结，定期化毛。'),
('英国短毛猫','猫','圆脸短毛猫，体格健壮。','温和独立、不爱吵闹。','忌高盐食物。','定期梳毛，控制食量防胖。'),
('橘猫','猫','中华田园猫常见花色，橘黄毛色。','亲人、贪吃、活泼。','忌过量喂食易肥胖。','控制饮食，保证运动。'),
('波斯猫','猫','长毛扁平脸猫，气质高雅。','安静温顺、喜清洁。','忌生水生肉。','每日梳毛，注意眼部清洁。');

-- ----------------------------
-- 初始数据：示例资讯 2 条
-- ----------------------------
INSERT INTO `pet_info` (`title`,`content`,`cover_image`,`category`,`status`) VALUES
('新手养宠必读：幼犬到家第一周护理指南','<p>幼犬到家前三天建议保持原食物不变，逐步过渡；准备独立安静的休息区域；2-3月龄前不宜外出接触其他犬只。</p>','','喂养',1),
('猫咪洗澡全攻略','<p>猫咪自我清洁能力强，室内猫一般 1-2 月洗一次即可；水温 38-40℃；洗后及时吹干防感冒。</p>','','洗护',1);

-- ============================================================
-- 8. 领养寄养信息表
-- ============================================================
DROP TABLE IF EXISTS `adoption`;
CREATE TABLE `adoption` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '发布用户ID',
  `type` VARCHAR(10) NOT NULL COMMENT '类型：adoption领养 / foster寄养',
  `pet_name` VARCHAR(50) NOT NULL COMMENT '宠物名称',
  `breed` VARCHAR(50) DEFAULT NULL COMMENT '品种',
  `pet_type` VARCHAR(10) DEFAULT NULL COMMENT '类型：猫/狗',
  `age` VARCHAR(20) DEFAULT NULL COMMENT '年龄',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别：公/母/未知',
  `photo_url` VARCHAR(255) DEFAULT NULL COMMENT '宠物照片',
  `description` TEXT COMMENT '详情描述',
  `contact` VARCHAR(100) DEFAULT NULL COMMENT '联系方式',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending待审核 approved已审核 rejected已拒绝 offline已下架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养寄养信息表';

-- ============================================================
-- 9. 领养寄养申请表
-- ============================================================
DROP TABLE IF EXISTS `adoption_application`;
CREATE TABLE `adoption_application` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `adoption_id` BIGINT NOT NULL COMMENT '领养寄养信息ID',
  `applicant_id` BIGINT NOT NULL COMMENT '申请人ID',
  `message` TEXT COMMENT '申请留言',
  `contact` VARCHAR(100) DEFAULT NULL COMMENT '申请人联系方式',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending待审核 approved已通过 rejected已拒绝',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_adoption_id` (`adoption_id`),
  KEY `idx_applicant_id` (`applicant_id`),
  UNIQUE KEY `uk_adoption_applicant` (`adoption_id`, `applicant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养寄养申请表';

-- ============================================================
-- 10. 社区帖子表
-- ============================================================
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '发帖用户ID',
  `title` VARCHAR(100) NOT NULL COMMENT '标题',
  `content` LONGTEXT NOT NULL COMMENT '正文',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `category` VARCHAR(20) DEFAULT NULL COMMENT '分类：日常/求助/晒宠/问答',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` INT NOT NULL DEFAULT 0 COMMENT '评论数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0下架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category` (`category`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子表';

-- ============================================================
-- 11. 评论表
-- ============================================================
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '评论用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（回复时）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ============================================================
-- 12. 帖子点赞表
-- ============================================================
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';

-- ============================================================
-- 13. 商品表
-- ============================================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
  `description` TEXT COMMENT '商品描述',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `images` TEXT COMMENT '轮播图URL，逗号分隔',
  `category` VARCHAR(20) DEFAULT NULL COMMENT '分类：食品/玩具/用品/医药',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
  `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ============================================================
-- 14. 购物车表
-- ============================================================
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================================
-- 15. 订单表
-- ============================================================
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总额',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending待付款 paid已付款 shipped已发货 completed已完成 cancelled已取消',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收件人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收件人电话',
  `receiver_address` VARCHAR(255) NOT NULL COMMENT '收件地址',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
  `ship_time` DATETIME DEFAULT NULL COMMENT '发货时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================================
-- 16. 订单明细表
-- ============================================================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称（快照）',
  `price` DECIMAL(10,2) NOT NULL COMMENT '单价（快照）',
  `quantity` INT NOT NULL COMMENT '数量',
  `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ============================================================
-- 初始演示数据：商品
-- ============================================================
INSERT INTO `product` (`name`,`description`,`price`,`stock`,`cover_image`,`category`,`status`) VALUES
('皇家狗粮 15kg','全价成犬粮，营养均衡，适合中大型犬。',298.00,100,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=premium%20dog%20food%20bag%2015kg%20on%20white%20background%20product%20photography&image_size=square_hd','食品',1),
('猫砂 10L 膨润土','高效结团，低尘除臭，猫咪如厕好伴侣。',49.90,200,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cat%20litter%2010L%20bag%20bentonite%20on%20white%20background%20product%20photography&image_size=square_hd','用品',1),
('宠物逗猫棒','弹性钢丝+羽毛铃铛，激发猫咪捕猎天性。',19.90,500,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cat%20teaser%20wand%20toy%20with%20feather%20on%20white%20background%20product%20photography&image_size=square_hd','玩具',1),
('狗狗磨牙棒 10支装','天然牛皮制作，清洁牙齿，缓解幼犬换牙期不适。',39.90,150,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=dog%20chew%20bones%2010%20pack%20on%20white%20background%20product%20photography&image_size=square_hd','食品',1),
('猫爬架 多层','剑麻抓柱+毛绒窝，满足猫咪攀爬磨爪需求。',268.00,50,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=multi%20level%20cat%20tree%20tower%20with%20scratching%20posts%20on%20white%20background%20product%20photography&image_size=square_hd','用品',1),
('宠物驱虫药 体内外同驱','一月一次，广谱驱虫，安全有效。',89.00,300,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20deworming%20medicine%20box%20on%20white%20background%20product%20photography&image_size=square_hd','医药',1);

-- ============================================================
-- 初始演示数据：领养寄养信息（已审核）
-- ============================================================
INSERT INTO `adoption` (`user_id`,`type`,`pet_name`,`breed`,`pet_type`,`age`,`gender`,`photo_url`,`description`,`contact`,`status`) VALUES
(1,'adoption','小橘','橘猫','猫','2个月','公','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=orange%20tabby%20kitten%20sitting%20looking%20up%20cute%20pet%20photography&image_size=square_hd','活泼亲人的小橘猫，已驱虫，希望找到有爱心的主人。','微信：petlover001','approved'),
(1,'foster','豆豆','泰迪','狗','3岁','母','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20brown%20toy%20poodle%20dog%20sitting%20smiling%20pet%20photography&image_size=square_hd','因出差两周，寻找有经验的寄养家庭，豆豆很乖不拆家。','电话：13800138000','approved'),
(1,'adoption','小白','比熊','狗','1岁','公','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=white%20bichon%20frise%20dog%20portrait%20smiling%20pet%20photography&image_size=square_hd','温顺粘人的比熊，因工作原因无法继续饲养，望好心人领养。','微信：bichon_home','approved');

-- ============================================================
-- 初始演示数据：社区帖子
-- ============================================================
INSERT INTO `post` (`user_id`,`title`,`content`,`cover_image`,`category`,`like_count`,`comment_count`,`status`) VALUES
(1,'我家金毛第一次洗澡的样子太可爱了','<p>今天带金毛去宠物店洗澡，全程配合，洗完香喷喷的，毛色也更亮了！</p>','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=golden%20retriever%20dog%20after%20bath%20wet%20fur%20cute%20pet%20photography&image_size=square_hd','晒宠',12,3,1),
(1,'新手养猫求助：猫咪不吃饭怎么办？','<p>刚接回家的小猫两天没好好吃东西了，只喝水，精神还行，需要去医院吗？</p>','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=worried%20kitten%20not%20eating%20looking%20at%20food%20bowl%20pet%20photography&image_size=square_hd','求助',5,8,1),
(1,'分享：自制狗狗零食教程','<p>用鸡胸肉和南瓜做的烘干零食，我家狗超爱吃，无添加更健康。做法很简单，切成薄片低温烘干即可。</p>','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=homemade%20dog%20treats%20chicken%20jerky%20on%20plate%20food%20photography&image_size=square_hd','日常',20,5,1);
