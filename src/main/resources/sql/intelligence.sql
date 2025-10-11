CREATE TABLE `blacklist_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `action_type` tinyint NOT NULL COMMENT '操作类型：1-加入黑名单，2-移出黑名单',
  `reason` varchar(255) COMMENT '原因',
  `operator_id` bigint COMMENT '操作人ID',
  `operator_name` varchar(50) COMMENT '操作人姓名',
  `action_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
 `create_by` bigint COMMENT '创建人',
  `update_by` bigint COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_action_type` (`action_type`),
  KEY `idx_action_time` (`action_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='黑名单记录表';