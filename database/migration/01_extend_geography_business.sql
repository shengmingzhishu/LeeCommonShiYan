-- 扩展数据库表结构，支持地理位置和复杂业务功能
USE `lingli_dev`;

-- 用户地址表
CREATE TABLE `user_addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(100) NOT NULL COMMENT '收货人姓名',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `province` varchar(50) NOT NULL COMMENT '省份',
  `city` varchar(50) NOT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `address` text NOT NULL COMMENT '详细地址',
  `is_default` tinyint DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
  `address_type` varchar(20) DEFAULT 'shipping' COMMENT '地址类型：shipping-收货地址,sampling-采样地址',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_city` (`city`),
  KEY `idx_is_default` (`is_default`),
  CONSTRAINT `fk_user_addresses_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地址表';

-- 用户地理位置记录表
CREATE TABLE `user_locations` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `province` varchar(50) NOT NULL COMMENT '省份',
  `city` varchar(50) NOT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度',
  `ip_address` varchar(45) DEFAULT NULL COMMENT 'IP地址',
  `location_source` varchar(20) DEFAULT 'user_select' COMMENT '位置来源：ip-IP地址定位,user_select-用户选择,gps-GPS定位',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_city` (`city`),
  KEY `idx_ip_address` (`ip_address`),
  CONSTRAINT `fk_user_locations_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户地理位置记录表';

-- 购物车表扩展（增加取样人信息）
ALTER TABLE `shopping_cart` ADD COLUMN `sampler_id` bigint DEFAULT NULL COMMENT '采样人ID';
ALTER TABLE `shopping_cart` ADD COLUMN `sampling_method` tinyint DEFAULT NULL COMMENT '采样方式：1-自邮寄，2-上门预约';

-- 订单地址信息表
CREATE TABLE `order_addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `address_type` varchar(20) NOT NULL COMMENT '地址类型：shipping-收货地址,sampling-采样地址',
  `name` varchar(100) NOT NULL COMMENT '联系人姓名',
  `phone` varchar(20) NOT NULL COMMENT '联系电话',
  `province` varchar(50) NOT NULL COMMENT '省份',
  `city` varchar(50) NOT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `address` text NOT NULL COMMENT '详细地址',
  `latitude` decimal(10,7) DEFAULT NULL COMMENT '纬度（用于上门服务）',
  `longitude` decimal(10,7) DEFAULT NULL COMMENT '经度（用于上门服务）',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_address_type` (`address_type`),
  CONSTRAINT `fk_order_addresses_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单地址信息表';

-- 预约上门取样表
CREATE TABLE `sampling_appointments` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_item_id` bigint NOT NULL COMMENT '订单项ID',
  `appointment_date` date NOT NULL COMMENT '预约日期',
  `appointment_time_slot` varchar(50) NOT NULL COMMENT '预约时间段：09:00-12:00,14:00-17:00,18:00-21:00',
  `appointment_address` text NOT COMMENT '预约地址',
  `contact_name` varchar(100) NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `remark` text COMMENT '备注信息',
  `status` tinyint DEFAULT 1 COMMENT '预约状态：1-待确认，2-已确认，3-已完成，4-已取消',
  `confirmed_by` varchar(50) DEFAULT NULL COMMENT '确认人',
  `confirmed_at` timestamp NULL DEFAULT NULL COMMENT '确认时间',
  `sampled_by` varchar(50) DEFAULT NULL COMMENT '采样人员',
  `sampled_at` timestamp NULL DEFAULT NULL COMMENT '采样完成时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_appointment_date` (`appointment_date`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_sampling_appointments_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_sampling_appointments_order_item_id` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约上门取样表';

-- 利益分成表
CREATE TABLE `revenue_distribution` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `company_amount` decimal(10,2) NOT NULL COMMENT '公司分成金额',
  `platform_amount` decimal(10,2) NOT NULL COMMENT '平台分成金额',
  `distribution_ratio` decimal(5,2) NOT NULL COMMENT '公司分成比例%',
  `city_id` bigint NOT NULL COMMENT '城市ID',
  `company_id` bigint NOT NULL COMMENT '公司ID',
  `settlement_status` tinyint DEFAULT 1 COMMENT '结算状态：1-待结算，2-已结算',
  `settlement_date` timestamp NULL DEFAULT NULL COMMENT '结算时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_city_id` (`city_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_settlement_status` (`settlement_status`),
  CONSTRAINT `fk_revenue_distribution_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_revenue_distribution_city_id` FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_revenue_distribution_company_id` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='利益分成表';

-- 支付记录表
CREATE TABLE `payment_records` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `payment_no` varchar(32) NOT NULL COMMENT '支付单号',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `payment_type` varchar(20) NOT NULL COMMENT '支付方式：wechat-微信支付,alipay-支付宝,unionpay-银联支付',
  `payment_channel` varchar(20) NOT NULL COMMENT '支付渠道：mp_weixin-微信小程序,h5-h5页面,pc-网页版',
  `status` tinyint DEFAULT 1 COMMENT '支付状态：1-待支付，2-支付中，3-支付成功，4-支付失败，5-已退款',
  `trade_no` varchar(64) DEFAULT NULL COMMENT '第三方交易号',
  `notify_data` json DEFAULT NULL COMMENT '支付回调数据',
  `paid_at` timestamp NULL DEFAULT NULL COMMENT '支付成功时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_payment_type` (`payment_type`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_payment_records_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- 更新订单表，增加新字段
ALTER TABLE `orders` ADD COLUMN `province` varchar(50) DEFAULT NULL COMMENT '省份';
ALTER TABLE `orders` ADD COLUMN `city` varchar(50) DEFAULT NULL COMMENT '城市';
ALTER TABLE `orders` ADD COLUMN `district` varchar(50) DEFAULT NULL COMMENT '区县';
ALTER TABLE `orders` ADD COLUMN `payment_id` bigint DEFAULT NULL COMMENT '支付记录ID';
ALTER TABLE `orders` ADD COLUMN `sampling_status` tinyint DEFAULT 1 COMMENT '采样状态：1-待采样，2-已预约，3-已采样，4-已送检';
ALTER TABLE `orders` ADD COLUMN `appointment_id` bigint DEFAULT NULL COMMENT '预约ID';

-- 更新套餐表，支持邮寄方式配置
ALTER TABLE `health_packages` ADD COLUMN `shipping_methods` json DEFAULT NULL COMMENT '支持的邮寄方式：["mailing","appointment"]';

-- 创建索引
CREATE INDEX idx_user_addresses_user_city ON user_addresses(user_id, city);
CREATE INDEX idx_user_locations_city_updated ON user_locations(city, updated_at);
CREATE INDEX idx_sampling_appointments_date_status ON sampling_appointments(appointment_date, status);
CREATE INDEX idx_payment_records_order_status ON payment_records(order_id, status);
CREATE INDEX idx_orders_city_status ON orders(city, status);
CREATE INDEX idx_revenue_distribution_company_status ON revenue_distribution(company_id, settlement_status);