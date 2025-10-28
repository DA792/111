-- 初始化通知配置数据
INSERT INTO notification_config (type, name, enabled, config_data, last_update, create_time) VALUES
('sms', '短信通知', 0, NULL, NOW(), NOW()),
('email', '邮件通知', 0, NULL, NOW(), NOW()),
('wechat', '微信小程序', 0, NULL, NOW(), NOW()),
('official_account', '微信服务号', 0, NULL, NOW(), NOW());
