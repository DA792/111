-- 团队预约表格模板表
CREATE TABLE team_appointment_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '模板ID',
    template_name VARCHAR(255) NOT NULL COMMENT '模板名称',
    template_file_id BIGINT NOT NULL COMMENT '模板文件ID（关联resource_file表）',
    description VARCHAR(500) COMMENT '模板描述',
    is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认模板：0-否，1-是',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_is_default (is_default),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队预约表格模板表';

-- 团队预约表
CREATE TABLE team_appointment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预约ID',
    appointment_no VARCHAR(50) NOT NULL COMMENT '预约编号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    appointment_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '预约时间',
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
    UNIQUE KEY uk_appointment_no (appointment_no),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_appointment_time (appointment_time),
    INDEX idx_team_name (team_name),
    INDEX idx_team_leader (team_leader),
    INDEX idx_contact_phone (contact_phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队预约表';

-- 团队预约设置表
CREATE TABLE team_appointment_setting (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设置ID',
    min_advance_days INT DEFAULT 3 COMMENT '最少提前预约天数',
    max_team_size_small INT DEFAULT 10 COMMENT '小团队最大人数',
    min_team_size_large INT DEFAULT 10 COMMENT '大团队最小人数',
    appointment_notes TEXT COMMENT '预约须知',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队预约设置表';

-- 注意：使用逻辑外键，不添加物理外键约束
-- 相关字段已添加索引以提高查询性能
