-- 初始化种子数据
USE `lingli_dev`;

-- 插入城市数据
INSERT INTO `cities` (`name`, `code`, `province`, `status`) VALUES
('系统城市', 'SYSTEM', '系统', 1),
('北京市', 'BEIJING', '北京市', 1),
('上海市', 'SHANGHAI', '上海市', 1),
('广州市', 'GUANGZHOU', '广东省', 1),
('深圳市', 'SHENZHEN', '广东省', 1),
('杭州市', 'HANGZHOU', '浙江省', 1),
('南京市', 'NANJING', '江苏省', 1),
('成都市', 'CHENGDU', '四川省', 1),
('武汉市', 'WUHAN', '湖北省', 1),
('西安市', 'XIAN', '陕西省', 1);

-- 插入公司数据
INSERT INTO `companies` (`city_id`, `name`, `code`, `address`, `contact_phone`, `contact_email`, `status`) VALUES
(1, '灵力检测总部', 'LEADING_CORP', '系统默认地址', '400-000-0000', 'admin@company.com', 1),
(2, '北京灵力检测有限公司', 'BJ_LINGLI', '北京市朝阳区建国路88号', '010-88888888', 'bj@company.com', 1),
(3, '上海灵力检测中心', 'SH_LINGLI', '上海市浦东新区陆家嘴环路1000号', '021-66666666', 'sh@company.com', 1),
(4, '广州灵力医学检验所', 'GZ_LINGLI', '广州市天河区珠江新城花城大道85号', '020-88888888', 'gz@company.com', 1),
(5, '深圳灵力生物科技', 'SZ_LINGLI', '深圳市南山区深南大道10000号', '0755-88888888', 'sz@company.com', 1);

-- 插入超级管理员用户
-- 密码为: root123 (BCrypt加密后)
INSERT INTO `users` (`username`, `password`, `phone`, `email`, `status`) VALUES
('root', '$2a$10$7JB720yubVSOfvHNI8MFe.FvQZfYfO6wS3v3H1yZ1j9z8x7y6z5x4w3v', '13800000000', 'admin@company.com', 1);

-- 插入套餐分类数据
INSERT INTO `package_categories` (`name`, `code`, `parent_id`, `sort_order`, `status`) VALUES
('基础体检', 'BASIC', 0, 1, 1),
('入职体检', 'PRE_EMPLOYMENT', 1, 1, 1),
('年度体检', 'ANNUAL_CHECKUP', 1, 2, 1),
('职业健康体检', 'OCCUPATIONAL_HEALTH', 1, 3, 1),
('高端体检', 'PREMIUM', 0, 2, 1),
('精英体检', 'ELITE', 5, 1, 1),
('VIP体检', 'VIP', 5, 2, 1),
('专项体检', 'SPECIALIZED', 0, 3, 1),
('心血管体检', 'CARDIOVASCULAR', 8, 1, 1),
('肿瘤筛查', 'CANCER_SCREENING', 8, 2, 1);

-- 插入体检套餐数据
INSERT INTO `health_packages` (`category_id`, `name`, `code`, `description`, `price`, `original_price`, `sampling_method`, `report_delivery_days`, `status`) VALUES
-- 基础体检分类
(2, '基础入职体检套餐', 'BASIC_PRE_EMPLOYMENT', '包含基础检查项目，适合入职体检使用', 299.00, 399.00, 3, 3, 1),
(2, '标准入职体检套餐', 'STANDARD_PRE_EMPLOYMENT', '标准入职体检套餐，涵盖常规检查项目', 399.00, 499.00, 3, 3, 1),
(3, '基础年度体检', 'BASIC_ANNUAL', '基础年度体检，包含血常规、尿常规等', 499.00, 599.00, 3, 5, 1),
(3, '标准年度体检', 'STANDARD_ANNUAL', '标准年度体检，包含更全面的检查项目', 799.00, 999.00, 3, 5, 1),
(3, '全面年度体检', 'COMPREHENSIVE_ANNUAL', '全面年度体检，包含影像学检查', 1299.00, 1599.00, 3, 7, 1),
(4, '职业健康体检A类', 'OCCUPATIONAL_A', '职业健康体检A类，适合一般职业', 399.00, 499.00, 3, 5, 1),
(4, '职业健康体检B类', 'OCCUPATIONAL_B', '职业健康体检B类，适合特殊职业', 599.00, 699.00, 3, 7, 1),
-- 高端体检分类
(6, '精英体检套餐', 'ELITE_CHECKUP', '精英体检套餐，提供个性化健康管理', 2999.00, 3599.00, 3, 10, 1),
(7, 'VIP体检套餐', 'VIP_CHECKUP', 'VIP体检套餐，享受一对一专属服务', 5999.00, 6999.00, 3, 14, 1),
-- 专项体检分类
(9, '心血管专项体检', 'CARDIOVASCULAR_SPECIAL', '专门针对心血管系统的体检套餐', 899.00, 1099.00, 3, 7, 1),
(10, '肿瘤早期筛查套餐', 'CANCER_EARLY_SCREENING', '肿瘤早期筛查体检套餐', 1299.00, 1599.00, 3, 10, 1);

-- 插入用户公司关联数据（将root用户分配到灵力检测总部）
INSERT INTO `user_companies` (`user_id`, `company_id`, `role`) VALUES
(1, 1, 3); -- root用户为城市管理员

-- 插入示例用户数据（仅供测试）
-- 密码为: password123 (BCrypt加密后)
INSERT INTO `users` (`username`, `password`, `phone`, `email`, `status`) VALUES
('testuser', '$2a$10$fVH8e28OQRj9tqiDXs1e1u1c8i6E7Y8x9z2a1b3c4d5e6f7g8h9i0j1k2l', '13812345678', 'test@example.com', 1),
('bjadmin', '$2a$10$fVH8e28OQRj9tqiDXs1e1u1c8i6E7Y8x9z2a1b3c4d5e6f7g8h9i0j1k2l', '13912345678', 'bjadmin@company.com', 1),
('shadmin', '$2a$10$fVH8e28OQRj9tqiDXs1e1u1c8i6E7Y8x9z2a1b3c4d5e6f7g8h9i0j1k2l', '13712345678', 'shadmin@company.com', 1);

-- 插入示例用户公司关联
INSERT INTO `user_companies` (`user_id`, `company_id`, `role`) VALUES
(2, 2, 1), -- testuser 分配到北京公司，为普通用户
(3, 2, 2), -- bjadmin 分配到北京公司，为公司管理员
(4, 3, 2); -- shadmin 分配到上海公司，为公司管理员

-- 插入示例采样人数据
INSERT INTO `samplers` (`user_id`, `name`, `gender`, `age`, `phone`, `id_card`, `address`, `medical_history`) VALUES
(2, '张三', 1, 25, '13812345678', '110101199801010001', '北京市朝阳区xxx街道xxx号', '无特殊病史'),
(2, '李四', 2, 28, '13812345679', '110101199601020002', '北京市海淀区xxx街道xxx号', '高血压病史5年');

-- 插入示例购物车数据
INSERT INTO `shopping_cart` (`user_id`, `package_id`, `quantity`) VALUES
(2, 1, 1),
(2, 3, 2);

-- 插入示例订单数据
INSERT INTO `orders` (`order_no`, `user_id`, `company_id`, `total_amount`, `status`, `pay_status`, `shipping_type`, `contact_name`, `contact_phone`) VALUES
('ORD2023112800001', 2, 2, 299.00, 1, 1, 1, '张三', '13812345678'),
('ORD2023112800002', 2, 2, 798.00, 2, 3, 3, '李四', '13912345678');

-- 插入示例订单详情数据
INSERT INTO `order_items` (`order_id`, `package_id`, `package_name`, `package_price`, `quantity`, `total_price`, `sampler_name`, `sampler_phone`) VALUES
(1, 1, '基础入职体检套餐', 299.00, 1, 299.00, '张三', '13812345678'),
(2, 1, '基础入职体检套餐', 299.00, 1, 299.00, '李四', '13912345678'),
(2, 3, '基础年度体检', 499.00, 1, 499.00, '李四', '13912345678');

-- 插入示例物流数据
INSERT INTO `logistics` (`order_id`, `type`, `company_name`, `tracking_no`, `status`, `sender_name`, `receiver_name`, `receiver_phone`) VALUES
(2, 1, '顺丰快递', 'SF1234567890', 4, '灵力检测', '李四', '13912345678');

-- 插入示例检验报告数据
INSERT INTO `test_reports` (`order_id`, `order_item_id`, `sampler_id`, `file_name`, `file_url`, `file_type`, `report_type`, `uploaded_by`) VALUES
(2, 3, 3, '李四_入职体检报告.pdf', 'https://cdn.company.com/reports/report_001.pdf', 'pdf', 1, 1);

-- 更新套餐详细字段（使用JSON格式）
UPDATE `health_packages` SET 
`cover_image` = 'https://cdn.company.com/packages/basic_pre_employment.jpg',
`detail_images` = '["https://cdn.company.com/packages/detail1.jpg", "https://cdn.company.com/packages/detail2.jpg"]',
`test_items` = '["血常规", "尿常规", "胸透", "心电图", "肝功能", "肾功能"]'
WHERE `id` = 1;

UPDATE `health_packages` SET 
`cover_image` = 'https://cdn.company.com/packages/standard_pre_employment.jpg',
`detail_images` = '["https://cdn.company.com/packages/detail3.jpg", "https://cdn.company.com/packages/detail4.jpg"]',
`test_items` = '["血常规", "尿常规", "胸透", "心电图", "肝功能", "肾功能", "血糖", "血脂"]'
WHERE `id` = 2;

UPDATE `health_packages` SET 
`cover_image` = 'https://cdn.company.com/packages/basic_annual.jpg',
`detail_images` = '["https://cdn.company.com/packages/detail5.jpg", "https://cdn.company.com/packages/detail6.jpg"]',
`test_items` = '["血常规", "尿常规", "肝功能", "肾功能", "血糖", "血脂", "甲状腺功能"]'
WHERE `id` = 3;