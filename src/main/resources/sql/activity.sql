-- 活动表
CREATE TABLE activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
    title VARCHAR(255) NOT NULL COMMENT '活动标题',
    summary VARCHAR(500) COMMENT '活动摘要',
    content_type TINYINT DEFAULT 1 COMMENT '内容类型：1-纯文本，2-Markdown',
    content TEXT COMMENT '活动详细内容（纯文本或Markdown格式）',
    content_file_id BIGINT COMMENT 'Markdown文件ID（关联resource_file表）',
    start_time DATETIME NOT NULL COMMENT '活动开始时间',
    end_time DATETIME NOT NULL COMMENT '活动结束时间',
    location VARCHAR(255) COMMENT '活动地点',
    location_detail VARCHAR(500) COMMENT '活动地点详情',
    min_participants INT DEFAULT 1 COMMENT '最少参与人数',
    max_participants INT COMMENT '最大参与人数',
    current_participants INT DEFAULT 0 COMMENT '当前参与人数',
    age_requirement VARCHAR(255) COMMENT '年龄要求（如：10岁及以上）',
    registration_deadline DATETIME COMMENT '报名截止时间',
    cover_image_id BIGINT COMMENT '封面图片ID（高清）',
    cover_thumbnail_id BIGINT COMMENT '封面缩略图ID（用于列表页面）',
    status TINYINT DEFAULT 1 COMMENT '活动状态：0-已取消，1-未开始，2-进行中，3-已结束',
    is_free TINYINT(1) DEFAULT 1 COMMENT '是否免费：0-收费，1-免费（默认免费）',
    is_featured TINYINT(1) DEFAULT 0 COMMENT '是否推荐：0-普通，1-推荐',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time),
    INDEX idx_status (status),
    INDEX idx_is_free (is_free),
    INDEX idx_is_featured (is_featured),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

-- 活动类别表
CREATE TABLE activity_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '类别ID',
    name VARCHAR(50) NOT NULL COMMENT '类别名称',
    description VARCHAR(255) COMMENT '类别描述',
    icon_id BIGINT COMMENT '图标文件ID',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_name (name),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动类别表';

-- 活动-类别关联表
CREATE TABLE activity_category_relation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    category_id BIGINT NOT NULL COMMENT '类别ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_activity_category (activity_id, category_id),
    INDEX idx_activity_id (activity_id),
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动-类别关联表';

-- 活动预约表格模板表
CREATE TABLE activity_registration_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    template_name VARCHAR(255) NOT NULL COMMENT '模板名称',
    template_file_id BIGINT NOT NULL COMMENT '模板文件ID（关联resource_file表）',
    description VARCHAR(500) COMMENT '模板描述',
    is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认模板：0-否，1-是',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_activity_id (activity_id),
    INDEX idx_is_default (is_default),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动预约表格模板表';

-- 活动预约表
CREATE TABLE activity_registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    registration_no VARCHAR(50) NOT NULL COMMENT '预约编号',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    activity_title VARCHAR(255) NOT NULL COMMENT '活动名称',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    registration_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
    form_file_id BIGINT NOT NULL COMMENT '填写后的表格文件ID（关联resource_file表）',
    team_name VARCHAR(100) COMMENT '团队名称（从表格中提取）',
    team_leader VARCHAR(50) COMMENT '团队负责人（从表格中提取）',
    contact_phone VARCHAR(20) COMMENT '联系电话（从表格中提取）',
    team_size INT COMMENT '团队人数（从表格中提取）',
    status TINYINT DEFAULT 1 COMMENT '预约状态：0-已取消，1-未开始，2-已完成',
    contact_email VARCHAR(100) COMMENT '联系人邮箱',
    remarks VARCHAR(500) COMMENT '备注',
    admin_remarks VARCHAR(500) COMMENT '管理员备注',
    check_in_time DATETIME COMMENT '签到时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_registration_no (registration_no),
    INDEX idx_activity_id (activity_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_registration_time (registration_time),
    INDEX idx_team_name (team_name),
    INDEX idx_team_leader (team_leader),
    INDEX idx_contact_phone (contact_phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动预约表';

-- 活动评价表
CREATE TABLE activity_review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    registration_id BIGINT COMMENT '预约ID',
    rating TINYINT NOT NULL COMMENT '评分（1-5）',
    content TEXT COMMENT '评价内容',
    is_anonymous TINYINT(1) DEFAULT 0 COMMENT '是否匿名：0-否，1-是',
    status TINYINT DEFAULT 1 COMMENT '状态：0-已删除，1-正常，2-已屏蔽',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_activity_id (activity_id),
    INDEX idx_user_id (user_id),
    INDEX idx_registration_id (registration_id),
    INDEX idx_rating (rating),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动评价表';

-- 活动通知表
CREATE TABLE activity_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    title VARCHAR(255) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    notification_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '通知时间',
    notification_type TINYINT DEFAULT 1 COMMENT '通知类型：1-活动提醒，2-活动变更，3-活动取消',
    status TINYINT DEFAULT 1 COMMENT '状态：0-已删除，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_activity_id (activity_id),
    INDEX idx_notification_time (notification_time),
    INDEX idx_notification_type (notification_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动通知表';

-- 活动用户通知关联表
CREATE TABLE activity_user_notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID',
    notification_id BIGINT NOT NULL COMMENT '通知ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
    read_time DATETIME COMMENT '阅读时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_notification_id (notification_id),
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动用户通知关联表';

-- 活动预约设置表
CREATE TABLE activity_registration_setting (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设置ID',
    activity_id BIGINT NOT NULL COMMENT '活动ID',
    registration_start_time DATETIME NOT NULL COMMENT '预约开始时间',
    registration_end_time DATETIME NOT NULL COMMENT '预约结束时间',
    max_registrations_per_user INT DEFAULT 1 COMMENT '每个用户最大预约次数',
    min_participants_per_registration INT DEFAULT 1 COMMENT '每次预约最少参与人数',
    max_participants_per_registration INT COMMENT '每次预约最大参与人数',
    cancellation_deadline DATETIME COMMENT '取消截止时间',
    refund_policy VARCHAR(500) COMMENT '退款政策',
    registration_notes TEXT COMMENT '预约须知',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_activity_id (activity_id),
    INDEX idx_registration_start_time (registration_start_time),
    INDEX idx_registration_end_time (registration_end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动预约设置表';

-- 注意：使用逻辑外键，不添加物理外键约束
-- 相关字段已添加索引以提高查询性能
