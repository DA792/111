-- 文件资源表（resource_file）
CREATE TABLE `resource_file` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `file_key` varchar(500) NOT NULL COMMENT 'MinIO存储路径',
  `bucket_name` varchar(50) NOT NULL COMMENT '存储桶名称',
  `file_size` bigint COMMENT '文件大小(字节)',
  `mime_type` varchar(100) COMMENT '文件类型',
  `file_type` tinyint NOT NULL COMMENT '1-图片 2-视频 3-文档 4-其他',
  `width` smallint COMMENT '图片/视频宽度',
  `height` smallint COMMENT '图片/视频高度',
  `duration` int COMMENT '视频/音频时长(秒)',
  `sha256` char(64) COMMENT '文件哈希值',
  `upload_user_id` bigint COMMENT '上传用户ID',
  `is_temp` tinyint DEFAULT 0 COMMENT '是否临时文件',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_key` (`bucket_name`, `file_key`),
  KEY `idx_upload_user` (`upload_user_id`),
  KEY `idx_file_type` (`file_type`)
) ENGINE=InnoDB COMMENT='统一文件存储表';

-- 保护区大事件表
CREATE TABLE protected_area_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '大事件ID',
    title VARCHAR(255) NOT NULL COMMENT '大事件标题',
    summary VARCHAR(500) COMMENT '大事件摘要',
    content_type TINYINT DEFAULT 1 COMMENT '内容类型：1-纯文本，2-Markdown',
    content TEXT COMMENT '大事件详细内容（纯文本或Markdown格式）',
    content_file_id BIGINT COMMENT 'Markdown文件ID（关联resource_file表）',
    event_date DATE NOT NULL COMMENT '大事件日期',
    event_year INT GENERATED ALWAYS AS (YEAR(event_date)) STORED COMMENT '大事件年份',
    event_month INT GENERATED ALWAYS AS (MONTH(event_date)) STORED COMMENT '大事件月份',
    cover_image_id BIGINT COMMENT '封面图片ID',
    cover_video_id BIGINT COMMENT '封面视频ID',
    location VARCHAR(255) COMMENT '事件发生地点',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_event_date (event_date),
    INDEX idx_event_year_month (event_year, event_month),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保护区大事件表';

-- 保护区大事件媒体表（用于存储大事件的图片、视频等媒体资源）
CREATE TABLE protected_area_event_media (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '媒体ID',
    event_id BIGINT NOT NULL COMMENT '大事件ID',
    file_id BIGINT NOT NULL COMMENT '文件资源ID',
    thumbnail_id BIGINT COMMENT '缩略图文件ID（针对视频）',
    title VARCHAR(255) COMMENT '媒体标题',
    description VARCHAR(500) COMMENT '媒体描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_highlight TINYINT(1) DEFAULT 0 COMMENT '是否为精彩瞬间：0-否，1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_event_id (event_id),
    INDEX idx_file_id (file_id),
    INDEX idx_is_highlight (is_highlight)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保护区大事件媒体表';


-- 保护区大事件讲解表（用于存储大事件的相关讲解内容）
CREATE TABLE protected_area_event_explanation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '讲解ID',
    event_id BIGINT NOT NULL COMMENT '大事件ID',
    title VARCHAR(255) NOT NULL COMMENT '讲解标题',
    content TEXT NOT NULL COMMENT '讲解内容',
    author VARCHAR(100) COMMENT '作者',
    image_id BIGINT COMMENT '配图文件ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_event_id (event_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保护区大事件讲解表';

-- 保护区大事件讲解对象表（用于存储大事件讲解中的不同对象，如不同的鸟类）
CREATE TABLE protected_area_event_explanation_object (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '讲解对象ID',
    explanation_id BIGINT NOT NULL COMMENT '讲解ID',
    object_name VARCHAR(255) NOT NULL COMMENT '对象名称（如：黑嘴天鹅）',
    object_description TEXT COMMENT '对象描述',
    image_id BIGINT COMMENT '对象图片文件ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_explanation_id (explanation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保护区大事件讲解对象表';

-- 保护区大事件讲解对象音频表（用于存储不同对象的多语言音频）
CREATE TABLE protected_area_event_explanation_audio (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '音频ID',
    object_id BIGINT NOT NULL COMMENT '讲解对象ID',
    language VARCHAR(50) NOT NULL DEFAULT 'zh_CN' COMMENT '语言代码：zh_CN-中文，en_US-英语，ja_JP-日语，ko_KR-韩语等',
    audio_id BIGINT NOT NULL COMMENT '音频文件ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_object_id (object_id),
    INDEX idx_language (language),
    UNIQUE KEY uk_object_language (object_id, language)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保护区大事件讲解对象音频表';



-- 用户搜索历史表
CREATE TABLE user_search_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '历史ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    keyword VARCHAR(100) NOT NULL COMMENT '搜索关键词',
    search_count INT DEFAULT 1 COMMENT '搜索次数',
    last_search_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '最后搜索时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_keyword (keyword),
    INDEX idx_last_search_time (last_search_time),
    UNIQUE KEY uk_user_keyword (user_id, keyword)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户搜索历史表';

-- 注意：使用逻辑外键，不添加物理外键约束
-- 相关字段已添加索引以提高查询性能
