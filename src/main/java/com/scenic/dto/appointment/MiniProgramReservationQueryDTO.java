package com.scenic.dto.appointment;

/**
 * 小程序端预约查询DTO类
 */
public class MiniProgramReservationQueryDTO {
    private String idNumber;    // 证件号码
    private Integer status;     // 预约状态
    
    // 构造函数
    public MiniProgramReservationQueryDTO() {}
    
    public MiniProgramReservationQueryDTO(String idNumber, Integer status) {
        this.idNumber = idNumber;
        this.status = status;
    }
    
    // Getter 和 Setter 方法
    public String getIdNumber() {
        return idNumber;
    }
    
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "MiniProgramReservationQueryDTO{" +
                "idNumber='" + idNumber + '\'' +
                ", status=" + status +
                '}';
    }
}