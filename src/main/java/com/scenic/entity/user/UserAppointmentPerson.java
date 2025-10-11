package com.scenic.entity.user;

import java.time.LocalDateTime;

/**
 * 用户预约人信息实体类
 * 用于"人员管理"功能
 */
public class UserAppointmentPerson {
    private Long id;
    private Long userId; // 用户ID
    private String name; // 姓名
    private String idCard; // 身份证号
    private String phone; // 电话
    private Integer age; // 年龄
    private String relationship; // 与用户的关系
    private Boolean isDefault; // 是否为默认预约人
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 构造函数
    public UserAppointmentPerson() {}

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UserAppointmentPerson{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", idCard='" + idCard + '\'' +
                ", phone='" + phone + '\'' +
                ", age=" + age +
                ", relationship='" + relationship + '\'' +
                ", isDefault=" + isDefault +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
