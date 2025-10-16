package com.scenic.entity.user;

import java.time.LocalDateTime;

/**
 * 用户常用预约人实体类
 * 用于"常用预约人管理"功能
 */
public class UserFrequentMember {
    private Long id;                    // 常用预约人ID
    private Long userId;                // 用户ID
    private String name;                // 姓名
    private Byte idType;                // 证件类型：1-身份证，2-护照，3-其他
    private String idNumber;            // 证件号码
    private String phone;               // 手机号
    private Byte gender;                // 性别：1-男，2-女
    private Byte isDefault;             // 是否为默认预约人：0-否，1-是
    private Byte status;                // 状态：0-禁用，1-启用
    private Integer version;            // 版本号（乐观锁）
    private Byte deleted;               // 逻辑删除：0-未删除，1-已删除
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间
    private Long createBy;              // 创建人
    private Long updateBy;              // 更新人

    // 构造函数
    public UserFrequentMember() {}

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

    public Byte getIdType() {
        return idType;
    }

    public void setIdType(Byte idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) {
        this.gender = gender;
    }

    public Byte getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Byte isDefault) {
        this.isDefault = isDefault;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
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

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public String toString() {
        return "UserFrequentMember{" +
                "id=" + id +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", idType=" + idType +
                ", idNumber='" + idNumber + '\'' +
                ", phone='" + phone + '\'' +
                ", gender=" + gender +
                ", isDefault=" + isDefault +
                ", status=" + status +
                ", version=" + version +
                ", deleted=" + deleted +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createBy=" + createBy +
                ", updateBy=" + updateBy +
                '}';
    }
}