-- 保护区大事件表
CREATE TABLE `protected_area_event` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '事件ID',
  `title` varchar(100) NOT NULL COMMENT '事件标题',
  `content` text NOT NULL COMMENT '事件内容',
  `event_time` datetime NOT NULL COMMENT '事件发生时间',
  `event_type` tinyint NOT NULL COMMENT '事件类型：1-保护活动，2-物种观察，3-科研发现，4-其他',
  `location` varchar(255) COMMENT '事件发生地点',
  `cover_image_id` bigint COMMENT '封面图片ID（关联resource_file）',
  `view_count` int NOT NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞次数',
  `favorite_count` int NOT NULL DEFAULT 0 COMMENT '收藏次数',
  `comment_count` int NOT NULL DEFAULT 0 COMMENT '评论次数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-已发布，2-草稿，3-已下架',
  `is_top` tinyint NOT NULL DEFAULT 0 COMMENT '是否置顶：0-否，1-是',
  `is_highlight` tinyint NOT NULL DEFAULT 0 COMMENT '是否精选：0-否，1-是',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint COMMENT '创建人',
  `update_by` bigint COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_event_time` (`event_time`),
  KEY `idx_event_type` (`event_type`),
  KEY `idx_status` (`status`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_is_highlight` (`is_highlight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保护区大事件表';

-- 保护区大事件图片表
CREATE TABLE `protected_area_event_image` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `event_id` bigint NOT NULL COMMENT '事件ID',
  `file_id` bigint NOT NULL COMMENT '图片文件ID（关联resource_file）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `description` varchar(255) COMMENT '图片描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint COMMENT '创建人',
  `update_by` bigint COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_event_id` (`event_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保护区大事件图片表';

-- 保护区物种表
CREATE TABLE `protected_area_species` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '物种ID',
  `species_name` varchar(100) NOT NULL COMMENT '物种名称',
  `scientific_name` varchar(100) COMMENT '学名',
  `category` varchar(50) NOT NULL COMMENT '物种类别',
  `protection_level` varchar(50) COMMENT '保护级别',
  `description` text COMMENT '物种描述',
  `habitat` text COMMENT '栖息地',
  `behavior` text COMMENT '行为习性',
  `distribution` text COMMENT '分布范围',
  `population` varchar(255) COMMENT '种群数量',
  `threats` text COMMENT '面临威胁',
  `conservation_measures` text COMMENT '保护措施',
  `cover_image_id` bigint COMMENT '封面图片ID（关联resource_file）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-已发布，2-草稿，3-已下架',
  `version` int NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint COMMENT '创建人',
  `update_by` bigint COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_species_name` (`species_name`),
  KEY `idx_category` (`category`),
  KEY `idx_protection_level` (`protection_level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保护区物种表';

-- 保护区物种图片表
CREATE TABLE `protected_area_species_image` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `species_id` bigint NOT NULL COMMENT '物种ID',
  `file_id` bigint NOT NULL COMMENT '图片文件ID（关联resource_file）',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序顺序',
  `description` varchar(255) COMMENT '图片描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint COMMENT '创建人',
  `update_by` bigint COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_species_id` (`species_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保护区物种图片表';

-- 用户收藏表
CREATE TABLE `user_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_id` bigint NOT NULL COMMENT '收藏目标ID',
  `target_type` tinyint NOT NULL COMMENT '目标类型：1-保护区大事件，2-保护区物种，3-其他',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_target_type` (`target_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 用户评论表
CREATE TABLE `user_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_id` bigint NOT NULL COMMENT '评论目标ID',
  `target_type` tinyint NOT NULL COMMENT '目标类型：1-保护区大事件，2-保护区物种，3-其他',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID，用于回复功能',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞次数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-正常，2-已隐藏',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_target_type` (`target_type`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户评论表';

-- 用户点赞表
CREATE TABLE `user_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_id` bigint NOT NULL COMMENT '点赞目标ID',
  `target_type` tinyint NOT NULL COMMENT '目标类型：1-保护区大事件，2-保护区物种，3-评论，4-其他',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_target` (`user_id`, `target_id`, `target_type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_target_id` (`target_id`),
  KEY `idx_target_type` (`target_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户点赞表';
