-- 知识库表
CREATE TABLE IF NOT EXISTS `knowledge_base` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '知识库ID',
  `title` VARCHAR(255) NOT NULL COMMENT '知识库标题',
  `content` TEXT COMMENT '知识库内容',
  `file_path` VARCHAR(500) COMMENT '文件路径',
  `file_name` VARCHAR(255) COMMENT '文件名',
  `file_size` BIGINT COMMENT '文件大小',
  `file_type` VARCHAR(50) COMMENT '文件类型',
  `version` VARCHAR(50) COMMENT '版本号',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`),
  KEY `idx_version` (`version`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识库表';

-- 知识图谱表
CREATE TABLE IF NOT EXISTS `knowledge_graph` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '知识图谱ID',
  `species_name` VARCHAR(255) NOT NULL COMMENT '物种名称',
  `scientific_name` VARCHAR(255) COMMENT '学名',
  `description` TEXT COMMENT '描述',
  `image_url` VARCHAR(500) COMMENT '图片URL',
  `category` VARCHAR(100) COMMENT '分类',
  `habitat` VARCHAR(255) COMMENT '栖息地',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_species_name` (`species_name`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='知识图谱表';

-- AI问答记录表
CREATE TABLE IF NOT EXISTS `ai_qa_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '问答记录ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `question` TEXT NOT NULL COMMENT '问题',
  `answer` TEXT NOT NULL COMMENT '回答',
  `knowledge_base_id` BIGINT COMMENT '引用的知识库ID',
  `knowledge_graph_id` BIGINT COMMENT '引用的知识图谱ID',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0-失败，1-成功）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_knowledge_base_id` (`knowledge_base_id`),
  KEY `idx_knowledge_graph_id` (`knowledge_graph_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI问答记录表';
