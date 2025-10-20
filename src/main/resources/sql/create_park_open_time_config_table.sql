-- 创建公园开放时间配置表
CREATE TABLE IF NOT EXISTS `park_open_time_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_date` date NOT NULL COMMENT '配置日期',
  `is_closed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否关闭(0-开放,1-关闭)',
  `day_type` int NOT NULL DEFAULT '1' COMMENT '日类型(1-工作日,2-周末,3-节假日)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人',
  `update_by` bigint DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_date` (`config_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公园开放时间配置表';
