-- 创建团队成员表
CREATE TABLE IF NOT EXISTS team_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    team_appointment_id BIGINT NOT NULL COMMENT '团队预约ID',
    name VARCHAR(50) NOT NULL COMMENT '成员姓名',
    id_card VARCHAR(18) COMMENT '身份证号码',
    phone VARCHAR(20) COMMENT '手机号码',
    age INT COMMENT '年龄',
    gender VARCHAR(10) COMMENT '性别',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_team_appointment_id (team_appointment_id),
    INDEX idx_name (name),
    INDEX idx_id_card (id_card),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队成员表';
