package com.scenic.common.constant;

/**
 * 系统常量类
 */
public class SystemConstants {
    
    /**
     * 预约状态常量
     */
    public static final String APPOINTMENT_STATUS_PENDING = "待审核";
    public static final String APPOINTMENT_STATUS_CONFIRMED = "已确认";
    public static final String APPOINTMENT_STATUS_CANCELLED = "已取消";
    public static final String APPOINTMENT_STATUS_COMPLETED = "已完成";
    
    /**
     * 用户角色常量
     */
    public static final String USER_ROLE_ADMIN = "ADMIN";
    public static final String USER_ROLE_USER = "USER";
    public static final String USER_ROLE_GUIDE = "GUIDE";
    
    /**
     * 内容类型常量
     */
    public static final String CONTENT_TYPE_NEWS = "NEWS";
    public static final String CONTENT_TYPE_ACTIVITY = "ACTIVITY";
    public static final String CONTENT_TYPE_SCENIC_SPOT = "SCENIC_SPOT";
    
    /**
     * 互动类型常量
     */
    public static final String INTERACTION_TYPE_COMMENT = "COMMENT";
    public static final String INTERACTION_TYPE_LIKE = "LIKE";
    public static final String INTERACTION_TYPE_SHARE = "SHARE";
    
    /**
     * 默认分页参数
     */
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 文件上传相关常量
     */
    public static final String UPLOAD_PATH_IMAGE = "/uploads/images/";
    public static final String UPLOAD_PATH_VIDEO = "/uploads/videos/";
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    /**
     * 响应码常量
     */
    public static final int RESPONSE_SUCCESS = 200;
    public static final int RESPONSE_ERROR = 500;
    public static final int RESPONSE_UNAUTHORIZED = 401;
    public static final int RESPONSE_FORBIDDEN = 403;
}
