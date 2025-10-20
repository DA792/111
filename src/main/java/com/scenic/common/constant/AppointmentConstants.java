package com.scenic.common.constant;

/**
 * 预约模块常量类
 */
public class AppointmentConstants {
    
    /**
     * 预约状态常量
     */
    public static final int STATUS_PENDING = 1;  // 待审核
    public static final int STATUS_CONFIRMED = 2;  // 已确认
    public static final int STATUS_CANCELLED = 3;  // 已取消
    public static final int STATUS_COMPLETED = 4;  // 已完成
    public static final int STATUS_REJECTED = 0;  // 已拒绝
    
    /**
     * 预约类型常量
     */
    public static final String TYPE_PERSONAL = "个人预约";
    public static final String TYPE_TEAM = "团队预约";
    public static final String TYPE_ACTIVITY = "活动预约";
    
    /**
     * 预约设置键常量
     */
    public static final String SETTING_MAX_DAILY_APPOINTMENTS = "max_daily_appointments";
    public static final String SETTING_MAX_PEOPLE_PER_APPOINTMENT = "max_people_per_appointment";
    public static final String SETTING_APPOINTMENT_TIME_SLOTS = "appointment_time_slots";
    public static final String SETTING_ALLOW_CANCEL_HOURS = "allow_cancel_hours";
    
    /**
     * 默认值常量
     */
    public static final int DEFAULT_MAX_DAILY_APPOINTMENTS = 100;
    public static final int DEFAULT_MAX_PEOPLE_PER_APPOINTMENT = 10;
    public static final int DEFAULT_ALLOW_CANCEL_HOURS = 24;
    
    /**
     * 时间格式常量
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
