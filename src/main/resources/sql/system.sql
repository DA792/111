-- 通知表
CREATE TABLE `notification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `title` varchar(255) NOT NULL COMMENT '通知标题',
  `content` text COMMENT '通知内容',
  `channel` varchar(50) NOT NULL COMMENT '通知渠道（短信/邮件/小程序/服务号）',
  `receiver_id` bigint(20) DEFAULT NULL COMMENT '接收人ID（用户ID或角色ID）',
  `receiver_type` varchar(20) DEFAULT NULL COMMENT '接收人类型（用户/角色）',
  `send_status` tinyint(4) DEFAULT '0' COMMENT '发送状态（0-未发送，1-已发送，2-发送失败）',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`),
  KEY `idx_channel` (`channel`),
  KEY `idx_send_status` (`send_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 主题设置表
CREATE TABLE `theme_setting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主题设置ID',
  `theme_name` varchar(100) NOT NULL COMMENT '主题名称',
  `color_config` text COMMENT '主题色彩配置（JSON格式）',
  `is_default` tinyint(4) DEFAULT '0' COMMENT '是否为默认主题（0-否，1-是）',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_theme_name` (`theme_name`),
  KEY `idx_is_default` (`is_default`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='主题设置表';
