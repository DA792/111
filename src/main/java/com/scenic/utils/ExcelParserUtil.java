package com.scenic.utils;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.scenic.entity.appointment.ActivityAppointment;
import com.scenic.entity.appointment.TeamAppointment;

/**
 * Excel解析工具类
 */
@Component
public class ExcelParserUtil {
    
    /**
     * 解析团队预约Excel文件
     * @param file Excel文件
     * @return 团队预约列表
     * @throws Exception 解析异常
     */
    public static List<TeamAppointment> parseTeamAppointmentExcel(MultipartFile file) throws Exception {
        List<TeamAppointment> teamAppointments = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(inputStream, file.getOriginalFilename());
            Sheet sheet = workbook.getSheetAt(0); // 读取第一个工作表
            
            // 跳过表头行，从第二行开始读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                
                TeamAppointment teamAppointment = parseTeamAppointmentRow(row);
                if (teamAppointment != null) {
                    teamAppointments.add(teamAppointment);
                }
            }
            
            workbook.close();
        }
        
        return teamAppointments;
    }
    
    /**
     * 解析活动预约Excel文件
     * @param file Excel文件
     * @return 活动预约列表
     * @throws Exception 解析异常
     */
    public static List<ActivityAppointment> parseActivityAppointmentExcel(MultipartFile file) throws Exception {
        List<ActivityAppointment> activityAppointments = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = createWorkbook(inputStream, file.getOriginalFilename());
            Sheet sheet = workbook.getSheetAt(0); // 读取第一个工作表
            
            // 跳过表头行，从第二行开始读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                
                ActivityAppointment activityAppointment = parseActivityAppointmentRow(row);
                if (activityAppointment != null) {
                    activityAppointments.add(activityAppointment);
                }
            }
            
            workbook.close();
        }
        
        return activityAppointments;
    }
    
    /**
     * 根据文件名创建对应的Workbook
     * @param inputStream 文件输入流
     * @param filename 文件名
     * @return Workbook对象
     * @throws Exception 创建异常
     */
    private static Workbook createWorkbook(InputStream inputStream, String filename) throws Exception {
        if (filename != null && filename.toLowerCase().endsWith(".xlsx")) {
            return new XSSFWorkbook(inputStream);
        } else {
            return new HSSFWorkbook(inputStream);
        }
    }
    
    /**
     * 解析团队预约行数据
     * @param row Excel行
     * @return 团队预约对象
     */
    private static TeamAppointment parseTeamAppointmentRow(Row row) {
        try {
            TeamAppointment teamAppointment = new TeamAppointment();
            
            // 解析各列数据（根据实际Excel格式调整列索引）
            teamAppointment.setTeamName(getCellValueAsString(row.getCell(0)));
            teamAppointment.setContactPerson(getCellValueAsString(row.getCell(1)));
            teamAppointment.setContactPhone(getCellValueAsString(row.getCell(2)));
            teamAppointment.setContactEmail(getCellValueAsString(row.getCell(3)));
            
            // 团队人数
            String numberOfPeopleStr = getCellValueAsString(row.getCell(4));
            if (numberOfPeopleStr != null && !numberOfPeopleStr.isEmpty()) {
                try {
                    teamAppointment.setNumberOfPeople(Integer.parseInt(numberOfPeopleStr));
                } catch (NumberFormatException e) {
                    teamAppointment.setNumberOfPeople(0);
                }
            }
            
            // 景点信息
            teamAppointment.setScenicSpotName(getCellValueAsString(row.getCell(5)));
            
            // 预约时间
            String appointmentTimeStr = getCellValueAsString(row.getCell(6));
            if (appointmentTimeStr != null && !appointmentTimeStr.isEmpty()) {
                try {
                    // 尝试解析为LocalDateTime
                    teamAppointment.setAppointmentTime(LocalDateTime.parse(appointmentTimeStr));
                } catch (Exception e) {
                    // 如果解析失败，设置为当前时间
                    teamAppointment.setAppointmentTime(LocalDateTime.now());
                }
            } else {
                // 如果没有提供预约时间，设置为当前时间
                teamAppointment.setAppointmentTime(LocalDateTime.now());
            }
            
            // 备注
            teamAppointment.setRemark(getCellValueAsString(row.getCell(7)));
            
            // 设置默认状态和时间
            teamAppointment.setStatus(1); // 1表示待审核
            teamAppointment.setCreateTime(LocalDateTime.now());
            teamAppointment.setUpdateTime(LocalDateTime.now());
            
            // 验证必要字段
            if (teamAppointment.getTeamName() != null && !teamAppointment.getTeamName().isEmpty() &&
                teamAppointment.getContactPerson() != null && !teamAppointment.getContactPerson().isEmpty() &&
                teamAppointment.getContactPhone() != null && !teamAppointment.getContactPhone().isEmpty()) {
                return teamAppointment;
            }
            
        } catch (Exception e) {
            // 解析某行出错，跳过该行
            System.err.println("解析Excel行数据出错: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 解析活动预约行数据
     * @param row Excel行
     * @return 活动预约对象
     */
    private static ActivityAppointment parseActivityAppointmentRow(Row row) {
        try {
            ActivityAppointment activityAppointment = new ActivityAppointment();
            
            // 解析各列数据（根据实际Excel格式调整列索引）
            activityAppointment.setActivityName(getCellValueAsString(row.getCell(0)));
            activityAppointment.setContactPerson(getCellValueAsString(row.getCell(1)));
            activityAppointment.setContactPhone(getCellValueAsString(row.getCell(2)));
            activityAppointment.setContactEmail(getCellValueAsString(row.getCell(3)));
            
            // 活动人数
            String numberOfPeopleStr = getCellValueAsString(row.getCell(4));
            if (numberOfPeopleStr != null && !numberOfPeopleStr.isEmpty()) {
                try {
                    activityAppointment.setNumberOfPeople(Integer.parseInt(numberOfPeopleStr));
                } catch (NumberFormatException e) {
                    activityAppointment.setNumberOfPeople(0);
                }
            }
            
            // 活动时间
            String activityTimeStr = getCellValueAsString(row.getCell(5));
            activityAppointment.setActivityTime(activityTimeStr);
            
            // 备注
            activityAppointment.setRemark(getCellValueAsString(row.getCell(6)));
            
            // 设置默认状态和时间
            activityAppointment.setStatus(1); // 1表示待审核
            activityAppointment.setCreateTime(LocalDateTime.now());
            activityAppointment.setUpdateTime(LocalDateTime.now());
            
            // 验证必要字段
            if (activityAppointment.getActivityName() != null && !activityAppointment.getActivityName().isEmpty() &&
                activityAppointment.getContactPerson() != null && !activityAppointment.getContactPerson().isEmpty() &&
                activityAppointment.getContactPhone() != null && !activityAppointment.getContactPhone().isEmpty()) {
                return activityAppointment;
            }
            
        } catch (Exception e) {
            // 解析某行出错，跳过该行
            System.err.println("解析Excel行数据出错: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 获取单元格值为字符串
     * @param cell 单元格
     * @return 字符串值
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // 数字转换为字符串，去掉小数点后的.0
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
