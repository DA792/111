CREATE TABLE `notification_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `type` varchar(50) NOT NULL COMMENT '通知类型（email-邮件, sms-短信, wechat-微信小程序, official_account-微信服务号）',
  `name` varchar(100) NOT NULL COMMENT '通知名称',
  `enabled` tinyint(1) DEFAULT '0' COMMENT '是否启用（0-禁用，1-启用）',
  `config_data` text COMMENT '配置数据（JSON格式，存储各通知类型的特定配置）',
  `last_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知配置表';
