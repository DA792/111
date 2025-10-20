-- 创建保护区介绍表
CREATE TABLE IF NOT EXISTS protected_area_introduction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    content TEXT COMMENT '内容',
    language VARCHAR(20) NOT NULL DEFAULT 'zh-CN' COMMENT '语言',
    image_url VARCHAR(500) COMMENT '图片URL',
    audio_url VARCHAR(500) COMMENT '语音URL',
    video_url VARCHAR(500) COMMENT '视频URL',
    sort_order INT DEFAULT 0 COMMENT '排序',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用 1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_language (language),
    INDEX idx_enabled (enabled),
    INDEX idx_sort_order (sort_order),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='保护区介绍表';
