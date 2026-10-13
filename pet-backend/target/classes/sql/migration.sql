-- ============================================================
-- 新增表迁移脚本（幂等：每次启动重建演示数据）
-- ============================================================

-- 8. 领养寄养信息表（每次启动重建以保证字段完整）
DROP TABLE IF EXISTS `adoption_favorite`;
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
  `area` VARCHAR(100) DEFAULT NULL COMMENT '地区',
  `size` VARCHAR(10) DEFAULT NULL COMMENT '体型：小型/中型/大型',
  `steril` VARCHAR(10) DEFAULT NULL COMMENT '是否已绝育：是/否',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending待审核 approved已审核 rejected已拒绝 offline已下架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养寄养信息表';

-- 8.1 领养寄养收藏表
CREATE TABLE `adoption_favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `adoption_id` BIGINT NOT NULL COMMENT '领养寄养信息ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_adoption` (`user_id`, `adoption_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_adoption_id` (`adoption_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='领养寄养收藏表';

-- 9. 领养寄养申请表
CREATE TABLE IF NOT EXISTS `adoption_application` (
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

-- 10. 社区帖子表
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
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0下架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category` (`category`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区帖子表';

-- 11. 评论表
CREATE TABLE IF NOT EXISTS `comment` (
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

-- 12. 帖子点赞表
CREATE TABLE IF NOT EXISTS `post_like` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_user` (`post_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子点赞表';

-- 2.1 用户表扩展：手机号 / 角色（幂等）
SET @col_phone := (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'phone');
SET @sql := IF(@col_phone = 0,
  'ALTER TABLE `user` ADD COLUMN `phone` VARCHAR(20) DEFAULT NULL COMMENT ''手机号'' AFTER `avatar`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_role := (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user' AND COLUMN_NAME = 'role');
SET @sql := IF(@col_role = 0,
  'ALTER TABLE `user` ADD COLUMN `role` VARCHAR(20) NOT NULL DEFAULT ''user'' COMMENT ''角色：user普通用户 vipVIP用户'' AFTER `phone`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.2 宠物档案扩展：体重/疫苗/绝育/健康状态（幂等）
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pet_archive' AND COLUMN_NAME = 'weight');
SET @sql := IF(@c = 0,
  'ALTER TABLE `pet_archive` ADD COLUMN `weight` VARCHAR(20) DEFAULT NULL COMMENT ''体重'' AFTER `gender`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pet_archive' AND COLUMN_NAME = 'vaccine_status');
SET @sql := IF(@c = 0,
  'ALTER TABLE `pet_archive` ADD COLUMN `vaccine_status` VARCHAR(50) DEFAULT NULL COMMENT ''疫苗状态'' AFTER `weight`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pet_archive' AND COLUMN_NAME = 'sterilized');
SET @sql := IF(@c = 0,
  'ALTER TABLE `pet_archive` ADD COLUMN `sterilized` VARCHAR(10) DEFAULT NULL COMMENT ''是否绝育：是/否'' AFTER `vaccine_status`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pet_archive' AND COLUMN_NAME = 'health_status');
SET @sql := IF(@c = 0,
  'ALTER TABLE `pet_archive` ADD COLUMN `health_status` VARCHAR(20) NOT NULL DEFAULT ''良好'' COMMENT ''健康状况：良好/轻微异常/异常'' AFTER `sterilized`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2.3 宠物健康记录表
CREATE TABLE IF NOT EXISTS `pet_health_record` (
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

-- 12.1 用户关注表
CREATE TABLE IF NOT EXISTS `user_follow` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '关注者用户ID',
  `follow_user_id` BIGINT NOT NULL COMMENT '被关注用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_follow` (`user_id`, `follow_user_id`),
  KEY `idx_follow_user` (`follow_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- 13. 商品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `merchant_id` BIGINT DEFAULT NULL COMMENT '商家ID，NULL表示管理员发布',
  `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
  `description` TEXT COMMENT '商品描述',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  `images` TEXT COMMENT '轮播图URL，逗号分隔',
  `category` VARCHAR(20) DEFAULT NULL COMMENT '分类：食品/玩具/用品/医药',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1上架 0下架',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_merchant_id` (`merchant_id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 14. 购物车表
CREATE TABLE IF NOT EXISTS `cart` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  UNIQUE KEY `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 15. 订单表
CREATE TABLE IF NOT EXISTS `order` (
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

-- 16. 订单明细表
CREATE TABLE IF NOT EXISTS `order_item` (
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

-- ============ 演示数据（每次启动重建） ============

-- 2.4 pet_info 表加 view_count/author 列（幂等）
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pet_info' AND COLUMN_NAME = 'view_count');
SET @sql := IF(@c = 0,
  'ALTER TABLE `pet_info` ADD COLUMN `view_count` INT NOT NULL DEFAULT 0 COMMENT ''浏览量'' AFTER `status`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pet_info' AND COLUMN_NAME = 'author');
SET @sql := IF(@c = 0,
  'ALTER TABLE `pet_info` ADD COLUMN `author` VARCHAR(50) DEFAULT ''小知宠'' COMMENT ''作者'' AFTER `view_count`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 补充资讯演示数据浏览量与作者
UPDATE `pet_info` SET `view_count` = 2853, `author` = '小知宠' WHERE `title` LIKE '%狗狗换粮%' AND `view_count` = 0;
UPDATE `pet_info` SET `view_count` = 4201, `author` = '小知宠' WHERE `title` LIKE '%猫咪常见疾病%' AND `view_count` = 0;
UPDATE `pet_info` SET `view_count` = 3672, `author` = '小知宠' WHERE `title` LIKE '%训练狗狗%' AND `view_count` = 0;
UPDATE `pet_info` SET `view_count` = 5128, `author` = '小知宠' WHERE `title` LIKE '%饮食禁忌%' AND `view_count` = 0;
UPDATE `pet_info` SET `view_count` = 2834, `author` = '小知宠' WHERE `title` LIKE '%体检%' AND `view_count` = 0;
UPDATE `pet_info` SET `view_count` = 3687, `author` = '小知宠' WHERE `title` LIKE '%夏季%' AND `view_count` = 0;

-- 2.5 知识记录表
CREATE TABLE IF NOT EXISTS `knowledge_record` (
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

-- 2.6 product 表加 sales 列（幂等）
SET @c := (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product' AND COLUMN_NAME = 'sales');
SET @sql := IF(@c = 0,
  'ALTER TABLE `product` ADD COLUMN `sales` INT NOT NULL DEFAULT 0 COMMENT ''销量'' AFTER `status`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 知识记录演示数据（每次启动重建）
DELETE FROM `knowledge_record`;
INSERT INTO `knowledge_record` (`title`,`excerpt`,`category`,`thumbnail`,`tags`,`pet_type`,`view_count`,`collect_count`,`share_count`,`status`,`author`) VALUES
('狗狗常见的皮肤病及预防方法','狗狗皮肤病类型包括真菌感染、细菌感染、寄生虫性皮炎和过敏性皮炎。定期驱虫、保持毛发干燥、使用宠物专用沐浴露可有效预防。','健康护理','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=golden%20retriever%20dog%20skin%20examination%20at%20vet%20clinic%20professional%20pet%20photography&image_size=square_hd','皮肤病,驱虫,真菌感染','狗',2948,186,42,1,'小知宠'),
('猫咪科学喂养指南：不同年龄段的饮食需求','幼猫期需要高蛋白高热量食物，成猫期要控制热量摄入，老年猫需减少脂肪增加纤维素。每个阶段的营养需求大不相同。','饮食营养','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cat%20eating%20from%20food%20bowl%20different%20life%20stages%20nutrition%20pet%20photography&image_size=square_hd','猫粮,营养,幼猫,老年猫','猫',1987,165,45,1,'小知宠'),
('猫咪常见传染病的症状与防治','猫瘟、猫鼻支、猫杯状病毒是最常见的三种传染病。疫苗接种是最有效的预防手段，幼猫8周龄开始接种。','疾病防治','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cat%20with%20cone%20collar%20at%20veterinary%20clinic%20professional%20pet%20photography&image_size=square_hd','疫苗接种,猫瘟,传染病','猫',1765,142,38,1,'小知宠'),
('如何训练狗狗定点排便','定点排便训练需要耐心和一致性。选择固定地点、固定时间引导狗狗排便，成功后及时奖励，逐步建立条件反射。','行为训练','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=white%20dog%20being%20trained%20to%20go%20potty%20on%20pad%20positive%20reinforcement%20pet%20photography&image_size=square_hd','定点排便,训练,幼犬','狗',1432,128,35,1,'小知宠'),
('猫咪毛发护理全攻略','长毛猫每日梳毛防打结，短毛猫每周2-3次。定期化毛膏预防毛球症，洗澡后必须彻底吹干。','日常护理','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cat%20being%20groomed%20brushing%20fur%20at%20home%20professional%20pet%20photography&image_size=square_hd','毛发护理,梳毛,化毛膏','猫',1218,98,28,1,'小知宠'),
('狗狗疫苗接种全攻略','幼犬6-8周龄开始接种第一针疫苗，间隔3-4周连续接种3针。核心疫苗包括犬瘟热、细小病毒、腺病毒和狂犬疫苗。','疾病防治','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=dog%20receiving%20vaccine%20shot%20at%20vet%20clinic%20professional%20pet%20photography&image_size=square_hd','疫苗接种,犬瘟热,细小病毒','狗',1623,156,48,1,'小知宠'),
('猫咪绝育的最佳时机及注意事项','母猫6月龄以上、公猫8月龄以上可绝育。绝育前需禁食8小时，术后戴伊丽莎白圈7-10天。','健康护理','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cat%20after%20spay%20surgery%20wearing%20cone%20resting%20at%20home%20pet%20photography&image_size=square_hd','绝育,母猫,公猫','猫',996,87,32,1,'小知宠'),
('宠物换粮的正确方法与注意事项','换粮应采用7日过渡法，逐步增加新粮比例。突然换粮容易导致腹泻和肠胃不适，尤其是幼犬幼猫。','饮食营养','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20food%20transition%20mixing%20old%20and%20new%20kibble%20bowl%20photography&image_size=square_hd','换粮,肠胃健康,7日过渡法','全部',854,76,25,1,'小知宠'),
('猫咪洗澡的注意事项与步骤','室内猫1-2月洗一次即可。水温38-40度，从尾部开始淋湿，避免头部。洗后立即吹干防止感冒。','日常护理','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cat%20being%20bathed%20in%20sink%20with%20warm%20water%20pet%20photography&image_size=square_hd','洗澡,毛发护理','猫',754,62,18,1,'小知宠');

DELETE FROM `product`;
INSERT INTO `product` (`name`,`description`,`price`,`stock`,`cover_image`,`category`,`status`,`sales`) VALUES
('皇家狗粮 15kg','全价成犬粮，营养均衡，适合中大型犬。',298.00,100,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=premium%20dog%20food%20bag%2015kg%20on%20white%20background%20product%20photography&image_size=square_hd','食品',1,1256),
('猫砂 10L 膨润土','高效结团，低尘除臭，猫咪如厕好伴侣。',49.90,200,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cat%20litter%2010L%20bag%20bentonite%20on%20white%20background%20product%20photography&image_size=square_hd','用品',1,982),
('宠物逗猫棒','弹性钢丝+羽毛铃铛，激发猫咪捕猎天性。',19.90,500,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cat%20teaser%20wand%20toy%20with%20feather%20on%20white%20background%20product%20photography&image_size=square_hd','玩具',1,654),
('狗狗磨牙棒 10支装','天然牛皮制作，清洁牙齿，缓解幼犬换牙期不适。',39.90,150,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=dog%20chew%20bones%2010%20pack%20on%20white%20background%20product%20photography&image_size=square_hd','食品',1,1203),
('猫爬架 多层','剑麻抓柱+毛绒窝，满足猫咪攀爬磨爪需求。',268.00,50,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=multi%20level%20cat%20tree%20tower%20with%20scratching%20posts%20on%20white%20background%20product%20photography&image_size=square_hd','用品',1,743),
('宠物驱虫药 体内外同驱','一月一次，广谱驱虫，安全有效。',89.00,300,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20deworming%20medicine%20box%20on%20white%20background%20product%20photography&image_size=square_hd','医药',1,456),
('幼犬专用狗粮 2.5kg','专为幼犬设计，高蛋白易消化，含DHA促进大脑发育。',89.00,156,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=puppy%20food%20bag%202.5kg%20on%20white%20background%20product%20photography&image_size=square_hd','食品',1,1256),
('猫咪营养罐头 85g*12','精选金枪鱼，高蛋白低脂肪，适口性极佳。',69.00,243,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cat%20canned%20food%2012%20pack%20on%20white%20background%20product%20photography&image_size=square_hd','食品',1,982),
('宠物窝 舒适款','柔软棉麻面料，可拆洗设计，四季通用。',129.00,87,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20bed%20cushion%20comfortable%20on%20white%20background%20product%20photography&image_size=square_hd','用品',1,654),
('猫咪玩具 鱼形','内含猫薄荷，仿真鱼造型，耐咬耐磨。',39.00,320,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cat%20fish%20toy%20with%20catnip%20on%20white%20background%20product%20photography&image_size=square_hd','玩具',1,1203),
('宠物沐浴露 500ml','植物精华，温和不刺激，深层清洁持久留香。',59.00,176,'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=pet%20shampoo%20bottle%20500ml%20on%20white%20background%20product%20photography&image_size=square_hd','医药',0,743);

DELETE FROM `adoption`;
INSERT INTO `adoption` (`user_id`,`type`,`pet_name`,`breed`,`pet_type`,`age`,`gender`,`photo_url`,`description`,`contact`,`area`,`size`,`steril`,`status`) VALUES
(1,'adoption','小橘','橘猫','猫','2个月','公','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=orange%20tabby%20kitten%20sitting%20looking%20up%20cute%20pet%20photography&image_size=square_hd','活泼亲人的小橘猫，已驱虫，希望找到有爱心的主人。','微信：petlover001','广州市 天河区','小型','否','approved'),
(1,'foster','豆豆','泰迪','狗','3岁','母','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=cute%20brown%20toy%20poodle%20dog%20sitting%20smiling%20pet%20photography&image_size=square_hd','因出差两周，寻找有经验的寄养家庭，豆豆很乖不拆家。','电话：13800138000','深圳市 南山区','小型','是','approved'),
(1,'adoption','小白','比熊','狗','1岁','公','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=white%20bichon%20frise%20dog%20portrait%20smiling%20pet%20photography&image_size=square_hd','温顺粘人的比熊，因工作原因无法继续饲养，望好心人领养。','微信：bichon_home','杭州市 西湖区','小型','是','approved');

DELETE FROM `post`;
INSERT INTO `post` (`user_id`,`title`,`content`,`cover_image`,`category`,`like_count`,`comment_count`,`view_count`,`status`) VALUES
(1,'我家金毛第一次洗澡的样子太可爱了','<p>今天带金毛去宠物店洗澡，全程配合，洗完香喷喷的，毛色也更亮了！</p>','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=golden%20retriever%20dog%20after%20bath%20wet%20fur%20cute%20pet%20photography&image_size=square_hd','晒宠',12,3,356,1),
(1,'新手养猫求助：猫咪不吃饭怎么办？','<p>刚接回家的小猫两天没好好吃东西了，只喝水，精神还行，需要去医院吗？</p>','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=worried%20kitten%20not%20eating%20looking%20at%20food%20bowl%20pet%20photography&image_size=square_hd','求助',5,8,182,1),
(1,'分享：自制狗狗零食教程','<p>用鸡胸肉和南瓜做的烘干零食，我家狗超爱吃，无添加更健康。做法很简单，切成薄片低温烘干即可。</p>','https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=homemade%20dog%20treats%20chicken%20jerky%20on%20plate%20food%20photography&image_size=square_hd','日常',20,5,428,1);

-- ============ 商家表 ============
DROP TABLE IF EXISTS `merchant`;
CREATE TABLE `merchant` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `account` VARCHAR(50) NOT NULL COMMENT '商家账号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt）',
  `name` VARCHAR(100) DEFAULT NULL COMMENT '商家名称',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

-- 默认商家账号（密码 123456，由 DataInitializer 写入 BCrypt）
-- 此处仅保证表结构存在，数据由后端初始化器写入
