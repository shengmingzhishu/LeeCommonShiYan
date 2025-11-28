-- 创建数据库
CREATE DATABASE IF NOT EXISTS `lingli_dev` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `lingli_dev`;

-- 创建表结构
-- 城市表
CREATE TABLE `cities` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '城市名称',
  `code` varchar(20) NOT NULL COMMENT '城市代码',
  `province` varchar(50) NOT NULL COMMENT '所属省份',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='城市表';

-- 公司表
CREATE TABLE `companies` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `city_id` bigint NOT NULL COMMENT '所属城市ID',
  `name` varchar(100) NOT NULL COMMENT '公司名称',
  `code` varchar(20) NOT NULL COMMENT '公司代码',
  `address` text COMMENT '公司地址',
  `contact_phone` varchar(20) COMMENT '联系电话',
  `contact_email` varchar(100) COMMENT '联系邮箱',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_city_id` (`city_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_companies_city_id` FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='公司表';

-- 用户表
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `last_login_at` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户资料表
CREATE TABLE `user_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(20) DEFAULT NULL COMMENT '身份证号',
  `gender` tinyint DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `address` text COMMENT '联系地址',
  `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系电话',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_id_card` (`id_card`),
  CONSTRAINT `fk_user_profiles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户资料表';

-- 采样人信息表
CREATE TABLE `samplers` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `name` varchar(50) NOT NULL COMMENT '采样人姓名',
  `gender` tinyint DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
  `age` int DEFAULT NULL COMMENT '年龄',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `id_card` varchar(20) DEFAULT NULL COMMENT '身份证号',
  `address` text COMMENT '详细地址',
  `medical_history` text COMMENT '病史信息',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_name` (`name`),
  KEY `idx_phone` (`phone`),
  CONSTRAINT `fk_samplers_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='采样人信息表';

-- 套餐分类表
CREATE TABLE `package_categories` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `code` varchar(20) NOT NULL COMMENT '分类代码',
  `parent_id` bigint DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
  `sort_order` int DEFAULT 0 COMMENT '排序顺序',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_package_categories_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `package_categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='套餐分类表';

-- 体检套餐表
CREATE TABLE `health_packages` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` bigint NOT NULL COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '套餐名称',
  `code` varchar(20) NOT NULL COMMENT '套餐代码',
  `description` text COMMENT '套餐描述',
  `price` decimal(10,2) NOT NULL COMMENT '套餐价格',
  `original_price` decimal(10,2) DEFAULT NULL COMMENT '原价',
  `cover_image` varchar(255) DEFAULT NULL COMMENT '封面图片',
  `detail_images` json DEFAULT NULL COMMENT '详情图片列表',
  `test_items` json DEFAULT NULL COMMENT '检测项目列表',
  `sampling_method` tinyint NOT NULL COMMENT '采样方式：1-自采样，2-上门采样，3-两种方式',
  `report_delivery_days` int DEFAULT 7 COMMENT '报告交付天数',
  `stock` int DEFAULT 9999 COMMENT '库存数量',
  `sort_order` int DEFAULT 0 COMMENT '排序顺序',
  `status` tinyint DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`),
  FULLTEXT KEY `idx_search` (`name`,`description`),
  CONSTRAINT `fk_health_packages_category_id` FOREIGN KEY (`category_id`) REFERENCES `package_categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='体检套餐表';

-- 用户公司关联表
CREATE TABLE `user_companies` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `company_id` bigint NOT NULL COMMENT '公司ID',
  `role` tinyint DEFAULT 1 COMMENT '用户角色：1-普通用户，2-公司管理员，3-城市管理员',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_company` (`user_id`,`company_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_role` (`role`),
  CONSTRAINT `fk_user_companies_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_companies_company_id` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户公司关联表';

-- 购物车表
CREATE TABLE `shopping_cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `package_id` bigint NOT NULL COMMENT '套餐ID',
  `quantity` int DEFAULT 1 COMMENT '数量',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_package` (`user_id`,`package_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_package_id` (`package_id`),
  CONSTRAINT `fk_shopping_cart_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_shopping_cart_package_id` FOREIGN KEY (`package_id`) REFERENCES `health_packages` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- 订单表
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(32) NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `company_id` bigint NOT NULL COMMENT '所属公司ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `paid_amount` decimal(10,2) DEFAULT 0.00 COMMENT '已支付金额',
  `status` tinyint DEFAULT 1 COMMENT '订单状态：1-待支付，2-已支付，3-待发货，4-已发货，5-已完成，6-已取消',
  `pay_status` tinyint DEFAULT 1 COMMENT '支付状态：1-待支付，2-支付中，3-已支付，4-支付失败',
  `pay_type` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '支付时间',
  `shipping_type` tinyint DEFAULT NULL COMMENT '配送方式：1-自邮寄，2-上门取样',
  `shipping_address` text COMMENT '配送地址',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `remark` text COMMENT '备注',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_company_id` (`company_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_orders_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_orders_company_id` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 订单详情表
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `package_id` bigint NOT NULL COMMENT '套餐ID',
  `package_name` varchar(100) NOT NULL COMMENT '套餐名称(快照)',
  `package_price` decimal(10,2) NOT NULL COMMENT '套餐单价(快照)',
  `quantity` int NOT NULL COMMENT '数量',
  `total_price` decimal(10,2) NOT NULL COMMENT '小计',
  `sampler_name` varchar(50) DEFAULT NULL COMMENT '采样人姓名',
  `sampler_id_card` varchar(20) DEFAULT NULL COMMENT '采样人身份证',
  `sampler_phone` varchar(20) DEFAULT NULL COMMENT '采样人电话',
  `sampler_address` text COMMENT '采样人地址',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_package_id` (`package_id`),
  CONSTRAINT `fk_order_items_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_items_package_id` FOREIGN KEY (`package_id`) REFERENCES `health_packages` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单详情表';

-- 物流表
CREATE TABLE `logistics` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `type` tinyint NOT NULL COMMENT '物流类型：1-设备配送，2-样品回收',
  `company_name` varchar(100) DEFAULT NULL COMMENT '物流公司名称',
  `tracking_no` varchar(50) DEFAULT NULL COMMENT '快递单号',
  `status` tinyint DEFAULT 1 COMMENT '物流状态：1-待发货，2-已发货，3-运输中，4-已签收，5-异常',
  `sender_name` varchar(50) DEFAULT NULL COMMENT '发货人姓名',
  `sender_phone` varchar(20) DEFAULT NULL COMMENT '发货人电话',
  `sender_address` text COMMENT '发货地址',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT '收货人电话',
  `receiver_address` text COMMENT '收货地址',
  `shipped_at` timestamp NULL DEFAULT NULL COMMENT '发货时间',
  `delivered_at` timestamp NULL DEFAULT NULL COMMENT '签收时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_logistics_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物流表';

-- 检验报告表
CREATE TABLE `test_reports` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_item_id` bigint NOT NULL COMMENT '订单项ID',
  `sampler_id` bigint NOT NULL COMMENT '采样人ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_url` varchar(500) NOT NULL COMMENT '文件访问URL',
  `file_type` varchar(10) NOT NULL COMMENT '文件类型：pdf,doc,docx',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小(字节)',
  `report_type` tinyint DEFAULT 1 COMMENT '报告类型：1-检验报告，2-健康建议',
  `upload_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `uploaded_by` bigint DEFAULT NULL COMMENT '上传人ID',
  `status` tinyint DEFAULT 1 COMMENT '状态：1-正常，2-已删除',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `created_by` varchar(50) DEFAULT 'system' COMMENT '创建人',
  `updated_by` varchar(50) DEFAULT 'system' COMMENT '更新人',
  `is_deleted` tinyint DEFAULT 0 COMMENT '删除标志（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_order_item_id` (`order_item_id`),
  KEY `idx_sampler_id` (`sampler_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_test_reports_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_test_reports_order_item_id` FOREIGN KEY (`order_item_id`) REFERENCES `order_items` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_test_reports_sampler_id` FOREIGN KEY (`sampler_id`) REFERENCES `samplers` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='检验报告表';

-- 创建索引
CREATE INDEX idx_users_last_login_at ON users(last_login_at);
CREATE INDEX idx_companies_city_status ON companies(city_id, status);
CREATE INDEX idx_health_packages_category_status ON health_packages(category_id, status);
CREATE INDEX idx_orders_user_status ON orders(user_id, status);
CREATE INDEX idx_orders_company_status ON orders(company_id, status);
CREATE INDEX idx_logistics_order_type ON logistics(order_id, type);
CREATE INDEX idx_test_reports_order_upload ON test_reports(order_id, upload_time);